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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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
package edu.clemson.cs.r2jt.proving.immutableadts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import edu.clemson.cs.r2jt.proving.ArrayIterator;

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
