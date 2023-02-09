/*
 * PLambda.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.prover.absyn.expressions;

import edu.clemson.rsrg.prover.absyn.PExp;
import edu.clemson.rsrg.prover.absyn.iterators.PExpSubexpressionIterator;
import edu.clemson.rsrg.prover.absyn.visitors.PExpVisitor;
import edu.clemson.rsrg.prover.exception.BindingException;
import edu.clemson.rsrg.prover.immutableadts.ArrayBackedImmutableList;
import edu.clemson.rsrg.prover.immutableadts.ImmutableList;
import edu.clemson.rsrg.prover.immutableadts.SingletonImmutableList;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTFunction;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;

import java.util.*;

/**
 * <p>
 * A {@code PLambda} represents a reference to a lambda expression.
 * </p>
 *
 * @author Hampton Smith
 * @author Mike Kabbani
 *
 * @version 2.0
 */
public class PLambda extends PExp {

    // ===========================================================
    // ClauseType
    // ===========================================================

    /**
     * <p>
     * An helper class for creating a lambda parameter.
     * </p>
     *
     * @author Hampton Smith
     *
     * @version 2.0
     */
    public static class Parameter {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * Parameter name
         * </p>
         */
        public final String name;

        /**
         * <p>
         * Parameter type
         * </p>
         */
        public final MTType type;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * This represents a parameter for a lambda expression.
         * </p>
         *
         * @param name
         *            Parameter name.
         * @param type
         *            Parameter type.
         */
        public Parameter(String name, MTType type) {
            if (name == null) {
                throw new IllegalArgumentException("Parameter name is null");
            }

            if (type == null) {
                throw new IllegalArgumentException("Parameter type is null");
            }

            this.name = name;
            this.type = type;
        }

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>
         * This method returns the current parameter in string format.
         * </p>
         *
         * @return Current {@link Parameter} as a string.
         */
        @Override
        public final String toString() {
            return name + " : " + type;
        }

    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * An immutable list of lambda parameters.
     * </p>
     */
    public final ImmutableList<Parameter> parameters;

    /**
     * <p>
     * An expression representing the lambda expression's body.
     * </p>
     */
    private final PExp myBody;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a prover representation of a lambda expression.
     * </p>
     *
     * @param parameters
     *            An immutable list of lambda parameters.
     * @param body
     *            An expression representing the lambda expression's body.
     */
    public PLambda(ImmutableList<Parameter> parameters, PExp body) {
        super(body.structureHash * 34, parameterHash(parameters),
                new MTFunction(body.getMathType().getTypeGraph(), body.getMathType(), parameterTypes(parameters)),
                null);

        this.parameters = parameters;
        myBody = body;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final void accept(PExpVisitor v) {
        v.beginPExp(this);
        v.beginPLambda(this);

        v.beginChildren(this);
        myBody.accept(v);
        v.endChildren(this);

        v.endPLambda(this);
        v.endPExp(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void bindTo(PExp target, Map<PExp, PExp> accumulator) throws BindingException {

        if (!(target instanceof PLambda) || !typeMatches(target)) {
            throw BINDING_EXCEPTION;
        }

        PLambda targetAsPLambda = (PLambda) target;

        targetAsPLambda = (PLambda) targetAsPLambda.substitute(accumulator);

        myBody.bindTo(targetAsPLambda.myBody, accumulator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExistential() {
        return myBody.containsExistential();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsName(String name) {
        boolean result = false;
        Iterator<Parameter> parameterIter = parameters.iterator();
        while (!result && parameterIter.hasNext()) {
            result = parameterIter.next().name.equals(name);
        }
        return result || myBody.containsName(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PLambda pLambda = (PLambda) o;

        if (!parameters.equals(pLambda.parameters))
            return false;
        return myBody.equals(pLambda.myBody);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PExp flipQuantifiers() {
        return this;
    }

    /**
     * <p>
     * This method returns the lambda body expression.
     * </p>
     *
     * @return A {@link PExp}.
     */
    public final PExp getBody() {
        return myBody;
    }

    /**
     * <p>
     * This method returns the list of parameters.
     * </p>
     *
     * @return A list of {@link PExp PExps}.
     */
    public final List<PExp> getParameters() {
        List<PExp> rList = new ArrayList<>();
        for (Parameter p : parameters) {
            rList.add(new PSymbol(p.type, null, p.name, PSymbol.Quantification.FOR_ALL));
        }

        return rList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ImmutableList<PExp> getSubExpressions() {
        return new SingletonImmutableList<>(myBody);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PExpSubexpressionIterator getSubExpressionIterator() {
        return new PLambdaBodyIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getTopLevelOperation() {
        return "lambda";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isEquality() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isLiteral() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isObviouslyTrue() {
        return myBody.isObviouslyTrue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isVariable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PExp substitute(Map<PExp, PExp> substitutions) {
        PExp retval;

        if (substitutions.containsKey(this)) {
            retval = substitutions.get(this);
        } else {
            // make new parameters if substitutions contains type variables
            retval = new PLambda(parameters, myBody.substitute(substitutions));
        }

        return retval;
    }

    /**
     * <p>
     * This method returns a new expression with the parameter names normalized.
     * </p>
     *
     * @return A new {@link PLambda} with normalized names.
     */
    public final PLambda withNormalizedParameterNames() {
        List<PExp> plist = getParameters();
        HashMap<PExp, PExp> substMap = new HashMap<>();
        int argNum = 0;
        ArrayList<Parameter> normParams = new ArrayList<>();
        for (PExp p : plist) {
            String name = p.getMathType().toString().toLowerCase() + argNum++;
            PExp norm = new PSymbol(p.getMathType(), p.getMathTypeValue(), name, PSymbol.Quantification.FOR_ALL);
            substMap.put(p, norm);
            normParams.add(new Parameter(name, p.getMathType()));
        }
        return new PLambda(new ArrayBackedImmutableList<>(normParams), myBody.substitute(substMap));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PLambda withSubExpressionReplaced(int i, PExp e) {
        if (i != 0) {
            throw new IndexOutOfBoundsException("" + i);
        }

        return new PLambda(parameters, e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PLambda withTypeReplaced(MTType t) {
        throw new UnsupportedOperationException("Cannot set the type " + "value on a " + this.getClass() + ".");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PLambda withTypeValueReplaced(MTType t) {
        throw new UnsupportedOperationException("Cannot set the type " + "value on a " + this.getClass() + ".");
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Set<String> getSymbolNamesNoCache() {
        Set<String> bodyNames = new HashSet<>(myBody.getSymbolNames());

        bodyNames.add("lambda");

        return bodyNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Set<PSymbol> getQuantifiedVariablesNoCache() {
        return myBody.getQuantifiedVariables();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final List<PExp> getFunctionApplicationsNoCache() {
        List<PExp> bodyFunctions = new LinkedList<>(myBody.getFunctionApplications());

        bodyFunctions.add(new PSymbol(getMathType(), null, "lambda"));

        return bodyFunctions;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method for retrieving the parameter's mathematical types.
     * </p>
     *
     * @param parameters
     *            An immutable list of lambda parameters.
     *
     * @return A list of mathematical types.
     */
    private static List<MTType> parameterTypes(Iterable<Parameter> parameters) {
        List<MTType> result = new LinkedList<>();

        for (Parameter p : parameters) {
            result.add(p.type);
        }

        return result;
    }

    /**
     * <p>
     * An helper method that computes an hash code for the specified parameter.
     * </p>
     *
     * @param parameters
     *            An immutable list of lambda parameters.
     *
     * @return A hash code computed from the parameters.
     */
    private static int parameterHash(Iterable<Parameter> parameters) {
        int hash = 0;

        for (Parameter p : parameters) {
            if (p.name == null) {
                throw new IllegalArgumentException("Null parameter name.");
            } else if (p.type == null) {
                throw new IllegalArgumentException("Null parameter type.");
            }

            hash += p.name.hashCode() * 27 + p.type.hashCode();
            hash *= 49;
        }

        return hash;
    }

    // ===========================================================
    // Private Classes
    // ===========================================================

    /**
     * <p>
     * An helper class for iterating over the lambda expression's body.
     * </p>
     *
     * @author Hampton Smith
     *
     * @version 2.0
     */
    private class PLambdaBodyIterator implements PExpSubexpressionIterator {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * This flag indicates if we are done visiting the body.
         * </p>
         */
        private boolean myReturnedBodyFlag = false;

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>
         * This method returns {@code true} <strong>iff</strong> there are additional sub-expressions. I.e., returns
         * {@code true} <strong>iff</strong> {@link #next()} would return an element rather than throwing an exception.
         * </p>
         *
         * @return {@code true} if the iterator has more elements, {@code false} otherwise.
         */
        @Override
        public final boolean hasNext() {
            return !myReturnedBodyFlag;
        }

        /**
         * <p>
         * This method returns the next sub-expression./p>
         *
         * @return The next element in the iteration.
         */
        @Override
        public final PExp next() {
            if (myReturnedBodyFlag) {
                throw new NoSuchElementException();
            }

            return myBody;
        }

        /**
         * <p>
         * This method returns a version of the original {@link PExp} (i.e., the {@code PExp} over whose sub-expressions
         * we are iterating) with the sub-expression most recently returned by {@link #next()} replaced with
         * {@code newExpression}.
         * </p>
         *
         * @param newExpression
         *            The argument to replace the most recently returned one with.
         *
         * @return The new version.
         */
        @Override
        public final PExp replaceLast(PExp newExpression) {
            return new PLambda(parameters, myBody);
        }

    }

}
