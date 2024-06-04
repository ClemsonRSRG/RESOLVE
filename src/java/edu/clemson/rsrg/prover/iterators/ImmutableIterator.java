/*
 * ImmutableIterator.java
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
 * Wraps an existing {@link Iterator} and disables its {@link Iterator#remove()} method, ensuring that clients cannot
 * change the contents of encapsulated lists. Note that if the iterator returns mutable objects, the contained objects
 * themselves could still be changed.
 * </p>
 *
 * @param <T>
 *            Type of elements in this collection.
 *
 * @author Hampton Smith
 *
 * @version 2.0
 */
public class ImmutableIterator<T> implements Iterator<T> {

    /**
     * <p>
     * The inner iterator.
     * </p>
     */
    private final Iterator<T> myInnerIterator;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an immutable iterator.
     * </p>
     *
     * @param inner
     *            An {@link Iterator}.
     */
    public ImmutableIterator(Iterator<T> inner) {
        myInnerIterator = inner;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasNext() {
        return myInnerIterator.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final T next() {
        return myInnerIterator.next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void remove() {
        throw new UnsupportedOperationException("Iterator is immutable.");
    }

}
