/*
 * ArrayIterator.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.prover.iterators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>This class implements the iterator class for an array.</p>
 *
 * @param <E> Elements to to be iterated.
 *
 * @author Hampton Smith
 * @version 2.0
 */
public class ArrayIterator<E> implements Iterator<E> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The array to be iterated.</p> */
    private final E[] myArray;

    /** <p>The first (or next) index that hasn't been iterated.</p> */
    private final int myFirstUnincludedIndex;

    /** <p>The current iterated cursor position.</p> */
    private int myCursor = 0;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Creates an iterator for an array.</p>
     *
     * @param array The array to be iterated.
     */
    public ArrayIterator(E[] array) {
        this(array, 0, array.length);
    }

    /**
     * <p>Creates an iterator for an array with a specified
     * start and end index.</p>
     *
     * @param array The array to be iterated.
     * @param start The start position in the array.
     * @param length The end position in the array.
     */
    public ArrayIterator(E[] array, int start, int length) {
        myArray = array;

        myCursor = start;
        myFirstUnincludedIndex = myCursor + length;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ArrayIterator<?> that = (ArrayIterator<?>) o;

        if (myFirstUnincludedIndex != that.myFirstUnincludedIndex)
            return false;
        if (myCursor != that.myCursor)
            return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(myArray, that.myArray);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = Arrays.hashCode(myArray);
        result = 31 * result + myFirstUnincludedIndex;
        result = 31 * result + myCursor;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasNext() {
        return myCursor < myFirstUnincludedIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final E next() {
        E retval;

        try {
            retval = myArray[myCursor];
        }
        catch (IndexOutOfBoundsException ex) {
            throw new NoSuchElementException();
        }

        myCursor++;

        return retval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void remove() {
        throw new UnsupportedOperationException();
    }

}