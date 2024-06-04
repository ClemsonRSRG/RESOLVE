/*
 * DummyIterator.java
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
import java.util.NoSuchElementException;

/**
 * <p>
 * This class serves as a dummy iterator.
 * </p>
 *
 * @param <T>
 *            Element to to be iterated.
 *
 * @author Hampton Smith
 *
 * @version 2.0
 */
public class DummyIterator<T> implements Iterator<T> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A singleton instance of this class.
     * </p>
     */
    private final static DummyIterator<Object> INSTANCE = new DummyIterator<>();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Creates a dummy iterator.
     * </p>
     */
    private DummyIterator() {
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Returns an instance of this iterator.
     * </p>
     *
     * @param i
     *            An iterator.
     * @param <T>
     *            Element to to be iterated.
     *
     * @return A {@code DummyIterator} instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> getInstance(Iterator<T> i) {
        return (Iterator<T>) INSTANCE;
    }

    /**
     * <p>
     * Returns an instance of this iterator.
     * </p>
     *
     * @param <T>
     *            Element to to be iterated.
     *
     * @return A {@code DummyIterator} instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> getInstance() {
        return (Iterator<T>) INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasNext() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final T next() {
        throw new NoSuchElementException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }

}
