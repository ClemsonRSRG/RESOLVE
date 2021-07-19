/*
 * AbstractImmutableList.java
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
package edu.clemson.cs.rsrg.prover.immutableadts;

/**
 * <p>
 * This is the abstract base class for all immutable list implementations.
 * </p>
 *
 * @param <E> Type of elements stored inside this list.
 *
 * @author Hampton Smith
 * @version 2.0
 */
public abstract class AbstractImmutableList<E> implements ImmutableList<E> {

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns a new immutable list after appending an element.
     * </p>
     *
     * @param e An element.
     *
     * @return An immutable list.
     */
    @Override
    public final ImmutableList<E> appended(E e) {
        return appended(new SingletonImmutableList<>(e));
    }

    /**
     * <p>
     * This method returns a new immutable list after appending all elements
     * from the specified
     * immutable list.
     * </p>
     *
     * @param l Immutable list to be appended to the end.
     *
     * @return An immutable list.
     */
    @Override
    public final ImmutableList<E> appended(ImmutableList<E> l) {
        return new ImmutableListConcatenation<>(this, l);
    }

    /**
     * <p>
     * This method returns a new immutable list after appending all elements
     * from the specified
     * iterator.
     * </p>
     *
     * @param i An iterable collection.
     *
     * @return An immutable list.
     */
    @Override
    public final ImmutableList<E> appended(Iterable<E> i) {
        return appended(new ArrayBackedImmutableList<>(i));
    }

    /**
     * <p>
     * This method returns the first element in the list.
     * </p>
     *
     * @return First element.
     */
    @Override
    public final E first() {
        return get(0);
    }

    /**
     * <p>
     * This method returns a new immutable list after inserting the element
     * {@code e} at the specified
     * index.
     * </p>
     *
     * @param index An index position where we want to insert the element
     *        {@code e}.
     * @param e A new element.
     *
     * @return An immutable list.
     */
    @Override
    public final ImmutableList<E> insert(int index, E e) {
        return insert(index, new SingletonImmutableList<>(e));
    }

    /**
     * <p>
     * This method returns a new immutable list after inserting the list
     * {@code e} at the specified
     * index.
     * </p>
     *
     * @param index An index position where we want to insert the list
     *        {@code e}.
     * @param l An immutable list that needs to be inserted into our current
     *        immutable list.
     *
     * @return An immutable list.
     */
    @Override
    public final ImmutableList<E> insert(int index, ImmutableList<E> l) {
        ImmutableList<E> first, second;

        if (index == 0) {
            first = l;
            second = this;
        }
        else if (index == size()) {
            first = this;
            second = l;
        }
        else {
            first = new ImmutableListConcatenation<>(head(index), l);
            second = tail(index);
        }

        return new ImmutableListConcatenation<>(first, second);
    }

    /**
     * <p>
     * This method returns a new immutable list after removing the element at
     * the specified index.
     * </p>
     *
     * @param index An index position where we want to remove.
     *
     * @return An immutable list.
     */
    @Override
    public final ImmutableList<E> removed(int index) {
        ImmutableList<E> retval;

        if (index == 0) {
            retval = tail(1);
        }
        else if (index == size() - 1) {
            retval = head(index);
        }
        else {
            retval = new ImmutableListConcatenation<>(head(index),
                    tail(index + 1));
        }

        return retval;
    }

    /**
     * <p>
     * This method returns a new immutable list after replacing the element
     * {@code e} at the specified
     * index.
     * </p>
     *
     * @param index An index position where we want to replace the element
     *        {@code e}.
     * @param e A replacing element.
     *
     * @return An immutable list.
     */
    @Override
    public final ImmutableList<E> set(int index, E e) {
        ImmutableList<E> first, second;
        ImmutableList<E> insertedList = new SingletonImmutableList<>(e);

        if (index == 0) {
            first = insertedList;
            second = tail(1);
        }
        else if (index == size() - 1) {
            first = head(index);
            second = insertedList;
        }
        else {
            first = new ImmutableListConcatenation<>(head(index), insertedList);
            second = tail(index + 1);
        }

        return new ImmutableListConcatenation<>(first, second);
    }

    /**
     * <p>
     * This method returns a new immutable sub-list from the specified start
     * index to the specified
     * length (end index).
     * </p>
     *
     * @param startIndex An index position to start building our sub-list.
     * @param length Length of the sub-list.
     *
     * @return An immutable sub-list of the original list.
     */
    @Override
    public ImmutableList<E> subList(int startIndex, int length) {
        return tail(startIndex).head(length);
    }

    /**
     * <p>
     * This method returns the object in string format.
     * </p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        StringBuilder buffer = new StringBuilder("[");

        int sizeSanityCheck = 0;
        boolean first = true;
        for (E e : this) {
            if (!first) {
                buffer.append(", ");
            }

            buffer.append(e);

            first = false;
            sizeSanityCheck++;
        }
        buffer.append("]");

        if (sizeSanityCheck != size()) {
            throw new RuntimeException();
        }

        return buffer.toString();
    }

}
