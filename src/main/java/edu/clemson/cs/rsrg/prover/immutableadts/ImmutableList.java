/*
 * ImmutableList.java
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

import java.util.Iterator;

/**
 * <p>An interface for immutable lists.</p>
 *
 * @param <E> Type of elements stored inside this list.
 *
 * @author Hampton Smith
 * @version 2.0
 */
public interface ImmutableList<E> extends Iterable<E> {

    /**
     * <p>This method returns a new immutable list after appending
     * an element.</p>
     *
     * @param e An element.
     *
     * @return An immutable list.
     */
    ImmutableList<E> appended(E e);

    /**
     * <p>This method returns a new immutable list after appending
     * all elements from the specified immutable list.</p>
     *
     * @param l Immutable list to be appended to the end.
     *
     * @return An immutable list.
     */
    ImmutableList<E> appended(ImmutableList<E> l);

    /**
     * <p>This method returns a new immutable list after appending
     * all elements from the specified iterator.</p>
     *
     * @param i An iterable collection.
     *
     * @return An immutable list.
     */
    ImmutableList<E> appended(Iterable<E> i);

    /**
     * <p>This method returns the first element in the list.</p>
     *
     * @return First element.
     */
    E first();

    /**
     * <p>This method returns the element at the specified index.</p>
     *
     * @param index An index position.
     *
     * @return The element at the specified index.
     */
    E get(int index);

    /**
     * <p>This method returns a new immutable sub-list from the head
     * to the specified index.</p>
     *
     * @param length Length of the sub-list.
     *
     * @return An immutable sub-list of the original list.
     */
    ImmutableList<E> head(int length);

    /**
     * <p>This method returns a new immutable list after inserting the
     * element {@code e} at the specified index.</p>
     *
     * @param index An index position where we want to insert
     *              the element {@code e}.
     * @param e A new element.
     *
     * @return An immutable list.
     */
    ImmutableList<E> insert(int index, E e);

    /**
     * <p>This method returns a new immutable list after inserting the
     * list {@code e} at the specified index.</p>
     *
     * @param index An index position where we want to insert
     *              the list {@code e}.
     * @param l An immutable list that needs to be inserted
     *          into our current immutable list.
     *
     * @return An immutable list.
     */
    ImmutableList<E> insert(int index, ImmutableList<E> l);

    /**
     * {@inheritDoc}
     */
    @Override
    Iterator<E> iterator();

    /**
     * <p>This method returns a new immutable list after removing the
     * element at the specified index.</p>
     *
     * @param index An index position where we want to remove.
     *
     * @return An immutable list.
     */
    ImmutableList<E> removed(int index);

    /**
     * <p>This method returns a new immutable list after replacing the
     * element {@code e} at the specified index.</p>
     *
     * @param index An index position where we want to replace
     *              the element {@code e}.
     * @param e A replacing element.
     *
     * @return An immutable list.
     */
    ImmutableList<E> set(int index, E e);

    /**
     * <p>This method returns the number of elements in
     * this list.</p>
     *
     * @return Number of elements.
     */
    int size();

    /**
     * <p>This method returns a new immutable sub-list from
     * the specified start index to the specified length (end index).</p>
     *
     * @param startIndex An index position to start building
     *                   our sub-list.
     * @param length Length of the sub-list.
     *
     * @return An immutable sub-list of the original list.
     */
    ImmutableList<E> subList(int startIndex, int length);

    /**
     * <p>This method returns a new immutable sub-list from the
     * specified start index to the end of our list.</p>
     *
     * @param startIndex An index position to start building
     *                   our sub-list.
     *
     * @return An immutable sub-list of the original list.
     */
    ImmutableList<E> tail(int startIndex);

}