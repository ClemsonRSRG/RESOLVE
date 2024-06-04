/*
 * SingletonIterator.java
 * ---------------------------------
 * Copyright (c) 2024
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

/**
 * <p>
 * This class implements the iterator class for a singleton element.
 * </p>
 *
 * @param <T>
 *            Element to to be iterated.
 *
 * @author Hampton Smith
 *
 * @version 2.0
 */
public class SingletonIterator<T> implements Iterator<T> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The singleton element to be iterated.
     * </p>
     */
    private final T myElement;

    /**
     * <p>
     * Flag that indicates whether or not we have iterated over this element yet.
     * </p>
     */
    private boolean myReturnedFlag = false;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Creates an iterator for a single element.
     * </p>
     *
     * @param element
     *            Element to be iterated.
     */
    public SingletonIterator(T element) {
        myElement = element;
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

        SingletonIterator<?> that = (SingletonIterator<?>) o;

        if (myReturnedFlag != that.myReturnedFlag)
            return false;
        return myElement.equals(that.myElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myElement.hashCode();
        result = 31 * result + (myReturnedFlag ? 1 : 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasNext() {
        return !myReturnedFlag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final T next() {
        myReturnedFlag = true;
        return myElement;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }

}
