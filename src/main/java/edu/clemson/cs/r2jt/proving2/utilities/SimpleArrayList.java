/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.proving2.utilities;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Not a true {@link java.util.List List}.  Simply provides the minimum of
 * functionality required by 
 * {@link edu.clemson.cs.r2jt.proving2.model.PerVCProverModel PerVCProverModel}.
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
