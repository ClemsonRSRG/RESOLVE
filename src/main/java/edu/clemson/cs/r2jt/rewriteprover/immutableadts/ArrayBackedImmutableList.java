/*
 * ArrayBackedImmutableList.java
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
package edu.clemson.cs.r2jt.rewriteprover.immutableadts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import edu.clemson.cs.r2jt.rewriteprover.iterators.ArrayIterator;

public class ArrayBackedImmutableList<E> extends AbstractImmutableList<E> {

    private final E[] myElements;
    private final int myElementsLength;

    private final int myHashCode;

    @SuppressWarnings("unchecked")
    public ArrayBackedImmutableList(Iterable<E> i) {
        List<E> tempList = new ArrayList<E>();

        for (E e : i) {
            tempList.add(e);
        }

        myElements = (E[]) tempList.toArray();
        myElementsLength = myElements.length;
        myHashCode = calculateHashCode();
    }

    public ArrayBackedImmutableList(E[] i) {
        myElementsLength = i.length;
        myElements = Arrays.copyOf(i, myElementsLength);
        myHashCode = calculateHashCode();
    }

    public ArrayBackedImmutableList(E[] i, int length) {
        myElementsLength = length;
        myElements = Arrays.copyOf(i, length);
        myHashCode = calculateHashCode();
    }

    private int calculateHashCode() {
        int result = 0;
        for (E e : myElements) {
            result += e.hashCode() * 74;
        }
        return result;
    }

    @Override
    public E get(int index) {
        return myElements[index];
    }

    @Override
    public ImmutableList<E> head(int length) {
        return new ImmutableListSubview<E>(this, 0, length);
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator<E>(myElements);
    }

    public Iterator<E> subsequenceIterator(int start, int length) {
        return new ArrayIterator<E>(myElements, start, length);
    }

    @Override
    public int size() {
        return myElementsLength;
    }

    @Override
    public ImmutableList<E> tail(int startIndex) {
        return new ImmutableListSubview<E>(this, startIndex, myElementsLength
                - startIndex);
    }

    @Override
    public int hashCode() {
        return myHashCode;
    }

    @Override
    public boolean equals(Object o) {
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
}
