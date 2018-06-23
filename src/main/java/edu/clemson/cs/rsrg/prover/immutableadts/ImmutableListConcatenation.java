/*
 * ImmutableListConcatenation.java
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

import edu.clemson.cs.rsrg.prover.iterators.ChainingIterator;

import java.util.Iterator;

/**
 * <p>This class implements an immutable list after concatenating
 * two immutable lists.</p>
 *
 * @param <E> Type of elements stored inside this list.
 *
 * @author Hampton Smith
 * @version 2.0
 */
public class ImmutableListConcatenation<E> extends AbstractImmutableList<E> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The first immutable list in our concatenated list.</p> */
    private final ImmutableList<E> myFirstList;

    /** <p>The number of elements in the first immutable list.</p> */
    private final int myFirstListSize;

    /** <p>The second immutable list in our concatenated list.</p> */
    private final ImmutableList<E> mySecondList;

    /** <p>Total number of elements in this immutable list.</p> */
    private final int myTotalSize;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new immutable list after concatenating
     * two immutable lists.</p>
     *
     * @param firstList An immutable list.
     * @param secondList Another immutable list.
     */
    public ImmutableListConcatenation(ImmutableList<E> firstList,
            ImmutableList<E> secondList) {
        myFirstList = firstList;
        myFirstListSize = myFirstList.size();
        mySecondList = secondList;
        myTotalSize = myFirstListSize + mySecondList.size();
    }

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
        E retval;

        if (index < myFirstListSize) {
            retval = myFirstList.get(index);
        }
        else {
            retval = mySecondList.get(index - myFirstListSize);
        }

        return retval;
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
    public ImmutableList<E> head(int length) {
        ImmutableList<E> retval;

        if (length <= myFirstListSize) {
            retval = myFirstList.head(length);
        }
        else {
            retval =
                    new ImmutableListConcatenation<>(myFirstList, mySecondList
                            .head(length - myFirstListSize));
        }

        return retval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Iterator<E> iterator() {
        return new ChainingIterator<>(myFirstList.iterator(), mySecondList.iterator());
    }

    /**
     * <p>This method returns the number of elements in
     * this list.</p>
     *
     * @return Number of elements.
     */
    @Override
    public int size() {
        return myTotalSize;
    }

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
    @Override
    public final ImmutableList<E> subList(int startIndex, int length) {
        return tail(startIndex).head(length);
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
        ImmutableList<E> retval;

        if (startIndex < myFirstListSize) {
            retval =
                    new ImmutableListConcatenation<>(myFirstList
                            .tail(startIndex), mySecondList);
        }
        else {
            retval = mySecondList.tail(startIndex - myFirstListSize);
        }

        return retval;
    }
}