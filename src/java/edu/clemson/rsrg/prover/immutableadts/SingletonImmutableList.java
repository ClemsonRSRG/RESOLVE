/*
 * SingletonImmutableList.java
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

import edu.clemson.rsrg.prover.iterators.SingletonIterator;
import java.util.Iterator;

/**
 * <p>
 * This class implements a singleton element immutable list.
 * </p>
 *
 * @param <E>
 *            Type of element stored inside this list.
 *
 * @author Hampton Smith
 *
 * @version 2.0
 */
public class SingletonImmutableList<E> extends AbstractImmutableList<E> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * An empty immutable list.
     * </p>
     */
    private final EmptyImmutableList<E> EMPTY;

    /**
     * <p>
     * The element stored inside this singleton list
     * </p>
     */
    private final E myElement;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a new immutable list that only contains the element {@code e}.
     * </p>
     *
     * @param e
     *            Element to be inserted.
     */
    public SingletonImmutableList(E e) {
        EMPTY = new EmptyImmutableList<>();
        myElement = e;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the element at the specified index.
     * </p>
     *
     * @param index
     *            An index position.
     *
     * @return The element at the specified index.
     */
    @Override
    public final E get(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }

        return myElement;
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
    public final ImmutableList<E> head(int length) {
        ImmutableList<E> retval;

        switch (length) {
            case 0:
                retval = EMPTY;
                break;
            case 1:
                retval = this;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return retval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Iterator<E> iterator() {
        return new SingletonIterator<>(myElement);
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
        return 1;
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
    public final ImmutableList<E> tail(int startIndex) {
        ImmutableList<E> retval;

        switch (startIndex) {
            case 0:
                retval = this;
                break;
            case 1:
                retval = EMPTY;
                break;
            default:
                throw new IndexOutOfBoundsException();
        }

        return retval;
    }

}
