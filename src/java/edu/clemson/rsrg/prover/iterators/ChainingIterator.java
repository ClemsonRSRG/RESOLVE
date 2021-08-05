/*
 * ChainingIterator.java
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
package edu.clemson.rsrg.prover.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * This class provides a single iteration construct for a pair of chained iterators.
 * </p>
 *
 * @param <T>
 *            Elements to to be iterated.
 *
 * @author Hampton Smith
 * 
 * @version 2.0
 */
public class ChainingIterator<T> implements Iterator<T> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The starting iterator.
     * </p>
     */
    private Iterator<T> myStartIterator;

    /**
     * <p>
     * A flag tha indicates if we have more elements to be iterated in the start iterator.
     * </p>
     */
    private boolean myStartHasNext = true;

    /**
     * <p>
     * The end iterator.
     * </p>
     */
    private Iterator<T> myEndIterator;

    /**
     * <p>
     * A flag tha indicates the last element from start iterator.
     * </p>
     */
    private boolean myLastFromStartFlag;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Creates a chained iterator from two iterators.
     * </p>
     *
     * @param start
     *            The starting iterator.
     * @param end
     *            The end iterator.
     */
    public ChainingIterator(Iterator<T> start, Iterator<T> end) {
        // TODO : This can be removed to increase performance
        if (start == null || end == null) {
            throw new IllegalArgumentException();
        }

        myStartIterator = start;
        myEndIterator = end;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ChainingIterator<?> that = (ChainingIterator<?>) o;

        if (myStartHasNext != that.myStartHasNext)
            return false;
        if (myLastFromStartFlag != that.myLastFromStartFlag)
            return false;
        if (myStartIterator != null ? !myStartIterator.equals(that.myStartIterator) : that.myStartIterator != null)
            return false;
        return myEndIterator != null ? myEndIterator.equals(that.myEndIterator) : that.myEndIterator == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myStartIterator != null ? myStartIterator.hashCode() : 0;
        result = 31 * result + (myStartHasNext ? 1 : 0);
        result = 31 * result + (myEndIterator != null ? myEndIterator.hashCode() : 0);
        result = 31 * result + (myLastFromStartFlag ? 1 : 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean hasNext() {
        if (myStartHasNext) {
            myStartHasNext = myStartIterator.hasNext();
        }

        return (myStartHasNext || myEndIterator.hasNext());
    }

    /**
     * {@inheritDoc}
     */
    public final T next() {
        T retval;

        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        if (myStartHasNext) {
            retval = myStartIterator.next();
            myLastFromStartFlag = true;
        } else {
            retval = myEndIterator.next();
            myLastFromStartFlag = false;
        }

        return retval;
    }

    /**
     * {@inheritDoc}
     */
    public final void remove() {
        if (myLastFromStartFlag) {
            myStartIterator.remove();
        } else {
            myEndIterator.remove();
        }
    }

}
