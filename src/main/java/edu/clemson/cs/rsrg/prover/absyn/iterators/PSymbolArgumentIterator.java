/*
 * PSymbolArgumentIterator.java
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
package edu.clemson.cs.rsrg.prover.absyn.iterators;

import edu.clemson.cs.rsrg.prover.absyn.PExp;
import edu.clemson.cs.rsrg.prover.absyn.expressions.PSymbol;
import java.util.Iterator;

/**
 * <p>
 * An implementation that allows the user to iterate over a {@link PSymbol
 * PSymbol's} arguments.
 * </p>
 *
 * @author Hampton Smith
 * @version 2.0
 */
public class PSymbolArgumentIterator implements PExpSubexpressionIterator {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The {@link PSymbol} whose arguments are being iterated.
     * </p>
     */
    private final PSymbol myOriginalSymbol;

    /**
     * <p>
     * An inner iterator for arguments.
     * </p>
     */
    private final Iterator<PExp> myArgumentIterator;

    /**
     * <p>
     * An index representing that last returned argument.
     * </p>
     */
    private int myLastReturnedIndex = -1;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an iterator for visiting {@code s}'s arguments (if any).
     * </p>
     *
     * @param s A {@link PSymbol}.
     */
    public PSymbolArgumentIterator(PSymbol s) {
        myOriginalSymbol = s;
        myArgumentIterator = s.arguments.iterator();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns {@code true} <strong>iff</strong> there are
     * additional sub-expressions.
     * I.e., returns {@code true} <strong>iff</strong> {@link #next()} would
     * return an element rather
     * than throwing an exception.
     * </p>
     *
     * @return {@code true} if the iterator has more elements, {@code false}
     *         otherwise.
     */
    @Override
    public final boolean hasNext() {
        return myArgumentIterator.hasNext();
    }

    /**
     * <p>
     * This method returns the next sub-expression./p>
     *
     * @return The next element in the iteration.
     */
    @Override
    public final PExp next() {
        PExp retval = myArgumentIterator.next();
        myLastReturnedIndex++;

        return retval;
    }

    /**
     * <p>
     * This method returns a version of the original {@link PExp} (i.e., the
     * {@code PExp} over whose
     * sub-expressions we are iterating) with the sub-expression most recently
     * returned by
     * {@link #next()} replaced with {@code newExpression}.
     * </p>
     *
     * @param newExpression The argument to replace the most recently returned
     *        one with.
     *
     * @return The new version.
     */
    @Override
    public final PExp replaceLast(PExp newExpression) {
        if (myLastReturnedIndex == -1) {
            throw new IllegalStateException("Must call next() first.");
        }

        return myOriginalSymbol.setArgument(myLastReturnedIndex, newExpression);
    }

}
