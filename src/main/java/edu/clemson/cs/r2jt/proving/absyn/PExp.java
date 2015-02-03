/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.proving.absyn;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTFunction;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol.DisplayType;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol.Quantification;
import edu.clemson.cs.r2jt.proving.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving2.Utilities;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;

/**
 * <p><code>PExp</code> is the root of the prover abstract syntax tree 
 * hierarchy.  Unlike {@link edu.clemson.cs.r2jt.absyn.Exp Exp}s, 
 * <code>PExp</code>s are immutable and exist without the complications 
 * introduced by control structures.  <code>PExp</code>s exist to represent
 * mathematical expressions only.</p>
 */
public abstract class PExp {

    protected final static BindingException BINDING_EXCEPTION =
            new BindingException();

    public final int structureHash;
    public final int valueHash;

    protected final MTType myType;
    protected final MTType myTypeValue;

    private Set<String> myCachedSymbolNames = null;
    private List<PExp> myCachedFunctionApplications = null;
    private Set<PSymbol> myCachedQuantifiedVariables = null;

    public PExp(HashDuple hashes, MTType type, MTType typeValue) {
        this(hashes.structureHash, hashes.valueHash, type, typeValue);
    }

    public PExp(int structureHash, int valueHash, MTType type, MTType typeValue) {
        myType = type;
        myTypeValue = typeValue;
        this.structureHash = structureHash;
        this.valueHash = valueHash;
    }

    public abstract void accept(PExpVisitor v);

    public final MTType getType() {
        return myType;
    }

    public final MTType getTypeValue() {
        return myTypeValue;
    }

    @SuppressWarnings("unchecked")
    public PExp withTypesSubstituted(Map<MTType, MTType> substitutions) {

        TypeModifyingVisitor v = new TypeModifyingVisitor(substitutions);
        this.accept(v);

        return v.getFinalPExp();
    }

    public PExp withSiteAltered(Iterator<Integer> path, PExp newValue) {

        Deque<Integer> integerPath = new LinkedList<Integer>();
        Deque<PExp> pexpPath = new LinkedList<PExp>();

        pexpPath.push(this);
        while (path.hasNext()) {
            integerPath.push(path.next());

            pexpPath.push(pexpPath.peek().getSubExpressions().get(
                    integerPath.peek()));
        }

        pexpPath.pop();
        pexpPath.push(newValue);

        while (pexpPath.size() > 1) {
            newValue = pexpPath.pop();
            pexpPath.push(pexpPath.pop().withSubExpressionReplaced(
                    integerPath.pop(), newValue));
        }

        return pexpPath.peek();
    }

    public abstract PExp withTypeReplaced(MTType t);

    public abstract PExp withTypeValueReplaced(MTType t);

    public abstract PExp withSubExpressionReplaced(int index, PExp e);

    public PExp withSubExpressionsReplaced(Map<Integer, PExp> e) {
        PExp working = this;

        for (Map.Entry<Integer, PExp> entry : e.entrySet()) {
            working =
                    working.withSubExpressionReplaced(entry.getKey(), entry
                            .getValue());
        }

        return working;
    }

    public static PExp trueExp(TypeGraph g) {
        return new PSymbol(g.BOOLEAN, null, "true");
    }

    public abstract ImmutableList<PExp> getSubExpressions();

    public abstract PExpSubexpressionIterator getSubExpressionIterator();

    public abstract boolean isObviouslyTrue();

    public final List<PExp> splitIntoConjuncts() {
        List<PExp> conjuncts = new LinkedList<PExp>();

        splitIntoConjuncts(conjuncts);

        return conjuncts;
    }

    protected abstract void splitIntoConjuncts(List<PExp> accumulator);

    public abstract PExp flipQuantifiers();

    /**
     * <p>For testing purposes, building a PExp is a pain in the ass.  This 
     * method presents a relatively easy way of 'describing' the PExp you'd like
     * as a string, rather than resorting to building complex types and object
     * trees manually.  The string is interpreted using a stack-based, postfix
     * style language, as follows:</p>
     * 
     * <p>All tokens are separated by whitespace.  A {@link PSymbol PSymbol} can
     * be pushed on the stack using two tokens: a name, which is any sequence of
     * characters except "(" or "forall", and then a reference to one of the 
     * following built-in types:</p>
     * 
     * <ul>
     *      <li>Z</li>
     *      <li>B</li>
     *      <li>SSet</li>
     * </ul>
     * 
     * <p>So, the string "0 Z x B" would push two PSymbols on the stack: "0", of
     * type Z, and "x", of type B.</p>
     * 
     * <p>Function applications are introduced by an open paren token, followed 
     * by a name, followed by a number of parameters to be taken from the stack, 
     * a single character indicating if it is infix ("i"), outfix ("o"), or 
     * prefix ("p"), and finally the return type.  Parameters will be applied in
     * the reverse order they are taken off the stack.</p>
     * 
     * <p>So, the string "0 Z x B ( foo 2 p B" would form "foo(0, x)", where foo
     * is of type (Z * B) -> B.</p>
     * 
     * <p>Either a symbol or function can be preceded by the token "forall" to
     * flag its quantification as "for all".  So 
     * "0 Z forall x B ( foo 2 p B" would form "foo(0, x)", where "x" is a 
     * universally quantified variable.</p>
     * 
     * <p>Following parsing, there must be exactly one PExp left on the stack,
     * which will be returned.  If there are more or less, an 
     * <code>IllegalArgumentException</code> will be thrown.</p>
     * 
     * @param description
     * @return 
     */
    public static PExp buildPExp(String description, TypeGraph g) {
        Deque<PExp> stack = new LinkedList<PExp>();

        Quantification quant = Quantification.NONE;
        Iterator<String> tokens =
                Arrays.asList(description.split(" ")).iterator();
        String token;
        while (tokens.hasNext()) {
            token = tokens.next();

            if (token.equals("forall")) {
                quant = Quantification.FOR_ALL;
            }
            else if (token.equals("(")) {
                String functionName = tokens.next();
                int parameterCount = Integer.parseInt(tokens.next());
                String displayTypeDesc = tokens.next();
                String typeDesc = tokens.next();

                DisplayType displayType;
                if (displayTypeDesc.equals("i")) {
                    displayType = DisplayType.INFIX;
                }
                else if (displayTypeDesc.equals("o")) {
                    displayType = DisplayType.OUTFIX;
                }
                else if (displayTypeDesc.equals("p")) {
                    displayType = DisplayType.PREFIX;
                }
                else {
                    throw new IllegalArgumentException("Unknown display type: "
                            + displayTypeDesc);
                }

                MTType type = typeFromDesc(typeDesc, g);

                List<PExp> parameters = new LinkedList<PExp>();
                List<MTType> parameterTypes = new LinkedList<MTType>();
                for (int i = 0; i < parameterCount; i++) {
                    if (stack.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Not enough parameters on stack: "
                                        + parameterCount);
                    }

                    parameters.add(0, stack.pop());
                    parameterTypes.add(0, parameters.get(0).getType());
                }

                MTType functionType = new MTFunction(g, type, parameterTypes);

                stack.push(new PSymbol(functionType, null, functionName,
                        parameters, quant, displayType));
                quant = Quantification.NONE;
            }
            else {
                String name = token;
                MTType type = typeFromDesc(tokens.next(), g);

                stack.push(new PSymbol(type, null, name, quant));
                quant = Quantification.NONE;
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException(
                    "Must set up stack with exactly one PExp on it.  "
                            + "Left with: " + stack.size());
        }

        return stack.pop();
    }

    private static MTType typeFromDesc(String desc, TypeGraph g) {
        MTType result;

        if (desc.equals("B")) {
            result = g.BOOLEAN;
        }
        else if (desc.equals("Z")) {
            result = g.Z;
        }
        else if (desc.equals("SSet")) {
            result = g.SET;
        }
        else {
            throw new IllegalArgumentException("Unknown type: " + desc);
        }

        return result;
    }

    /**
     * <p>Simply walks the tree represented by the given <code>Exp</code> and
     * sounds the alarm if it or any sub-expression does not have a type.  As
     * a convenience, returns the same expression it is given so that it can
     * be used without introducing intermediate variables.</p>
     * 
     * @param e
     */
    public static <E extends Exp> E sanityCheckExp(E e) {

        if (e.getMathType() == null) {

            String varExpAdditional = "";
            if (e instanceof VarExp) {
                varExpAdditional =
                        " = \"" + ((VarExp) e).getName().getName() + "\", "
                                + ((VarExp) e).getName().getLocation();
            }

            throw new UnsupportedOperationException(
                    "Expression has null type.\n\n" + e + " (" + e.getClass()
                            + ")" + varExpAdditional);
        }

        for (Exp subexp : e.getSubExpressions()) {
            sanityCheckExp(subexp);
        }

        return e;
    }

    public static PExp buildPExp(Exp e) {
        PExp retval;

        e = Utilities.applyQuantification(e);

        if (e == null) {
            throw new IllegalArgumentException("Prover does not accept null "
                    + "as an expression.");
        }

        if (e instanceof FunctionExp) {
            FunctionExp eAsFunctionExp = (FunctionExp) e;

            List<PExp> arguments = new LinkedList<PExp>();
            Iterator<Exp> eArgs = eAsFunctionExp.argumentIterator();
            while (eArgs.hasNext()) {
                arguments.add(PExp.buildPExp(eArgs.next()));
            }

            retval =
                    new PSymbol(e.getMathType(), e.getMathTypeValue(),
                            fullName(eAsFunctionExp.getQualifier(),
                                    eAsFunctionExp.getName().getName()),
                            arguments, convertExpQuantification(eAsFunctionExp
                                    .getQuantification()));
        }
        else if (e instanceof PrefixExp) {
            PrefixExp eAsPrefixExp = (PrefixExp) e;

            List<PExp> arguments = new LinkedList<PExp>();
            arguments.add(PExp.buildPExp(eAsPrefixExp.getArgument()));

            retval =
                    new PSymbol(e.getMathType(), e.getMathTypeValue(),
                            eAsPrefixExp.getSymbol().getName(), arguments);
        }
        else if (e instanceof InfixExp) {
            InfixExp eAsInfixExp = (InfixExp) e;

            List<PExp> arguments = new LinkedList<PExp>();
            arguments.add(PExp.buildPExp(eAsInfixExp.getLeft()));
            arguments.add(PExp.buildPExp(eAsInfixExp.getRight()));

            retval =
                    new PSymbol(e.getMathType(), e.getMathTypeValue(),
                            eAsInfixExp.getOpName().getName(), arguments,
                            PSymbol.DisplayType.INFIX);
        }
        else if (e instanceof IsInExp) {
            IsInExp eAsIsInExp = (IsInExp) e;

            List<PExp> arguments = new LinkedList<PExp>();
            arguments.add(PExp.buildPExp(eAsIsInExp.getLeft()));
            arguments.add(PExp.buildPExp(eAsIsInExp.getRight()));

            retval =
                    new PSymbol(e.getMathType(), e.getMathTypeValue(), "is_in",
                            arguments, PSymbol.DisplayType.INFIX);
        }
        else if (e instanceof OutfixExp) {
            OutfixExp eAsOutfixExp = (OutfixExp) e;

            List<PExp> arguments = new LinkedList<PExp>();
            arguments.add(PExp.buildPExp(eAsOutfixExp.getArgument()));

            retval =
                    new PSymbol(e.getMathType(), e.getMathTypeValue(),
                            eAsOutfixExp.getLeftDelimiter(), eAsOutfixExp
                                    .getRightDelimiter(), arguments,
                            PSymbol.DisplayType.OUTFIX);
        }
        else if (e instanceof EqualsExp) {
            EqualsExp eAsEqualsExp = (EqualsExp) e;

            List<PExp> arguments = new LinkedList<PExp>();
            arguments.add(PExp.buildPExp(eAsEqualsExp.getLeft()));
            arguments.add(PExp.buildPExp(eAsEqualsExp.getRight()));

            retval =
                    new PSymbol(e.getMathType(), e.getMathTypeValue(),
                            eAsEqualsExp.getOperatorAsString(), arguments,
                            PSymbol.DisplayType.INFIX);
        }
        else if (e instanceof CharExp) {
            CharExp eAsCharExp = (CharExp) e;

            String symbol = "" + eAsCharExp.getValue();

            retval = new PSymbol(e.getMathType(), e.getMathTypeValue(), symbol);
        }
        else if (e instanceof IntegerExp) {
            IntegerExp eAsIntegerExp = (IntegerExp) e;

            String symbol = "" + eAsIntegerExp.getValue();

            retval = new PSymbol(e.getMathType(), e.getMathTypeValue(), symbol);
        }
        else if (e instanceof StringExp) {
            StringExp eAsStringExp = (StringExp) e;

            retval =
                    new PSymbol(e.getMathType(), e.getMathTypeValue(),
                            eAsStringExp.getValue());
        }
        else if (e instanceof DotExp) {
            DotExp eAsDotExp = (DotExp) e;

            String symbol = "";

            List<PExp> arguments = new LinkedList<PExp>();

            boolean first = true;
            for (Exp s : eAsDotExp.getSegments()) {
                if (!first) {
                    symbol += ".";
                }
                else {
                    first = false;
                }

                if (s instanceof FunctionExp) {
                    FunctionExp sAsFE = (FunctionExp) s;
                    symbol += sAsFE.getOperatorAsString();

                    for (Exp param : sAsFE.getParameters()) {
                        arguments.add(buildPExp(param));
                    }
                }
                else {
                    symbol += s;
                }
            }

            if (eAsDotExp.getSemanticExp() != null) {
                symbol += PExp.buildPExp(eAsDotExp.getSemanticExp());
            }

            retval =
                    new PSymbol(e.getMathType(), e.getMathTypeValue(), symbol,
                            arguments);
        }
        else if (e instanceof VarExp) {
            VarExp eAsVarExp = (VarExp) e;

            retval =
                    new PSymbol(eAsVarExp.getMathType(), eAsVarExp
                            .getMathTypeValue(), fullName(eAsVarExp
                            .getQualifier(), eAsVarExp.getName().getName()),
                            convertExpQuantification(eAsVarExp
                                    .getQuantification()));
        }
        else if (e instanceof VariableDotExp) {
            VariableDotExp eAsDotExp = (VariableDotExp) e;

            String finalName = "";
            for (Exp s : eAsDotExp.getSegments()) {
                finalName += "." + s.toString(0);
            }

            finalName += eAsDotExp.getSemanticExp().toString(0);

            retval =
                    new PSymbol(eAsDotExp.getSemanticExp().getMathType(),
                            eAsDotExp.getSemanticExp().getMathTypeValue(),
                            finalName);
        }
        else if (e instanceof LambdaExp) {
            LambdaExp eAsLambdaExp = (LambdaExp) e;

            List<PLambda.Parameter> parameters =
                    new LinkedList<PLambda.Parameter>();
            for (MathVarDec p : eAsLambdaExp.getParameters()) {
                parameters.add(new PLambda.Parameter(p.getName().getName(), p
                        .getTy().getMathTypeValue()));
            }

            retval =
                    new PLambda(new ArrayBackedImmutableList(parameters), PExp
                            .buildPExp(eAsLambdaExp.getBody()));
        }
        else if (e instanceof AlternativeExp) {
            AlternativeExp eAsAlternativeExp = (AlternativeExp) e;

            retval = new PAlternatives(eAsAlternativeExp);
        }
        else {
            throw new RuntimeException("Expressions of type " + e.getClass()
                    + " are not accepted by the prover.");
        }

        //The Analyzer doesn't work consistently.  Fail early if we don't have
        //typing information
        if (retval.getType() == null) {

            String varExpAdditional = "";
            if (e instanceof VarExp) {
                varExpAdditional =
                        " = \"" + ((VarExp) e).getName().getName() + "\", "
                                + ((VarExp) e).getName().getLocation();
            }

            throw new UnsupportedOperationException(
                    "Expression has null type.\n\n" + e + " (" + e.getClass()
                            + ")" + varExpAdditional);
        }

        return retval;
    }

    public final Map<PExp, PExp> bindTo(PExp target) throws BindingException {
        Map<PExp, PExp> bindings = new HashMap<PExp, PExp>();

        bindTo(target, bindings);

        return bindings;
    }

    public abstract void bindTo(PExp target, Map<PExp, PExp> accumulator)
            throws BindingException;

    @Override
    public int hashCode() {
        return valueHash;
    }

    public abstract PExp substitute(Map<PExp, PExp> substitutions);

    public abstract boolean containsName(String name);

    public abstract String getTopLevelOperation();

    public final Set<String> getSymbolNames() {
        if (myCachedSymbolNames == null) {
            //We're immutable, so only do this once
            myCachedSymbolNames =
                    Collections.unmodifiableSet(getSymbolNamesNoCache());
        }

        return myCachedSymbolNames;
    }

    protected abstract Set<String> getSymbolNamesNoCache();

    public final Set<PSymbol> getQuantifiedVariables() {
        if (myCachedQuantifiedVariables == null) {
            //We're immutable, so only do this once
            myCachedQuantifiedVariables =
                    Collections
                            .unmodifiableSet(getQuantifiedVariablesNoCache());
        }

        return myCachedQuantifiedVariables;
    }

    public abstract Set<PSymbol> getQuantifiedVariablesNoCache();

    public final List<PExp> getFunctionApplications() {
        if (myCachedFunctionApplications == null) {
            //We're immutable, so only do this once
            myCachedFunctionApplications = getFunctionApplicationsNoCache();
        }

        return myCachedFunctionApplications;
    }

    public abstract List<PExp> getFunctionApplicationsNoCache();

    public abstract boolean containsExistential();

    public abstract boolean isEquality();

    public abstract boolean isLiteral();

    private final static PSymbol.Quantification convertExpQuantification(int q) {

        PSymbol.Quantification retval;

        switch (q) {
        case VarExp.EXISTS:
            retval = PSymbol.Quantification.THERE_EXISTS;
            break;
        case VarExp.FORALL:
            retval = PSymbol.Quantification.FOR_ALL;
            break;
        case VarExp.NONE:
            retval = PSymbol.Quantification.NONE;
            break;
        default:
            throw new RuntimeException("Unrecognized quantification");
        }

        return retval;
    }

    private final static String fullName(PosSymbol qualifier, String name) {
        String retval;

        if (qualifier == null) {
            retval = "";
        }
        else {
            if (qualifier.getName() == null) {
                retval = "";
            }
            else {
                retval = qualifier.getName() + ".";
            }
        }

        return retval + name;
    }

    public boolean typeMatches(MTType other) {
        return other.isSubtypeOf(myType);
    }

    public boolean typeMatches(PExp other) {
        return typeMatches(other.getType());
    }

    public void processStringRepresentation(PExpVisitor visitor, Appendable a) {
        PExpTextRenderingVisitor renderer = new PExpTextRenderingVisitor(a);
        PExpVisitor finalVisitor = new NestedPExpVisitors(visitor, renderer);

        this.accept(finalVisitor);
    }

    public abstract boolean isVariable();

    public static class HashDuple {

        public int structureHash;
        public int valueHash;

        public HashDuple(int structureHash, int valueHash) {
            this.structureHash = structureHash;
            this.valueHash = valueHash;
        }
    }

    public final String toString() {
        StringWriter output = new StringWriter();
        PExpTextRenderingVisitor renderer =
                new PExpTextRenderingVisitor(output);

        this.accept(renderer);

        return output.toString();
    }

    public final String toDebugString(int indent, int offset) {
        StringBuilder b = new StringBuilder();
        if (this instanceof PSymbol) {
            b.append(((PSymbol) this).quantification + " ");
        }

        b.append(toString() + " : " + myType);

        if (myTypeValue != null) {
            b.append("(Defines: " + myTypeValue + ")");
        }

        b.append(" " + valueHash);

        for (PExp e : getSubExpressions()) {
            b.append("\n" + e.toDebugString(indent + offset, offset));
        }

        return b.toString();
    }
}
