/*
 * LazilyMappedImmutableList.java
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
package edu.clemson.rsrg.prover.immutableadts;

import edu.clemson.rsrg.misc.Utilities.Mapping;
import edu.clemson.rsrg.prover.iterators.ImmutableIterator;
import java.util.Iterator;

/**
 * <p>
 * This class implements a lazy mapping for elements from an immutable list to an element of type {@link R}.
 * </p>
 *
 * @param <T>
 *            Type of elements stored inside this list.
 * @param <R>
 *            Mapped type for the elements stored inside this list.
 *
 * @author Hampton Smith
 *
 * @version 2.0
 */
public class LazilyMappedImmutableList<T, R> extends AbstractImmutableList<R> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The original immutable list.
     * </p>
     */
    private final ImmutableList<T> myOriginalList;

    /**
     * <p>
     * A cache of mapped elements.
     * </p>
     */
    private final R[] myMappedCache;

    /**
     * <p>
     * A mapping from elements of type {@link T} to type {@link R}.
     * </p>
     */
    private final Mapping<T, R> myMapping;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a new immutable list based on {@code original}, in which each entry in this new list will be the
     * sister entry in that original list, filtered through {@code m}. {@code m} must represent a functional
     * mapping--that is, if {@code x.equals(y)}, then {@code m.map(x).equals(m.map(y))} in all cases, otherwise the
     * resulting list may appear to "mutate" to the client, despite the original underlying list remaining unchanged.
     * </p>
     *
     * @param original
     *            The original list.
     * @param m
     *            The mapping to apply to each entry.
     */
    @SuppressWarnings("unchecked")
    public LazilyMappedImmutableList(ImmutableList<T> original, Mapping<T, R> m) {
        myOriginalList = original;
        myMapping = m;
        myMappedCache = (R[]) new Object[myOriginalList.size()];
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the mapped element at the specified index.
     * </p>
     *
     * @param index
     *            An index position.
     *
     * @return The element at the specified index.
     */
    @Override
    public final R get(int index) {
        R result = myMappedCache[index];

        if (result == null) {
            result = myMapping.map(myOriginalList.get(index));
            myMappedCache[index] = result;
        }

        return result;
    }

    /**
     * <p>
     * This method returns a new immutable sub-list from the head to the specified index.
     * </p>
     *
     * @param length
     *            Length of the sub-list.
     *
     * @return An immutable sub-list of the original list.
     */
    @Override
    public final ImmutableList<R> head(int length) {
        return new LazilyMappedImmutableList<>(myOriginalList.head(length), myMapping);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Iterator<R> iterator() {
        return new ImmutableIterator<>(new CacheCheckingIterator());
    }

    /**
     * <p>
     * This method returns the number of elements in this list.
     * </p>
     *
     * @return Number of elements.
     */
    @Override
    public final int size() {
        return myOriginalList.size();
    }

    /**
     * <p>
     * This method returns a new immutable sub-list from the specified start index to the end of our list.
     * </p>
     *
     * @param startIndex
     *            An index position to start building our sub-list.
     *
     * @return An immutable sub-list of the original list.
     */
    @Override
    public final ImmutableList<R> tail(int startIndex) {
        return new LazilyMappedImmutableList<>(myOriginalList.tail(startIndex), myMapping);
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>
     * An helper class that returns objects from our cache.
     * </p>
     */
    private class CacheCheckingIterator implements Iterator<R> {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * The iterator for the original immutable list.
         * </p>
         */
        private Iterator<T> myOriginalIterator = myOriginalList.iterator();

        /**
         * <p>
         * Our current iteration index.
         * </p>
         */
        private int myIndex = 0;

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * {@inheritDoc}
         */
        @Override
        public final boolean hasNext() {
            return myOriginalIterator.hasNext();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final R next() {
            R result = myMappedCache[myIndex];

            T nextOriginalElement = myOriginalIterator.next();
            if (result == null) {
                result = myMapping.map(nextOriginalElement);
                myMappedCache[myIndex] = result;
            }

            myIndex++;

            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final void remove() {
            myOriginalIterator.remove();
        }

    }

}
