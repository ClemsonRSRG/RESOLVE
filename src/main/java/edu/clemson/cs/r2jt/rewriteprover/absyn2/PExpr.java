/**
 * PExpr.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.absyn2;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.absynnew.expr.MathSetAST;
import edu.clemson.cs.r2jt.absynnew.expr.MathSymbolAST;
import edu.clemson.cs.r2jt.rewriteprover.Utilities;
import edu.clemson.cs.r2jt.rewriteprover.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.typeandpopulate2.MTFunction;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import org.antlr.v4.runtime.Token;

import java.util.*;
import java.io.StringWriter;

/**
 * <p><code>PExpr</code> is the root of the prover abstract syntax tree
 * hierarchy.  Unlike {@link ExprAST}s, <code>PExpr</code>s are immutable and
 * exist without the complications introduced by control structures.
 * <code>PExp</code>s exist to represent mathematical expressions only.</p>
 */
public abstract class PExpr {

    protected final static BindingException BINDING_EXCEPTION =
            new BindingException();

    public final int structureHash;
    public final int valueHash;

    protected final MTType myType;
    protected final MTType myTypeValue;

    private Set<String> myCachedSymbolNames = null;
    private List<PExpr> myCachedFunctionApplications = null;
    private Set<PSymbol> myCachedQuantifiedVariables = null;

    public PExpr(HashDuple hashes, MTType type, MTType typeValue) {
        this(hashes.structureHash, hashes.valueHash, type, typeValue);
    }

    public PExpr(int structureHash, int valueHash, MTType type, MTType typeValue) {
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
    public PExpr withTypesSubstituted(Map<MTType, MTType> substitutions) {

        TypeModifyingVisitor v = new TypeModifyingVisitor(substitutions);
        this.accept(v);

        return v.getFinalPExp();
    }

    public PExpr withSiteAltered(Iterator<Integer> path, PExpr newValue) {

        Deque<Integer> integerPath = new LinkedList<Integer>();
        Deque<PExpr> pexpPath = new LinkedList<PExpr>();

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

    public abstract PExpr withTypeReplaced(MTType t);

    public abstract PExpr withTypeValueReplaced(MTType t);

    public abstract PExpr withSubExpressionReplaced(int index, PExpr e);

    public PExpr withSubExpressionsReplaced(Map<Integer, PExpr> e) {
        PExpr working = this;

        for (Map.Entry<Integer, PExpr> entry : e.entrySet()) {
            working =
                    working.withSubExpressionReplaced(entry.getKey(), entry
                            .getValue());
        }

        return working;
    }

    public static PExpr trueExp(TypeGraph g) {
        return new PSymbol(g.BOOLEAN, null, "true");
    }

    public abstract ImmutableList<PExpr> getSubExpressions();

    public abstract PExpSubexpressionIterator getSubExpressionIterator();

    public abstract boolean isObviouslyTrue();

    public final List<PExpr> splitIntoConjuncts() {
        List<PExpr> conjuncts = new LinkedList<PExpr>();

        splitIntoConjuncts(conjuncts);

        return conjuncts;
    }

    protected abstract void splitIntoConjuncts(List<PExpr> accumulator);

    public abstract PExpr flipQuantifiers();

    /**
     * <p>For testing purposes, building a PExp is a pain in the ass.  This
     * method presents a relatively easy way of 'describing' the PExp you'd like
     * as a string, rather than resorting to building complex types and object
     * trees manually.  The string is interpreted using a stack-based, postfix
     * style language, as follows:</p>
     *
     * <p>All tokens are separated by whitespace.  A {@link PSymbol} can
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
    /*public static PExpr buildPExp(String description, TypeGraph g) {
        Deque<PExpr> stack = new LinkedList<PExpr>();

        PSymbol.Quantification quant = PSymbol.Quantification.NONE;
        Iterator<String> tokens =
                Arrays.asList(description.split(" ")).iterator();
        String token;
        while (tokens.hasNext()) {
            token = tokens.next();

            if (token.equals("forall")) {
                quant = PSymbol.Quantification.FOR_ALL;
            }
            else if (token.equals("(")) {
                String functionName = tokens.next();
                int parameterCount = Integer.parseInt(tokens.next());
                String displayTypeDesc = tokens.next();
                String typeDesc = tokens.next();

                PSymbol.DisplayType displayType;
                if (displayTypeDesc.equals("i")) {
                    displayType = PSymbol.DisplayType.INFIX;
                }
                else if (displayTypeDesc.equals("o")) {
                    displayType = PSymbol.DisplayType.OUTFIX;
                }
                else if (displayTypeDesc.equals("p")) {
                    displayType = PSymbol.DisplayType.PREFIX;
                }
                else {
                    throw new IllegalArgumentException("Unknown display type: "
                            + displayTypeDesc);
                }

                MTType type = typeFromDesc(typeDesc, g);

                List<PExpr> parameters = new LinkedList<PExpr>();
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
                quant = PSymbol.Quantification.NONE;
            }
            else {
                String name = token;
                MTType type = typeFromDesc(tokens.next(), g);

                stack.push(new PSymbol(type, null, name, quant));
                quant = PSymbol.Quantification.NONE;
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
    }*/

    public static PExpr buildPExp(ExprAST e) {
        PExpr retval;
        e = Utilities.applyQuantification(e);

        if (e == null) {
            throw new IllegalArgumentException("Prover does not accept null "
                    + "as an expression.");
        }

        if (e instanceof MathSymbolAST) {
            MathSymbolAST eAsFunctionExp = (MathSymbolAST) e;
            List<PExpr> arguments = new LinkedList<PExpr>();

            for (ExprAST arg : eAsFunctionExp.getArguments()) {
                arguments.add(PExpr.buildPExp(arg));
            }
            retval =
                    new PSymbol(e.getMathType(), e.getMathTypeValue(),
                            fullName(null, eAsFunctionExp.getName().getText()),
                            arguments, convertExpQuantification(eAsFunctionExp
                                    .getQuantification()));
        }
        else if (e instanceof MathSetAST) {
            MathSetAST eAsSet = (MathSetAST) e;
            List<PExpr> elements = new ArrayList<PExpr>();
            for (ExprAST element : eAsSet.getElements()) {
                elements.add(PExpr.buildPExp(element));
            }
            retval =
                    new PSymbol(e.getMathType(), e.getMathTypeValue(),
                            fullName(null, "{..}"));
        }
        /*else if (e instanceof DotExp) {
            DotExp eAsDotExp = (DotExp) e;

            String symbol = "";

            List<edu.clemson.cs.r2jt.rewriteprover.absyn.PExp> arguments = new LinkedList<edu.clemson.cs.r2jt.rewriteprover.absyn.PExp>();

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
                symbol += edu.clemson.cs.r2jt.rewriteprover.absyn.PExp.buildPExp(eAsDotExp.getSemanticExp());
            }

            retval =
                    new PSymbol(e.getMathType(), e.getMathTypeValue(), symbol,
                            arguments);
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
                    new PLambda(new ArrayBackedImmutableList(parameters), edu.clemson.cs.r2jt.rewriteprover.absyn.PExp
                            .buildPExp(eAsLambdaExp.getBody()));
        }
        else if (e instanceof AlternativeExp) {
            AlternativeExp eAsAlternativeExp = (AlternativeExp) e;

            retval = new PAlternatives(eAsAlternativeExp);
        }*/
        else {
            throw new RuntimeException("Expressions of type " + e.getClass()
                    + " are not currently accepted by the prover.");
        }

        //The Analyzer doesn't work consistently.  Fail early if we don't have
        //typing information
        if (retval.getType() == null) {

            String varExpAdditional = "";
            if (e instanceof MathSymbolAST) {
                varExpAdditional =
                        " = \"" + ((MathSymbolAST) e).getName().getText()
                                + "\" ";
            }

            throw new UnsupportedOperationException(
                    "Expression has null type.\n\n" + e + " (" + e.getClass()
                            + ")" + varExpAdditional);
        }
        return retval;
    }

    //Todo: I don't really see why we can't all use one quantification enum...
    private final static PSymbol.Quantification convertExpQuantification(
            SymbolTableEntry.Quantification q) {

        PSymbol.Quantification retval;

        if (q == SymbolTableEntry.Quantification.EXISTENTIAL) {
            retval = PSymbol.Quantification.THERE_EXISTS;
        }
        else if (q == SymbolTableEntry.Quantification.UNIVERSAL) {
            retval = PSymbol.Quantification.FOR_ALL;
        }
        else {
            retval = PSymbol.Quantification.NONE;
        }
        return retval;
    }

    public final Map<PExpr, PExpr> bindTo(PExpr target) throws BindingException {
        Map<PExpr, PExpr> bindings = new HashMap<PExpr, PExpr>();
        bindTo(target, bindings);

        return bindings;
    }

    public abstract void bindTo(PExpr target, Map<PExpr, PExpr> accumulator)
            throws BindingException;

    @Override
    public int hashCode() {
        return valueHash;
    }

    public abstract PExpr substitute(Map<PExpr, PExpr> substitutions);

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

    public final List<PExpr> getFunctionApplications() {
        if (myCachedFunctionApplications == null) {
            //We're immutable, so only do this once
            myCachedFunctionApplications = getFunctionApplicationsNoCache();
        }

        return myCachedFunctionApplications;
    }

    public abstract List<PExpr> getFunctionApplicationsNoCache();

    public abstract boolean containsExistential();

    public abstract boolean isEquality();

    public abstract boolean isLiteral();

    private final static String fullName(Token qualifier, String name) {
        String retval;

        if (qualifier == null) {
            retval = "";
        }
        else {
            if (qualifier.getText() == null) {
                retval = "";
            }
            else {
                retval = qualifier.getText() + ".";
            }
        }

        return retval + name;
    }

    public boolean typeMatches(MTType other) {
        return other.isSubtypeOf(myType);
    }

    public boolean typeMatches(PExpr other) {
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

        for (PExpr e : getSubExpressions()) {
            b.append("\n" + e.toDebugString(indent + offset, offset));
        }

        return b.toString();
    }
}
