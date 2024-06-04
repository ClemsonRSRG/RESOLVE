/*
 * ArrayBackedImmutableList.java
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

import edu.clemson.rsrg.prover.iterators.ArrayIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * This class implements an array-based immutable list.
 * </p>
 *
 * @param <E>
 *            Type of elements stored inside this list.
 *
 * @author Hampton Smith
 *
 * @version 2.0
 */
public class ArrayBackedImmutableList<E> extends AbstractImmutableList<E> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Elements in this immutable list.
     * </p>
     */
    private final E[] myElements;

    /**
     * <p>
     * Number of elements in this immutable list.
     * </p>
     */
    private final int myElementsLength;

    /**
     * <p>
     * The computed hash code for this immutable list.
     * </p>
     */
    private final int myHashCode;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a new array-backed immutable list using the specified iterator.
     * </p>
     *
     * @param i
     *            An {@link Iterator}.
     */
    @SuppressWarnings("unchecked")
    public ArrayBackedImmutableList(Iterable<E> i) {
        List<E> tempList = new ArrayList<>();

        for (E e : i) {
            tempList.add(e);
        }

        myElements = (E[]) tempList.toArray();
        myElementsLength = myElements.length;
        myHashCode = calculateHashCode();
    }

    /**
     * <p>
     * This creates a new array-backed immutable list using the specified array of elements.
     * </p>
     *
     * @param i
     *            An array of elements.
     */
    public ArrayBackedImmutableList(E[] i) {
        myElementsLength = i.length;
        myElements = Arrays.copyOf(i, myElementsLength);
        myHashCode = calculateHashCode();
    }

    /**
     * <p>
     * This creates a new array-backed immutable list using the specified array of elements and length.
     * </p>
     *
     * @param i
     *            An array of elements.
     * @param length
     *            Length to copy.
     */
    public ArrayBackedImmutableList(E[] i, int length) {
        myElementsLength = length;
        myElements = Arrays.copyOf(i, length);
        myHashCode = calculateHashCode();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Equals method to compare two immutable lists.
     * </p>
     *
     * @param o
     *            Object to be compared.
     *
     * @return {@code true} if all the elements are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        boolean result = (o instanceof ArrayBackedImmutableList);

        if (result) {
            ArrayBackedImmutableList oAsABIL = (ArrayBackedImmutableList) o;

            result = (myElementsLength == oAsABIL.size());

            if (result) {
                int i = 0;
                while (i < myElementsLength && result) {
                    result = (myElements[i].equals(oAsABIL.get(i)));
                    i++;
                }
            }
        }

        return result;
    }

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
        return myElements[index];
    }

    /**
     * <p>
     * This method overrides the default {@code hashCode} method implementation.
     * </p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        return myHashCode;
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
        return new ImmutableListSubview<>(this, 0, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Iterator<E> iterator() {
        return new ArrayIterator<>(myElements);
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
        return myElementsLength;
    }

    /**
     * <p>
     * This method returns an iterator for iterating over a sub-list.
     * </p>
     *
     * @param start
     *            An index position to start building our sub-list.
     * @param length
     *            Length of the sub-list.
     *
     * @return An {@link Iterator}.
     */
    public Iterator<E> subsequenceIterator(int start, int length) {
        return new ArrayIterator<>(myElements, start, length);
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
        return new ImmutableListSubview<>(this, startIndex, myElementsLength - startIndex);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method for computing the hash code value for this immutable list.
     * </p>
     *
     * @return Hash code value.
     */
    private int calculateHashCode() {
        int result = 0;
        for (E e : myElements) {
            result += e.hashCode() * 74;
        }

        return result;
    }

}
