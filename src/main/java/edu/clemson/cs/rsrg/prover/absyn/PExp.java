/*
 * PExp.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.prover.absyn;

import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.prover.absyn.expressions.PAlternatives;
import edu.clemson.cs.rsrg.prover.absyn.expressions.PLambda;
import edu.clemson.cs.rsrg.prover.absyn.expressions.PSymbol;
import edu.clemson.cs.rsrg.prover.absyn.iterators.PExpSubexpressionIterator;
import edu.clemson.cs.rsrg.prover.absyn.visitors.NestedPExpVisitors;
import edu.clemson.cs.rsrg.prover.absyn.visitors.PExpTextRenderingVisitor;
import edu.clemson.cs.rsrg.prover.absyn.visitors.PExpVisitor;
import edu.clemson.cs.rsrg.prover.exception.BindingException;
import edu.clemson.cs.rsrg.prover.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.rsrg.prover.immutableadts.ImmutableList;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTFunction;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.io.StringWriter;
import java.util.*;

/**
 * <p>
 * This class represents the root of the prover abstract syntax tree (AST)
 * hierarchy.
 * </p>
 *
 * <p>
 * {@code PExp} is the root of the prover abstract syntax tree hierarchy. Unlike
 * {@link Exp Exp}s,
 * {@code PExp}s are immutable and exist without the complications introduced by
 * control structures.
 * {@code PExp}s exist to represent <em>only</em> mathematical expressions.
 * </p>
 *
 * @author Hampton Smith
 * @author Mike Kabbani
 * @version 2.0
 */
public abstract class PExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * An instance of a binding exception.
     * </p>
     */
    protected final static BindingException BINDING_EXCEPTION =
            new BindingException();

    /**
     * <p>
     * The expression's cached symbol names.
     * </p>
     */
    private Set<String> myCachedSymbolNames = null;

    /**
     * <p>
     * The expression's cached function applications.
     * </p>
     */
    private List<PExp> myCachedFunctionApplications = null;

    /**
     * <p>
     * The expression's cached quantified variable symbols.
     * </p>
     */
    private Set<PSymbol> myCachedQuantifiedVariables = null;

    /**
     * <p>
     * The expression's mathematical type.
     * </p>
     */
    protected final MTType myMathType;

    /**
     * <p>
     * The expression's mathematical type value.
     * </p>
     */
    protected final MTType myMathTypeValue;

    /**
     * <p>
     * The expression's structure hash.
     * </p>
     */
    public final int structureHash;

    /**
     * <p>
     * The expression's value hash.
     * </p>
     */
    public final int valueHash;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * An helper constructor that allow us to store the calculated hash values
     * and mathematical type
     * information for objects created from a class that inherits from
     * {@code PExp}.
     * </p>
     *
     * @param hashes An helper object that contains the structure hash and value
     *        hash.
     * @param type The expression's mathematical type.
     * @param typeValue The expression's mathematical type value.
     */
    protected PExp(HashDuple hashes, MTType type, MTType typeValue) {
        this(hashes.structureHash, hashes.valueHash, type, typeValue);
    }

    /**
     * <p>
     * An helper constructor that allow us to store the calculated hash values
     * and mathematical type
     * information for objects created from a class that inherits from
     * {@code PExp}.
     * </p>
     *
     * @param structureHash The expression's structure hash
     * @param valueHash The expression's value hash.
     * @param type The expression's mathematical type.
     * @param typeValue The expression's mathematical type value.
     */
    protected PExp(int structureHash, int valueHash, MTType type,
            MTType typeValue) {
        myMathType = type;
        myMathTypeValue = typeValue;

        this.structureHash = structureHash;
        this.valueHash = valueHash;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method is the {@code accept()} method in a visitor pattern for
     * invoking an instance of
     * {@link PExpVisitor}.
     * </p>
     *
     * @param v A visitor for {@link PExp PExps}.
     */
    public abstract void accept(PExpVisitor v);

    /**
     * <p>
     * This method returns a mapping of expressions that that can be bound from
     * the target.
     * </p>
     *
     * @param target The target expression.
     *
     * @return A mapping of binding expressions.
     *
     * @throws BindingException Some error occurred during binding.
     */
    public final Map<PExp, PExp> bindTo(PExp target) throws BindingException {
        Map<PExp, PExp> bindings = new HashMap<>();

        bindTo(target, bindings);

        return bindings;
    }

    /**
     * <p>
     * This method binds the expressions found in our mapping to the specified
     * target.
     * </p>
     *
     * @param target The target expression.
     * @param accumulator A mapping of expressions to be bound.
     *
     * @throws BindingException Some error occurred during binding.
     */
    public abstract void bindTo(PExp target, Map<PExp, PExp> accumulator)
            throws BindingException;

    /**
     * <p>
     * This method converts an expression generated from the AST to the prover's
     * immutable expression
     * hierarchy.
     * </p>
     *
     * @param g The current type graph.
     * @param e An expression from the compiler AST.
     *
     * @return A {@link PExp} representation of {@code e}.
     */
    public static PExp buildPExp(TypeGraph g, Exp e) {
        PExp retval;

        // Apply proper quantification
        Map<String, SymbolTableEntry.Quantification> quantifiedVariables =
                new LinkedHashMap<>();
        e = applyQuantification(e, quantifiedVariables);

        if (e == null) {
            throw new IllegalArgumentException(
                    "Prover does not accept null " + "as an expression.");
        }

        if (e instanceof FunctionExp) {
            FunctionExp eAsFunctionExp = (FunctionExp) e;

            List<PExp> arguments = new LinkedList<>();
            Iterator<Exp> eArgs = eAsFunctionExp.getArguments().iterator();
            List<MTType> paramTypes = new ArrayList<>();
            while (eArgs.hasNext()) {
                PExp argExp = PExp.buildPExp(g, eArgs.next());
                arguments.add(argExp);
                paramTypes.add(argExp.getMathType());
            }

            if (g != null) {
                MTFunction fullType =
                        new MTFunction(g, e.getMathType(), paramTypes);
                retval = new PSymbol(fullType, e.getMathTypeValue(),
                        fullName(eAsFunctionExp.getQualifier(),
                                eAsFunctionExp.getName().getName().getName()),
                        arguments, convertExpQuantification(
                                eAsFunctionExp.getQuantification()));
            }
            else {
                retval = new PSymbol(e.getMathType(), e.getMathTypeValue(),
                        fullName(eAsFunctionExp.getQualifier(),
                                eAsFunctionExp.getName().getName().getName()),
                        arguments, convertExpQuantification(
                                eAsFunctionExp.getQuantification()));
            }
        }
        else if (e instanceof PrefixExp) {
            PrefixExp eAsPrefixExp = (PrefixExp) e;

            List<PExp> arguments = new LinkedList<>();
            arguments.add(PExp.buildPExp(g, eAsPrefixExp.getArgument()));

            retval = new PSymbol(e.getMathType(), e.getMathTypeValue(),
                    eAsPrefixExp.getOperatorAsPosSymbol().getName(), arguments);
        }
        else if (e instanceof InfixExp) {
            InfixExp eAsInfixExp = (InfixExp) e;

            List<PExp> arguments = new LinkedList<>();
            arguments.add(PExp.buildPExp(g, eAsInfixExp.getLeft()));
            arguments.add(PExp.buildPExp(g, eAsInfixExp.getRight()));

            retval = new PSymbol(e.getMathType(), e.getMathTypeValue(),
                    eAsInfixExp.getOperatorAsPosSymbol().getName(), arguments,
                    PSymbol.DisplayType.INFIX);
        }
        else if (e instanceof OutfixExp) {
            OutfixExp eAsOutfixExp = (OutfixExp) e;

            List<PExp> arguments = new LinkedList<>();
            arguments.add(PExp.buildPExp(g, eAsOutfixExp.getArgument()));

            retval = new PSymbol(e.getMathType(), e.getMathTypeValue(),
                    eAsOutfixExp.getOperator().getLeftDelimiterString(),
                    eAsOutfixExp.getOperator().getRightDelimiterString(),
                    arguments, PSymbol.DisplayType.OUTFIX);
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

            retval = new PSymbol(e.getMathType(), e.getMathTypeValue(),
                    eAsStringExp.getValue());
        }
        else if (e instanceof DotExp) {
            DotExp eAsDotExp = (DotExp) e;
            StringBuilder symbol = new StringBuilder();

            List<PExp> arguments = new LinkedList<>();

            boolean first = true;
            for (Exp s : eAsDotExp.getSegments()) {
                if (!first) {
                    symbol.append(".");
                }
                else {
                    first = false;
                }

                if (s instanceof FunctionExp) {
                    FunctionExp sAsFE = (FunctionExp) s;
                    symbol.append(sAsFE.getOperatorAsString());

                    for (Exp param : sAsFE.getParameters()) {
                        arguments.add(buildPExp(g, param));
                    }
                }
                else {
                    symbol.append(s);
                }
            }

            retval = new PSymbol(e.getMathType(), e.getMathTypeValue(),
                    symbol.toString(), arguments);
        }
        else if (e instanceof VarExp) {
            VarExp eAsVarExp = (VarExp) e;

            retval = new PSymbol(eAsVarExp.getMathType(),
                    eAsVarExp.getMathTypeValue(),
                    fullName(eAsVarExp.getQualifier(),
                            eAsVarExp.getName().getName()),
                    convertExpQuantification(eAsVarExp.getQuantification()));
        }
        else if (e instanceof LambdaExp) {
            LambdaExp eAsLambdaExp = (LambdaExp) e;

            List<PLambda.Parameter> parameters = new LinkedList<>();
            for (MathVarDec p : eAsLambdaExp.getParameters()) {
                parameters.add(new PLambda.Parameter(p.getName().getName(),
                        p.getTy().getMathTypeValue()));
            }

            retval = new PLambda(new ArrayBackedImmutableList<>(parameters),
                    PExp.buildPExp(g, eAsLambdaExp.getBody()));
        }
        else if (e instanceof AlternativeExp) {
            AlternativeExp eAsAlternativeExp = (AlternativeExp) e;

            retval = new PAlternatives(eAsAlternativeExp);
        }
        else {
            throw new RuntimeException("Expressions of type " + e.getClass()
                    + " are not accepted by the prover." + e.getLocation());
        }

        // Fail early if we don't have a mathematical type.
        if (retval.getMathType() == null) {
            String varExpAdditional = "";
            if (e instanceof VarExp) {
                varExpAdditional = " = \"" + ((VarExp) e).getName().getName()
                        + "\", " + ((VarExp) e).getName().getLocation();
            }

            throw new UnsupportedOperationException(
                    "Expression has null type.\n\n" + e + " (" + e.getClass()
                            + ")" + varExpAdditional);
        }

        return retval;
    }

    /**
     * <p>
     * This method checks to see if the current expression contains an
     * existential quantifier.
     * </p>
     *
     * @return {@code true} if it does contain one, {@code false} otherwise.
     */
    public abstract boolean containsExistential();

    /**
     * <p>
     * This method attempts to find an expression with the given name in our
     * sub-expressions.
     * </p>
     *
     * @param name Expression name to be searched.
     *
     * @return {@code true} if this expression contains a sub-expression that
     *         matches the specified
     *         name, {@code false} otherwise.
     */
    public abstract boolean containsName(String name);

    /**
     * <p>
     * This method must be implemented by all inherited classes to override the
     * default equals method
     * implementation.
     * </p>
     *
     * @param o Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false}
     *         otherwise.
     */
    @Override
    public abstract boolean equals(Object o);

    /**
     * <p>
     * This method attempts to flip all quantifiers to generate a new
     * expression.
     * </p>
     *
     * @return A new {@link PExp}.
     */
    public abstract PExp flipQuantifiers();

    /**
     * <p>
     * This method returns a list of sub-expressions that are function
     * applications in the current
     * expression.
     * </p>
     *
     * @return A list of sub-expressions that are function applications.
     */
    public final List<PExp> getFunctionApplications() {
        if (myCachedFunctionApplications == null) {
            // We're immutable, so only do this once
            myCachedFunctionApplications = getFunctionApplicationsNoCache();
        }

        return myCachedFunctionApplications;
    }

    /**
     * <p>
     * This method gets the mathematical type associated with this expression.
     * </p>
     *
     * @return A {@link MTType} type object.
     */
    public final MTType getMathType() {
        return myMathType;
    }

    /**
     * <p>
     * This method gets the mathematical type value associated with this
     * expression.
     * </p>
     *
     * @return A {@link MTType} type object.
     */
    public final MTType getMathTypeValue() {
        return myMathTypeValue;
    }

    /**
     * <p>
     * This method returns a set of quantified variables for the current
     * expression.
     * </p>
     *
     * @return A set of quantified variable symbols.
     */
    public final Set<PSymbol> getQuantifiedVariables() {
        if (myCachedQuantifiedVariables == null) {
            // We're immutable, so only do this once
            myCachedQuantifiedVariables = Collections
                    .unmodifiableSet(getQuantifiedVariablesNoCache());
        }

        return myCachedQuantifiedVariables;
    }

    /**
     * <p>
     * This method returns the list of sub-expressions.
     * </p>
     *
     * @return An immutable list containing {@link PExp} expressions.
     */
    public abstract ImmutableList<PExp> getSubExpressions();

    /**
     * <p>
     * This method returns an iterator for iterating over the list of
     * sub-expressions.
     * </p>
     *
     * @return An iterator.
     */
    public abstract PExpSubexpressionIterator getSubExpressionIterator();

    /**
     * <p>
     * This method returns a set of symbol names for the current expression.
     * </p>
     *
     * @return A set of names.
     */
    public final Set<String> getSymbolNames() {
        if (myCachedSymbolNames == null) {
            // We're immutable, so only do this once
            myCachedSymbolNames =
                    Collections.unmodifiableSet(getSymbolNamesNoCache());
        }

        return myCachedSymbolNames;
    }

    /**
     * <p>
     * This method returns the top-level operation for the current expression.
     * </p>
     *
     * @return A string representing the top-level operation.
     */
    public abstract String getTopLevelOperation();

    /**
     * <p>
     * This method overrides the default {@code hashCode} method implementation.
     * </p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        return valueHash;
    }

    /**
     * <p>
     * This method checks to see if this expression is an equality expression.
     * </p>
     *
     * @return {@code true} if it is one, {@code false} otherwise.
     */
    public abstract boolean isEquality();

    /**
     * <p>
     * This method checks to see if this expression is a literal expression.
     * </p>
     *
     * @return {@code true} if it is, {@code false} otherwise.
     */
    public abstract boolean isLiteral();

    /**
     * <p>
     * This method checks to see if this expression is obviously equivalent to
     * {@code true}.
     * </p>
     *
     * @return {@code true} if it is obviously equivalent to mathematical
     *         {@code true} expression,
     *         {@code false} otherwise.
     */
    public abstract boolean isObviouslyTrue();

    /**
     * <p>
     * This method checks to see if this expression represents a variable.
     * </p>
     *
     * @return {@code true} if it is a variable expression, {@code false}
     *         otherwise.
     */
    public abstract boolean isVariable();

    /**
     * <p>
     * This method processes the current expression using its string
     * representation.
     * </p>
     *
     * @param visitor A visitor for {@link PExp PExps}.
     * @param a An appendable object.
     */
    public final void processStringRepresentation(PExpVisitor visitor,
            Appendable a) {
        PExpTextRenderingVisitor renderer = new PExpTextRenderingVisitor(a);
        PExpVisitor finalVisitor = new NestedPExpVisitors(visitor, renderer);

        this.accept(finalVisitor);
    }

    /**
     * <p>
     * This method returns a DEEP COPY of this expression, with all instances of
     * {@link PExp PExps}
     * that occur as keys in {@code substitutions} replaced with their
     * corresponding values.
     * </p>
     *
     * @param substitutions A mapping from {@link PExp PExps} that should be
     *        substituted out to the
     *        {@link PExp PExps} that should replace them.
     *
     * @return A new {@link PExp} that is a deep copy of the original with the
     *         provided substitutions
     *         made.
     */
    public abstract PExp substitute(Map<PExp, PExp> substitutions);

    /**
     * <p>
     * This method returns the current expression in string format with proper
     * indentation and offset.
     * </p>
     *
     * <p>
     * Note that this method is only used for debugging purposes.
     * </p>
     *
     * @param indent The base indentation to the first line of the text.
     * @param offset The additional indentation increment for the subsequent
     *        lines.
     *
     * @return A formatted text string for this {@link PExp}.
     */
    public final String toDebugString(int indent, int offset) {
        StringBuilder b = new StringBuilder();
        if (this instanceof PSymbol) {
            b.append(((PSymbol) this).quantification).append(" ");
        }

        b.append(toString()).append(" : ").append(myMathType);

        if (myMathTypeValue != null) {
            b.append("(Defines: ").append(myMathTypeValue).append(")");
        }

        b.append(" ").append(valueHash);

        for (PExp e : getSubExpressions()) {
            b.append("\n").append(e.toDebugString(indent + offset, offset));
        }

        return b.toString();
    }

    /**
     * <p>
     * This method returns the current expression in string format.
     * </p>
     *
     * @return Current {@link PExp} as a string.
     */
    public final String toString() {
        StringWriter output = new StringWriter();
        PExpTextRenderingVisitor renderer =
                new PExpTextRenderingVisitor(output);

        this.accept(renderer);

        return output.toString();
    }

    /**
     * <p>
     * This static method method creates a variable expression that matches the
     * boolean {@code true}.
     * </p>
     *
     * @param g A {@link TypeGraph} to retrieve the mathematical boolean type.
     *
     * @return A {@link PExp} representing {@code true}.
     */
    public static PExp trueExp(TypeGraph g) {
        return new PSymbol(g.BOOLEAN, null, "true");
    }

    /**
     * <p>
     * This method checks to see if the specified type matches the current
     * expression's mathematical
     * type.
     * </p>
     *
     * @param other Some other mathematical type.
     *
     * @return {@code true} if it matches, {@code false} otherwise.
     */
    public final boolean typeMatches(MTType other) {
        return other.isSubtypeOf(myMathType);
    }

    /**
     * <p>
     * This method checks to see if the specified expression has a type that
     * matches the current
     * expression's mathematical type.
     * </p>
     *
     * @param other Some other {@link PExp}.
     *
     * @return {@code true} if it matches, {@code false} otherwise.
     */
    public final boolean typeMatches(PExp other) {
        return typeMatches(other.getMathType());
    }

    /**
     * <p>
     * This method returns a new expression by substituting the specified value
     * in all the
     * sub-expressions.
     * </p>
     *
     * @param path A path for iterating over the sub-expressions.
     * @param newValue New expression value to be substituted.
     *
     * @return A modified {@link PExp}.
     */
    public final PExp withSiteAltered(Iterator<Integer> path, PExp newValue) {
        Deque<Integer> integerPath = new LinkedList<>();
        Deque<PExp> pexpPath = new LinkedList<>();

        pexpPath.push(this);
        while (path.hasNext()) {
            integerPath.push(path.next());

            pexpPath.push(pexpPath.peek().getSubExpressions()
                    .get(integerPath.peek()));
        }

        pexpPath.pop();
        pexpPath.push(newValue);

        while (pexpPath.size() > 1) {
            newValue = pexpPath.pop();
            pexpPath.push(pexpPath.pop()
                    .withSubExpressionReplaced(integerPath.pop(), newValue));
        }

        return pexpPath.peek();
    }

    /**
     * <p>
     * This method attempts to replace an argument at the specified index.
     * </p>
     *
     * @param index Index to an expression argument.
     * @param e The {@link PExp} to replace the one in our argument list.
     *
     * @return A new {@link PExp} with the expression at the specified index
     *         replaced with {@code e}.
     */
    public abstract PExp withSubExpressionReplaced(int index, PExp e);

    /**
     * <p>
     * This method provides a mapping of expressions that needs to be replaced
     * at each index key.
     * </p>
     *
     * @param e A mapping from index to some expression argument.
     *
     * @return A new {@link PExp} with the expression replaced by all the
     *         key-value pairs in
     *         {@code e}.
     */
    public final PExp withSubExpressionsReplaced(Map<Integer, PExp> e) {
        PExp working = this;

        for (Map.Entry<Integer, PExp> entry : e.entrySet()) {
            working = working.withSubExpressionReplaced(entry.getKey(),
                    entry.getValue());
        }

        return working;
    }

    /**
     * <p>
     * This method returns a new expression with the mathematical type replaced.
     * </p>
     *
     * @param t A new mathematical type.
     *
     * @return A new {@link PExp} with {@code t} as its mathematical type.
     */
    public abstract PExp withTypeReplaced(MTType t);

    /**
     * <p>
     * This method returns a new expression with the mathematical type value
     * replaced.
     * </p>
     *
     * @param t A new mathematical type value.
     *
     * @return A new {@link PExp} with {@code t} as its mathematical type value.
     */
    public abstract PExp withTypeValueReplaced(MTType t);

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * This method returns a list of sub-expressions that are function
     * applications in the current
     * expression without using the cache.
     * </p>
     *
     * @return A list of sub-expressions that are function applications.
     */
    protected abstract List<PExp> getFunctionApplicationsNoCache();

    /**
     * <p>
     * This method returns a set of quantified variables without using the
     * cache.
     * </p>
     *
     * @return A set of quantified variables.
     */
    protected abstract Set<PSymbol> getQuantifiedVariablesNoCache();

    /**
     * <p>
     * This method returns a set of symbol names without using the cache.
     * </p>
     *
     * @return A set of names.
     */
    protected abstract Set<String> getSymbolNamesNoCache();

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * Takes an expression and distributes its quantifiers down to the
     * variables, removing the
     * quantifiers in the process. (That is, in the expression
     * <code>For all x, x = y</code>, the
     * variable <code>x</code> in the equals expression would be marked
     * internally as a "for all"
     * variable and the expression <code>x = y</code> would be returned.)
     * </p>
     *
     * <p>
     * Quantified expressions with "where" clauses will be normalized to remove
     * the where clause. In
     * the case of "for all" statements, the where clause will be added as the
     * antecedent to an
     * implication, so that <code>For all x, where Q(x), P(x)</code> will become
     * <code>For all x,
     * Q(x) implies P(x)</code> and <code>There exists x, where Q(x), such that
     * P(x)</code> will become <code>There exists x such that Q(x) and P(x)
     * </code>.
     * </p>
     *
     * @param e The expression for whom quantifiers should be distributed
     *        downward. This will largely
     *        be modified in place.
     * @param quantifiedVariables A map of variable names that should be
     *        considered quantified mapped
     *        to the quantifiers that apply to them.
     *
     * @return The root of the new expression.
     */
    private static Exp applyQuantification(Exp e,
            Map<String, SymbolTableEntry.Quantification> quantifiedVariables) {
        Exp retval;

        if (e instanceof QuantExp) {
            QuantExp eAsQuantifier = (QuantExp) e;

            // Normalize our eAsQuantifier so that it doesn't have a "where"
            // clause by appropriately transferring the "where" clause into
            // the body using logical connectives
            if (eAsQuantifier.getWhere() != null) {
                switch (eAsQuantifier.getQuantification()) {
                case UNIVERSAL:
                    eAsQuantifier = new QuantExp(eAsQuantifier.getLocation(),
                            eAsQuantifier.getQuantification(),
                            eAsQuantifier.getVars(), null,
                            MathExp.formImplies(
                                    eAsQuantifier.getLocation().clone(),
                                    eAsQuantifier.getWhere(),
                                    eAsQuantifier.getBody()));
                    break;
                case EXISTENTIAL:
                    eAsQuantifier = new QuantExp(eAsQuantifier.getLocation(),
                            eAsQuantifier.getQuantification(),
                            eAsQuantifier.getVars(), null,
                            MathExp.formConjunct(
                                    eAsQuantifier.getLocation().clone(),
                                    eAsQuantifier.getWhere(),
                                    eAsQuantifier.getBody()));
                    break;
                default:
                    throw new RuntimeException("Don't know how to normalize "
                            + "this kind of quantified expression.");
                }
            }

            List<MathVarDec> variableNames = eAsQuantifier.getVars();

            for (MathVarDec v : variableNames) {
                quantifiedVariables.put(v.getName().getName(),
                        eAsQuantifier.getQuantification());
            }

            retval = applyQuantification(eAsQuantifier.getBody(),
                    quantifiedVariables);

            for (MathVarDec v : variableNames) {
                quantifiedVariables.remove((v.getName().getName()));
            }
        }
        else {
            if (e instanceof VarExp) {
                VarExp eAsVar = (VarExp) e;
                String varName = eAsVar.getName().getName();

                if (quantifiedVariables.containsKey(varName)) {
                    eAsVar.setQuantification(quantifiedVariables.get(varName));
                }
            }
            else {
                if (e instanceof FunctionExp) {
                    FunctionExp eAsFunctionExp = (FunctionExp) e;
                    String functionName =
                            eAsFunctionExp.getName().getName().getName();
                    if (quantifiedVariables.containsKey(functionName)) {
                        eAsFunctionExp.setQuantification(
                                quantifiedVariables.get(functionName));
                    }
                }

                List<Exp> subExpressions = e.getSubExpressions();
                Exp curSubExpression;
                Map<Exp, Exp> substitutions = new LinkedHashMap<>();
                for (Exp subExpression : subExpressions) {
                    curSubExpression = subExpression;
                    substitutions.put(curSubExpression, applyQuantification(
                            curSubExpression, quantifiedVariables));
                }

                // Apply substitutions
                e = e.substitute(substitutions);
            }

            retval = e;
        }

        return retval;
    }

    /**
     * <p>
     * An helper method for converting the quantification to the one provided by
     * {@link PSymbol}.
     * </p>
     *
     * @param q A symbol table quantifier.
     *
     * @return A prover expression version of the quantifier.
     */
    private static PSymbol.Quantification
            convertExpQuantification(SymbolTableEntry.Quantification q) {
        PSymbol.Quantification retval;

        switch (q) {
        case EXISTENTIAL:
            retval = PSymbol.Quantification.THERE_EXISTS;
            break;
        case UNIVERSAL:
            retval = PSymbol.Quantification.FOR_ALL;
            break;
        case NONE:
            retval = PSymbol.Quantification.NONE;
            break;
        default:
            throw new RuntimeException("Unrecognized quantification");
        }

        return retval;
    }

    /**
     * <p>
     * An helper method for generating the full name with the qualifier.
     * </p>
     *
     * @param qualifier A qualifier name.
     * @param name A name.
     *
     * @return A string with the qualifier name (if not {@code null}) prepended
     *         before the specified
     *         name.
     */
    private static String fullName(PosSymbol qualifier, String name) {
        StringBuilder sb = new StringBuilder();

        if (qualifier != null) {
            if (qualifier.getName() != null) {
                sb.append(qualifier.getName());
                sb.append(".");
            }
        }

        sb.append(name);

        return sb.toString();
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>
     * An helper construct for storing the structure hash and value hash for a
     * {@code PExp}.
     * </p>
     */
    protected static class HashDuple {

        /**
         * <p>
         * The structure hash.
         * </p>
         */
        public int structureHash;

        /**
         * <p>
         * The value hash.
         * </p>
         */
        public int valueHash;

        /**
         * <p>
         * This creates a duple with the two hash values.
         * </p>
         *
         * @param structureHash A structure hash.
         * @param valueHash A value hash.
         */
        public HashDuple(int structureHash, int valueHash) {
            this.structureHash = structureHash;
            this.valueHash = valueHash;
        }
    }

}
