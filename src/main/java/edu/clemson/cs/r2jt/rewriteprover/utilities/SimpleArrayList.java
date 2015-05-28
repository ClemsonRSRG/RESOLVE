/**
 * SimpleArrayList.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.utilities;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Not a true {@link java.util.List List}.  Simply provides the minimum of
 * functionality required by 
 * {@link edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel PerVCProverModel}.
 * We don't use an existing list because we want to explicitly allow the 
 * collection to be modified out from under iterators derived from it--with
 * modifiers fulfilling the contract that by time Iterator.next() is called,
 * the collection will be restored to the exact state it was in when the 
 * iterator was last used.</p>
 */
public class SimpleArrayList<E> implements Iterable<E> {

    private E[] myData = (E[]) new Object[10];
    private int myFirstEmptyIndex = 0;

    public void add(E e) {
        resizeAsNecessary();

        myData[myFirstEmptyIndex] = e;
        myFirstEmptyIndex++;
    }

    public void add(int index, E e) {
        if (index > myFirstEmptyIndex) {
            throw new IndexOutOfBoundsException("" + index);
        }

        resizeAsNecessary();

        System.arraycopy(myData, index, myData, index + 1, myFirstEmptyIndex
                - index);

        myData[index] = e;
        myFirstEmptyIndex++;
    }

    public boolean contains(E e) {
        boolean result = false;
        int i = 0;

        while (i < myFirstEmptyIndex && !result) {
            result = myData[i].equals(e);
            i++;
        }

        return result;
    }

    public boolean isEmpty() {
        return myFirstEmptyIndex == 0;
    }

    public int size() {
        return myFirstEmptyIndex;
    }

    public void set(int index, E e) {
        if (index >= myFirstEmptyIndex) {
            throw new IndexOutOfBoundsException("" + index);
        }

        myData[index] = e;
    }

    public boolean remove(Object o) {
        int foundIndex = -1;
        int index = 0;
        while (foundIndex == -1 && index < myFirstEmptyIndex) {
            if (myData[index] == o) {
                foundIndex = index;
            }

            index++;
        }

        if (foundIndex != -1) {
            remove(foundIndex);
        }

        return (foundIndex != -1);
    }

    public E remove(int index) {
        if (index > myFirstEmptyIndex) {
            throw new IndexOutOfBoundsException("" + index);
        }

        E result = myData[index];

        System.arraycopy(myData, index + 1, myData, index, myFirstEmptyIndex
                - index - 1);

        myFirstEmptyIndex--;

        return result;
    }

    private void resizeAsNecessary() {
        if (myFirstEmptyIndex == myData.length) {
            E[] bigger = (E[]) new Object[myData.length * 2];
            System.arraycopy(myData, 0, bigger, 0, myData.length);
            myData = bigger;
        }
    }

    public E get(int index) {
        if (index < 0 || index >= myFirstEmptyIndex) {
            throw new IndexOutOfBoundsException("" + index);
        }

        return myData[index];
    }

    @Override
    public Iterator<E> iterator() {
        return new SimpleArrayListIterator();
    }

    @Override
    public String toString() {
        return Arrays.toString(Arrays.copyOf(myData, myFirstEmptyIndex));
    }

    private class SimpleArrayListIterator implements Iterator<E> {

        private int myCurIndex;

        @Override
        public boolean hasNext() {
            return myCurIndex < myFirstEmptyIndex;
        }

        @Override
        public E next() {
            if (myCurIndex >= myFirstEmptyIndex) {
                throw new NoSuchElementException();
            }

            E result = myData[myCurIndex];

            myCurIndex++;

            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
