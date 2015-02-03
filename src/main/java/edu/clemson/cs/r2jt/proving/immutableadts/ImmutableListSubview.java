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
package edu.clemson.cs.r2jt.proving.immutableadts;

import java.util.Iterator;

public class ImmutableListSubview<E> extends AbstractImmutableList<E> {

    private final ArrayBackedImmutableList<E> myBaseList;
    private final int mySubviewStart;
    private final int mySubviewLength;
    private final int myFirstAfterIndex;

    public ImmutableListSubview(ArrayBackedImmutableList<E> baseList,
            int start, int length) {

        //TODO : These defensive checks can be taken out for efficiency once
        //       we're satisfied that ImmutableLists works correctly.
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

    @Override
    public E get(int index) {
        if (index < 0 || index >= myFirstAfterIndex) {
            throw new IndexOutOfBoundsException();
        }

        return myBaseList.get(index + mySubviewStart);
    }

    @Override
    public ImmutableList<E> head(int length) {
        if (length > mySubviewLength) {
            throw new IndexOutOfBoundsException();
        }

        return new ImmutableListSubview<E>(myBaseList, mySubviewStart, length);
    }

    @Override
    public Iterator<E> iterator() {
        return myBaseList.subsequenceIterator(mySubviewStart, mySubviewLength);
    }

    @Override
    public int size() {
        return mySubviewLength;
    }

    @Override
    public ImmutableList<E> tail(int startIndex) {
        if (startIndex < 0 || startIndex > mySubviewLength) {
            throw new IndexOutOfBoundsException();
        }

        return new ImmutableListSubview<E>(myBaseList, startIndex
                + mySubviewStart, mySubviewLength - startIndex);
    }

}
