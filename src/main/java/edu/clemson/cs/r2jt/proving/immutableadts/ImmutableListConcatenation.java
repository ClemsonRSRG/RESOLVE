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

import java.util.Iterator;

import edu.clemson.cs.r2jt.proving.ChainingIterator;

public class ImmutableListConcatenation<E> extends AbstractImmutableList<E> {

    private final ImmutableList<E> myFirstList;
    private final int myFirstListSize;

    private final ImmutableList<E> mySecondList;
    private final int mySecondListSize;

    private final int myTotalSize;

    public ImmutableListConcatenation(ImmutableList<E> firstList,
            ImmutableList<E> secondList) {

        myFirstList = firstList;
        myFirstListSize = myFirstList.size();

        mySecondList = secondList;
        mySecondListSize = mySecondList.size();

        myTotalSize = myFirstListSize + mySecondListSize;
    }

    @Override
    public E get(int index) {
        E retval;

        if (index < myFirstListSize) {
            retval = myFirstList.get(index);
        }
        else {
            retval = mySecondList.get(index - myFirstListSize);
        }

        return retval;
    }

    @Override
    public ImmutableList<E> head(int length) {
        ImmutableList<E> retval;

        if (length <= myFirstListSize) {
            retval = myFirstList.head(length);
        }
        else {
            retval =
                    new ImmutableListConcatenation<E>(myFirstList, mySecondList
                            .head(length - myFirstListSize));
        }

        return retval;
    }

    @Override
    public Iterator<E> iterator() {
        return new ChainingIterator<E>(myFirstList.iterator(), mySecondList
                .iterator());
    }

    @Override
    public int size() {
        return myTotalSize;
    }

    @Override
    public ImmutableList<E> subList(int startIndex, int length) {
        return tail(startIndex).head(length);
    }

    @Override
    public ImmutableList<E> tail(int startIndex) {
        ImmutableList<E> retval;

        if (startIndex < myFirstListSize) {
            retval =
                    new ImmutableListConcatenation<E>(myFirstList
                            .tail(startIndex), mySecondList);
        }
        else {
            retval = mySecondList.tail(startIndex - myFirstListSize);
        }

        return retval;
    }
}
