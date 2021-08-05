/*
 * PAlternatives.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.prover.absyn.expressions;

import edu.clemson.rsrg.absyn.expressions.mathexpr.AltItemExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.AlternativeExp;
import edu.clemson.rsrg.misc.RCollections;
import edu.clemson.rsrg.misc.Utilities;
import edu.clemson.rsrg.prover.absyn.PExp;
import edu.clemson.rsrg.prover.absyn.iterators.PExpSubexpressionIterator;
import edu.clemson.rsrg.prover.absyn.visitors.PExpVisitor;
import edu.clemson.rsrg.prover.exception.BindingException;
import edu.clemson.rsrg.prover.immutableadts.ArrayBackedImmutableList;
import edu.clemson.rsrg.prover.immutableadts.ImmutableList;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.*;

/**
 * <p>
 * A {@code PAlternatives} represents an alternative in an {@code if-else} expression.
 * </p>
 *
 * @author Daniel Welch
 * 
 * @version 2.0
 */
public class PAlternatives extends PExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A list of alternative expressions.
     * </p>
     */
    public final List<Alternative> myAlternatives;

    /**
     * <p>
     * An expression that contains the otherwise clause.
     * </p>
     */
    public final PExp myOtherwiseClauseResult;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This represents all alternative expressions in an {@code if-else} expression.
     * </p>
     *
     * @param conditions
     *            A list of conditions for the expression.
     * @param results
     *            A list of resulting expressions.
     * @param otherwiseClauseResult
     *            An otherwise clause.
     * @param type
     *            The expression's mathematical type.
     * @param typeValue
     *            The expression's mathematical type value.
     */
    public PAlternatives(List<PExp> conditions, List<PExp> results, PExp otherwiseClauseResult, MTType type,
            MTType typeValue) {
        super(calculateStructureHash(conditions), calculateStructureHash(results), type, typeValue);

        myAlternatives = new LinkedList<>();

        sanityCheckConditions(conditions);

        if (conditions.size() != results.size()) {
            throw new IllegalArgumentException("conditions.size() must equal " + "results.size().");
        }

        Iterator<PExp> conditionIter = conditions.iterator();
        Iterator<PExp> resultIter = results.iterator();

        while (conditionIter.hasNext()) {
            myAlternatives.add(new Alternative(conditionIter.next(), resultIter.next()));
        }

        myOtherwiseClauseResult = otherwiseClauseResult;
    }

    /**
     * <p>
     * This represents all alternative expressions in an {@code if-else} expression.
     * </p>
     *
     * @param alternativeExp
     *            A raw alternative expression.
     */
    public PAlternatives(AlternativeExp alternativeExp) {
        this(getConditions(alternativeExp), getResults(alternativeExp), getOtherwiseClauseResult(alternativeExp),
                alternativeExp.getMathType(), alternativeExp.getMathTypeValue());
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
        v.beginPAlternatives(this);

        v.beginChildren(this);

        boolean first = true;
        for (Alternative alt : myAlternatives) {
            if (!first) {
                v.fencepostPAlternatives(this);
            } else {
                first = false;
            }

            alt.result.accept(v);
            alt.condition.accept(v);
        }
        v.fencepostPAlternatives(this);

        myOtherwiseClauseResult.accept(v);

        v.endChildren(this);

        v.endPAlternatives(this);
        v.endPExp(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void bindTo(PExp target, Map<PExp, PExp> accumulator) throws BindingException {

        if (!(target instanceof PAlternatives)) {
            throw BINDING_EXCEPTION;
        }

        PAlternatives targetAsPAlternatives = (PAlternatives) target;

        if (myAlternatives.size() != targetAsPAlternatives.myAlternatives.size()) {
            throw BINDING_EXCEPTION;
        }

        Iterator<Alternative> thisAlternatives = myAlternatives.iterator();
        Iterator<Alternative> targetAlternatives = targetAsPAlternatives.myAlternatives.iterator();

        Alternative curThisAlt, curTargetAlt;
        while (thisAlternatives.hasNext()) {
            curThisAlt = thisAlternatives.next();
            curTargetAlt = targetAlternatives.next();

            curThisAlt.result.bindTo(curTargetAlt.result, accumulator);
            curThisAlt.condition.bindTo(curTargetAlt.condition, accumulator);
        }

        myOtherwiseClauseResult.bindTo(targetAsPAlternatives.myOtherwiseClauseResult, accumulator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExistential() {
        boolean result = false;

        for (Alternative a : myAlternatives) {
            result |= a.condition.containsExistential();
            result |= a.result.containsExistential();
        }

        return result || myOtherwiseClauseResult.containsExistential();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsName(String name) {
        boolean result = false;

        for (Alternative a : myAlternatives) {
            result |= a.condition.containsName(name) || a.result.containsName(name);
        }

        return result || myOtherwiseClauseResult.containsName(name);
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

        PAlternatives that = (PAlternatives) o;

        if (!myAlternatives.equals(that.myAlternatives))
            return false;
        return myOtherwiseClauseResult.equals(that.myOtherwiseClauseResult);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PExp flipQuantifiers() {
        throw new UnsupportedOperationException("This method has not yet " + "been implemented.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ImmutableList<PExp> getSubExpressions() {
        List<PExp> exps = new LinkedList<>();

        for (Alternative a : myAlternatives) {
            exps.add(a.result);
            exps.add(a.condition);
        }

        exps.add(myOtherwiseClauseResult);

        return new ArrayBackedImmutableList<>(exps);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PExpSubexpressionIterator getSubExpressionIterator() {
        return new PAlternativesIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getTopLevelOperation() {
        return "{{";
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
        boolean result = true;

        for (Alternative a : myAlternatives) {
            result &= a.result.isObviouslyTrue();
        }

        return result && myOtherwiseClauseResult.isObviouslyTrue();
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
            List<PExp> conditions = new ArrayList<>();
            List<PExp> results = new ArrayList<>();
            PExp otherwise = myOtherwiseClauseResult.substitute(substitutions);
            for (Alternative a : myAlternatives) {
                conditions.add(a.condition.substitute(substitutions));
                results.add(a.result.substitute(substitutions));
            }
            retval = new PAlternatives(conditions, results, otherwise, getMathType(), getMathTypeValue());
        }

        return retval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PAlternatives withSubExpressionReplaced(int index, PExp e) {
        List<PExp> newResults = RCollections.map(myAlternatives, UnboxResult.INSTANCE);
        List<PExp> newConditions = RCollections.map(myAlternatives, UnboxCondition.INSTANCE);
        PExp newOtherwise = myOtherwiseClauseResult;

        if (index < 0 || index > (myAlternatives.size() * 2) + 1) {
            throw new IndexOutOfBoundsException("" + index);
        } else {
            if (index % 2 == 0) {
                index /= 2;
                if (index < myAlternatives.size()) {
                    newResults.set(index, e);
                } else {
                    newOtherwise = e;
                }
            } else {
                newConditions.set(index / 2, e);
            }
        }

        return new PAlternatives(newConditions, newResults, newOtherwise, myMathType, myMathTypeValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PAlternatives withTypeReplaced(MTType t) {
        return new PAlternatives(RCollections.map(myAlternatives, UnboxCondition.INSTANCE),
                RCollections.map(myAlternatives, UnboxResult.INSTANCE), myOtherwiseClauseResult, t, myMathTypeValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PAlternatives withTypeValueReplaced(MTType t) {
        return new PAlternatives(RCollections.map(myAlternatives, UnboxCondition.INSTANCE),
                RCollections.map(myAlternatives, UnboxResult.INSTANCE), myOtherwiseClauseResult, myMathType, t);
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final List<PExp> getFunctionApplicationsNoCache() {
        List<PExp> result = new LinkedList<>();

        for (Alternative a : myAlternatives) {
            result.addAll(a.condition.getFunctionApplications());
            result.addAll(a.result.getFunctionApplications());
        }

        result.addAll(myOtherwiseClauseResult.getFunctionApplications());

        result.add(this);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Set<PSymbol> getQuantifiedVariablesNoCache() {
        Set<PSymbol> result = new HashSet<>();

        for (Alternative a : myAlternatives) {
            result.addAll(a.condition.getQuantifiedVariables());
            result.addAll(a.result.getQuantifiedVariables());
        }

        result.addAll(myOtherwiseClauseResult.getQuantifiedVariables());

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Set<String> getSymbolNamesNoCache() {
        Set<String> result = new HashSet<>();

        for (Alternative a : myAlternatives) {
            result.addAll(a.condition.getSymbolNames());
            result.addAll(a.result.getSymbolNames());
        }

        result.addAll(myOtherwiseClauseResult.getSymbolNames());

        return result;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method for calculating the structure hash from a list of {@link PExp PExps}.
     * </p>
     *
     * @param conditions
     *            A list of expressions.
     *
     * @return An hash value.
     */
    private static int calculateStructureHash(List<PExp> conditions) {
        int hash = 0;

        Iterator<PExp> conditionIter = conditions.iterator();
        Iterator<PExp> resultIter = conditions.iterator();

        while (conditionIter.hasNext()) {
            hash *= 31;
            hash += conditionIter.next().structureHash;
            hash *= 34;
            hash += resultIter.next().structureHash;
        }

        return hash;
    }

    /**
     * <p>
     * An helper method for converting all testing conditions into {@link PExp PExps}.
     * </p>
     *
     * @param alternativeExp
     *            A raw alternative expression.
     *
     * @return A list of condition expressions.
     */
    private static List<PExp> getConditions(AlternativeExp alternativeExp) {
        List<PExp> result = new LinkedList<>();
        for (AltItemExp aie : alternativeExp.getAlternatives()) {
            if (aie.getTest() != null) {
                result.add(PExp.buildPExp(aie.getMathType().getTypeGraph(), aie.getTest()));
            }
        }

        return result;
    }

    /**
     * <p>
     * An helper method for converting the otherwise clause into a {@link PExp}.
     * </p>
     *
     * @param alternativeExp
     *            A raw alternative expression.
     *
     * @return An otherwise expression.
     */
    private static PExp getOtherwiseClauseResult(AlternativeExp alternativeExp) {
        PExp workingOtherwiseClauseResult = null;

        for (AltItemExp aie : alternativeExp.getAlternatives()) {
            if (workingOtherwiseClauseResult != null) {
                throw new IllegalArgumentException(
                        "AlternativeExps with " + "additional alternatives after the 'otherwise' "
                                + "clause are not accepted by the prover. \n\t" + aie.getAssignment()
                                + " appears in such a position.");
            }

            if (aie.getTest() == null) {
                workingOtherwiseClauseResult = PExp.buildPExp(aie.getMathType().getTypeGraph(), aie.getAssignment());
            }
        }

        return workingOtherwiseClauseResult;
    }

    /**
     * <p>
     * An helper method for converting all resulting expressions into {@link PExp PExps}.
     * </p>
     *
     * @param alternativeExp
     *            A raw alternative expression.
     *
     * @return A list of result expressions.
     */
    private static List<PExp> getResults(AlternativeExp alternativeExp) {
        List<PExp> result = new LinkedList<>();
        for (AltItemExp aie : alternativeExp.getAlternatives()) {
            if (aie.getTest() != null) {
                result.add(PExp.buildPExp(aie.getMathType().getTypeGraph(), aie.getAssignment()));
            }
        }

        return result;
    }

    /**
     * <p>
     * An helper method for obtaining the mathematical type for the resulting expression.
     * </p>
     *
     * @param results
     *            A list of resulting expressions.
     * @param otherwiseClauseResult
     *            An otherwise clause.
     *
     * @return A mathematical type
     */
    private static MTType getResultType(List<PExp> results, PExp otherwiseClauseResult) {
        // TODO : This could be made more flexible--if the first alternative
        // is an N and the second a Z, that shouldn't be an error--the
        // result type is Z
        PExp prototypeResult = null;

        for (PExp curResult : results) {
            if (prototypeResult == null) {
                prototypeResult = curResult;
            } else {
                if (!curResult.typeMatches(prototypeResult)) {
                    throw new IllegalArgumentException("AlternativeExps with "
                            + "results of different types are not accepted by " + "the prover. \n\t" + prototypeResult
                            + " has " + "type " + prototypeResult.getMathType() + ".\n\t" + curResult + " has type "
                            + curResult.getMathType() + ".");
                }
            }
        }

        if (!otherwiseClauseResult.typeMatches(prototypeResult)) {
            throw new IllegalArgumentException(
                    "AlternativeExps with " + "results of different types are not accepted by " + "the prover. \n\t"
                            + prototypeResult + " has " + "type " + prototypeResult.getMathType() + ".\n\t"
                            + otherwiseClauseResult + " has type " + otherwiseClauseResult.getMathType() + ".");
        }

        return prototypeResult.getMathType();
    }

    /**
     * <p>
     * An helper method for sanity checking the condition expressions to make sure they are all {@code boolean} type.
     * </p>
     *
     * @param conditions
     *            A list of conditions for the expression.
     */
    private void sanityCheckConditions(List<PExp> conditions) {
        for (PExp condition : conditions) {
            if (!condition.getMathType().isBoolean()) {
                throw new IllegalArgumentException(
                        "AlternativeExps with " + "non-boolean-typed conditions are not accepted "
                                + "by the prover. \n\t" + condition + " has type " + condition.getMathType());
            }
        }
    }

    // ===========================================================
    // Private Classes
    // ===========================================================

    /**
     * <p>
     * An helper class for representing an alternative condition and result expressions.
     * </p>
     *
     * @version 2.0
     */
    public static class Alternative {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * A condition expression.
         * </p>
         */
        public final PExp condition;

        /**
         * <p>
         * A result expression.
         * </p>
         */
        public final PExp result;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * A representation of an alternative expression.
         * </p>
         *
         * @param condition
         *            A condition expression.
         * @param result
         *            A result expression.
         */
        public Alternative(PExp condition, PExp result) {
            this.condition = condition;
            this.result = result;
        }

    }

    /**
     * <p>
     * An helper class for iterating over the alternative expression.
     * </p>
     *
     * @version 2.0
     */
    private class PAlternativesIterator implements PExpSubexpressionIterator {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * A counter for keeping track the current alternative expression visited.
         * </p>
         */
        private int myCurAlternativeNum;

        // These variables combine to tell you what the last thing returned was:
        // if myReturnedOtherwiseFlag == true, the last thing returned was the
        // otherwise clause and there's nothing left to return. Otherwise, if
        // myCurAlternative == null, the last thing returned was the condition
        // of the (myCurAlternativeNum)th element. Otherwise (if
        // myCurAlternative != null), the last thing returned was the result of
        // the (myCurAlternativeNum)th element.

        /**
         * <p>
         * An iterator for the list of alternatives.
         * </p>
         */
        private final Iterator<Alternative> myAlternativesIter;

        /**
         * <p>
         * The current alternative expression.
         * </p>
         */
        private Alternative myCurAlternative;

        /**
         * <p>
         * A flag that indicates we have encountered the otherwise expression.
         * </p>
         */
        private boolean myReturnedOtherwiseFlag = false;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * This creates an iterator for alternative expressions.
         * </p>
         */
        PAlternativesIterator() {
            myAlternativesIter = myAlternatives.iterator();
        }

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
            return (myCurAlternative != null) || (myAlternativesIter.hasNext()) || !myReturnedOtherwiseFlag;
        }

        /**
         * <p>
         * This method returns the next sub-expression./p>
         *
         * @return The next element in the iteration.
         */
        @Override
        public final PExp next() {
            PExp result;

            if (myCurAlternative == null) {
                if (myAlternativesIter.hasNext()) {
                    myCurAlternativeNum++;

                    myCurAlternative = myAlternativesIter.next();
                    result = myCurAlternative.result;
                } else if (!myReturnedOtherwiseFlag) {
                    myReturnedOtherwiseFlag = true;
                    result = myOtherwiseClauseResult;
                } else {
                    throw new NoSuchElementException();
                }
            } else {
                result = myCurAlternative.condition;
                myCurAlternative = null;
            }

            return result;
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
        public final PAlternatives replaceLast(PExp newExpression) {
            List<PExp> newConditions = RCollections.map(myAlternatives, UnboxCondition.INSTANCE);
            List<PExp> newResults = RCollections.map(myAlternatives, UnboxResult.INSTANCE);
            PExp newOtherwise = myOtherwiseClauseResult;

            if (myReturnedOtherwiseFlag) {
                newOtherwise = newExpression;
            } else {
                if (myCurAlternative == null) {
                    newConditions.set(myCurAlternativeNum, newExpression);
                } else {
                    newResults.set(myCurAlternativeNum, newExpression);
                }
            }

            return new PAlternatives(newConditions, newResults, newOtherwise, myMathType, myMathTypeValue);
        }
    }

    /**
     * <p>
     * An helper class for unboxing the condition expression.
     * </p>
     *
     * @version 2.0
     */
    private static class UnboxCondition implements Utilities.Mapping<Alternative, PExp> {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * An instance of this class.
         * </p>
         */
        public final static UnboxCondition INSTANCE = new UnboxCondition();

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * This creates an object for unboxing the condition expression.
         * </p>
         */
        private UnboxCondition() {
        }

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>
         * This returns the inner {@link PExp} representation of a condition expression in {@code a}.
         * </p>
         *
         * @param a
         *            An alternative expression.
         *
         * @return A {@link PExp}.
         */
        public PExp map(Alternative a) {
            return a.condition;
        }

    }

    /**
     * <p>
     * An helper class for unboxing the result expression.
     * </p>
     *
     * @version 2.0
     */
    private static class UnboxResult implements Utilities.Mapping<Alternative, PExp> {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * An instance of this class.
         * </p>
         */
        public final static UnboxResult INSTANCE = new UnboxResult();

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * This creates an object for unboxing the result expression.
         * </p>
         */
        private UnboxResult() {
        }

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>
         * This returns the inner {@link PExp} representation of a result expression in {@code a}.
         * </p>
         *
         * @param a
         *            An alternative expression.
         *
         * @return A {@link PExp}.
         */
        public PExp map(Alternative a) {
            return a.result;
        }

    }

}
