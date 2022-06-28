/*
 * PExpSubexpressionIterator.java
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
package edu.clemson.rsrg.prover.absyn.iterators;

import edu.clemson.rsrg.prover.absyn.PExp;
import java.util.NoSuchElementException;

/**
 * <p>
 * A {@code PExpSubexpressionIterator} defines the interface for classes that iterate over the sub-expressions of a
 * {@link PExp}, with the ability to get a version of the original {@code PExp} in which any given sub-expression has
 * been replaced with another.
 * </p>
 *
 * @author Hampton Smith
 *
 * @version 2.0
 */
public interface PExpSubexpressionIterator {

    /**
     * <p>
     * This method returns {@code true} <strong>iff</strong> there are additional sub-expressions. I.e., returns
     * {@code true} <strong>iff</strong> {@link #next()} would return an element rather than throwing an exception.
     * </p>
     *
     * @return {@code true} if the iterator has more elements, {@code false} otherwise.
     */
    boolean hasNext();

    /**
     * <p>
     * This method returns the next sub-expression./p>
     *
     * @return The next element in the iteration.
     *
     * @throws NoSuchElementException
     *             If there are no further subexpressions.
     */
    PExp next();

    /**
     * <p>
     * This method returns a version of the original {@link PExp} (i.e., the {@code PExp} over whose sub-expressions we
     * are iterating) with the sub-expression most recently returned by {@link #next()} replaced with
     * {@code newExpression}.
     * </p>
     *
     * @param newExpression
     *            The argument to replace the most recently returned one with.
     *
     * @return The new version.
     */
    PExp replaceLast(PExp newExpression);

}
