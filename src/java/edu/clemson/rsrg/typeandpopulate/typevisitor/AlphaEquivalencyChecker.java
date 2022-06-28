/*
 * AlphaEquivalencyChecker.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.typevisitor;

import edu.clemson.rsrg.typeandpopulate.mathtypes.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * <p>
 * This class visits two mathematical types to see if they are alpha-equivalent to each other.
 * </p>
 *
 * @version 2.0
 */
public class AlphaEquivalencyChecker extends SymmetricBoundVariableVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A limit for how many checkers we can have.
     * </p>
     */
    private static final int POOL_SIZE = 3;

    /**
     * <p>
     * An object pool to cut down on the creation of <code>AlphaEquivalencyCheckers</code>.
     * </p>
     */
    private final Deque<AlphaEquivalencyChecker> myCheckerPool;

    /**
     * <p>
     * The result from the alpha-equivalency check.
     * </p>
     */
    private boolean myResult;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a checker for checking for alpha-equivalency.
     * </p>
     */
    public AlphaEquivalencyChecker() {
        myCheckerPool = new ArrayDeque<>(POOL_SIZE);

        for (int i = 0; i < POOL_SIZE; i++) {
            myCheckerPool.push(new AlphaEquivalencyChecker(myCheckerPool));
        }
    }

    /**
     * <p>
     * This constructs an alpha-equivalency checker instance with the provided pool of checkers.
     * </p>
     *
     * @param pool
     *            A pool of existing checkers.
     */
    private AlphaEquivalencyChecker(Deque<AlphaEquivalencyChecker> pool) {
        myCheckerPool = pool;
    }

    /**
     * <p>
     * This creates an empty pool of checkers.
     * </p>
     *
     * @param dummy
     *            A dummy variable that is not being used.
     */
    private AlphaEquivalencyChecker(boolean dummy) {
        myCheckerPool = new ArrayDeque<>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method adds additional logic before we visit two {@link MTFunctionApplication} by checking for equality.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return The updated result if they are not equal or we keep the original result value.
     */
    @Override
    public final boolean beginMTFunctionApplication(MTFunctionApplication t1, MTFunctionApplication t2) {
        // myResult = (t1.getName().equals(t2.getName()));

        // Not sure if this is the right fix.... -BD
        myResult = (t1.hashCode() == t2.hashCode());

        return myResult;
    }

    /**
     * <p>
     * This method adds additional logic before we visit two {@link MTNamed} by checking for alpha equivalency.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return The updated result from the check.
     */
    @Override
    public final boolean beginMTNamed(MTNamed t1, MTNamed t2) {
        // TODO: This doesn't deal correctly with multiple appearances of a
        // variable

        if (!t1.getName().equals(t2.getName())) {
            MTType t1Value;
            MTType t2Value;
            try {
                t1Value = getInnermostBinding1(t1.getName());
                t2Value = getInnermostBinding2(t2.getName());

                AlphaEquivalencyChecker alphaEq = getChecker();
                alphaEq.visit(t1Value, t2Value);
                myResult = alphaEq.getResult();
                returnChecker(alphaEq);
            } catch (NoSuchElementException nsee) {
                // We have no information about the named types--but we know they
                // aren't named the same, so...
                myResult = false;
            }
        }

        return myResult;
    }

    /**
     * <p>
     * This method adds additional logic before we visit two {@link MTProper} by setting the final return value to false
     * if {@code t1 != t2}.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return The updated result if they are not equal or we keep the original result value.
     */
    @Override
    public final boolean beginMTProper(MTProper t1, MTProper t2) {
        if (t1 != t2) {
            myResult = false;
        }

        return myResult;
    }

    /**
     * <p>
     * This method adds additional logic before we visit two {@link MTSetRestriction}.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return Currently, this method always throws an exception.
     */
    @Override
    public final boolean beginMTSetRestriction(MTSetRestriction t1, MTSetRestriction t2) {
        // TODO:
        // We really need a way to check the expression embedded in each set
        // restriction for alpha-equivalency. We don't have one, so for the
        // moment, we throw an exception
        throw new RuntimeException("Can't check set restrictions for " + "alpha equivalency.");
    }

    /**
     * <p>
     * This method returns the final result for checking alpha equivalency between two {@link MTType MTTypes}.
     * </p>
     *
     * @return {@code true} if they are alpha-equivalent, {@code false} otherwise.
     */
    public final boolean getResult() {
        return myResult;
    }

    /**
     * <p>
     * This method provides logic for handling type mismatches.
     * </p>
     *
     * @param t1
     *            A math type.
     * @param t2
     *            A math type.
     *
     * @return This method always returns {@code false}.
     */
    @Override
    public final boolean mismatch(MTType t1, MTType t2) {
        myResult = false;
        return myResult;
    }

    /**
     * <p>
     * This method resets this symmetric visitor.
     * </p>
     */
    @Override
    public final void reset() {
        super.reset();
        myResult = true;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This method only creates a new checker if needed.
     * </p>
     *
     * @return A checker for handling alpha equivalency.
     */
    private AlphaEquivalencyChecker getChecker() {
        AlphaEquivalencyChecker result;

        if (myCheckerPool.isEmpty()) {
            result = new AlphaEquivalencyChecker(false);
        } else {
            result = myCheckerPool.pop();
        }

        return result;
    }

    /**
     * <p>
     * This method returns a checker to our available pool of checkers.
     * </p>
     *
     * @param c
     *            The alpha equivalency checker to be returned.
     */
    private void returnChecker(AlphaEquivalencyChecker c) {
        if (myCheckerPool.size() < POOL_SIZE) {
            myCheckerPool.push(c);
        }
    }

}
