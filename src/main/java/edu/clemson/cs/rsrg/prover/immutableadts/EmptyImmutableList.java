/*
 * EmptyImmutableList.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.prover.immutableadts;

import edu.clemson.cs.rsrg.prover.iterators.DummyIterator;
import java.util.Iterator;

/**
 * <p>This class implements an empty immutable list.</p>
 *
 * @param <E> Type of elements stored inside this list.
 *
 * @author Hampton Smith
 * @version 2.0
 */
public class EmptyImmutableList<E> extends AbstractImmutableList<E> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>A type safe iterator.</p> */
    private final Iterator<E> TYPESAFE_ITERATOR = (Iterator<E>) null;

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the element at the specified index.</p>
     *
     * @param index An index position.
     *
     * @return The element at the specified index.
     */
    @Override
    public final E get(int index) {
        throw new IndexOutOfBoundsException();
    }

    /**
     * <p>This method returns a new immutable sub-list from the head
     * to the specified index.</p>
     *
     * @param length Length of the sub-list.
     *
     * @return An immutable sub-list of the original list.
     */
    @Override
    public final ImmutableList<E> head(int length) {
        if (length != 0) {
            throw new IndexOutOfBoundsException();
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Iterator<E> iterator() {
        return DummyIterator.getInstance(TYPESAFE_ITERATOR);
    }

    /**
     * <p>This method returns the number of elements in
     * this list.</p>
     *
     * @return Number of elements.
     */
    @Override
    public final int size() {
        return 0;
    }

    /**
     * <p>This method returns a new immutable sub-list from the
     * specified start index to the end of our list.</p>
     *
     * @param startIndex An index position to start building
     *                   our sub-list.
     *
     * @return An immutable sub-list of the original list.
     */
    @Override
    public final ImmutableList<E> tail(int startIndex) {
        if (startIndex != 0) {
            throw new IndexOutOfBoundsException();
        }

        return this;
    }

}