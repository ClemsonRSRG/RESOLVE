/*
 * ImmutableListSubview.java
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
package edu.clemson.rsrg.prover.immutableadts;

import java.util.Iterator;

/**
 * <p>
 * This class implements an immutable view of an immutable sub-list.
 * </p>
 *
 * @param <E>
 *            Type of elements stored inside this list.
 *
 * @author Hampton Smith
 *
 * @version 2.0
 */
public class ImmutableListSubview<E> extends AbstractImmutableList<E> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The original array-based immutable list.
     * </p>
     */
    private final ArrayBackedImmutableList<E> myBaseList;

    /**
     * <p>
     * Start index for this sub-list.
     * </p>
     */
    private final int mySubviewStart;

    /**
     * <p>
     * Length for this sub-list.
     * </p>
     */
    private final int mySubviewLength;

    /**
     * <p>
     * Index for the element after this view.
     * </p>
     */
    private final int myFirstAfterIndex;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a immutable view for an array-based immutable list.
     * </p>
     *
     * @param baseList
     *            The original immutable list.
     * @param start
     *            An index position to start building our sub-list.
     * @param length
     *            Length of the sub-list.
     */
    public ImmutableListSubview(ArrayBackedImmutableList<E> baseList, int start, int length) {
        // TODO : These defensive checks can be taken out for efficiency once
        // we're satisfied that ImmutableLists works correctly.
        if (start + length > baseList.size()) {
            throw new IllegalArgumentException("View exceeds source bounds.");
        }

        if (length < 0) {
            throw new IllegalArgumentException("Negative length.");
        }

        if (start < 0) {
            throw new IllegalArgumentException("Negative start.");
        }

        myBaseList = baseList;
        mySubviewStart = start;
        mySubviewLength = length;
        myFirstAfterIndex = mySubviewStart + mySubviewLength;
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
        if (index < 0 || index >= myFirstAfterIndex) {
            throw new IndexOutOfBoundsException();
        }

        return myBaseList.get(index + mySubviewStart);
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
        if (length > mySubviewLength) {
            throw new IndexOutOfBoundsException();
        }

        return new ImmutableListSubview<>(myBaseList, mySubviewStart, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Iterator<E> iterator() {
        return myBaseList.subsequenceIterator(mySubviewStart, mySubviewLength);
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
        return mySubviewLength;
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
        if (startIndex < 0 || startIndex > mySubviewLength) {
            throw new IndexOutOfBoundsException();
        }

        return new ImmutableListSubview<>(myBaseList, startIndex + mySubviewStart, mySubviewLength - startIndex);
    }
}
