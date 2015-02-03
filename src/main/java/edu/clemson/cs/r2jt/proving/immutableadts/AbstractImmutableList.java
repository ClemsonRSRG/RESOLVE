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

public abstract class AbstractImmutableList<E> implements ImmutableList<E> {

    @Override
    public ImmutableList<E> appended(E e) {
        return appended(new SingletonImmutableList<E>(e));
    }

    @Override
    public ImmutableList<E> appended(ImmutableList<E> l) {
        return new ImmutableListConcatenation<E>(this, l);
    }

    @Override
    public ImmutableList<E> appended(Iterable<E> i) {
        return appended(new ArrayBackedImmutableList<E>(i));
    }

    @Override
    public E first() {
        return get(0);
    }

    @Override
    public ImmutableList<E> removed(int index) {
        ImmutableList<E> retval;

        if (index == 0) {
            retval = tail(1);
        }
        else if (index == size() - 1) {
            retval = head(index);
        }
        else {
            retval =
                    new ImmutableListConcatenation<E>(head(index),
                            tail(index + 1));
        }

        return retval;
    }

    @Override
    public ImmutableList<E> set(int index, E e) {
        ImmutableList<E> first, second;

        ImmutableList<E> insertedList = new SingletonImmutableList<E>(e);

        if (index == 0) {
            first = insertedList;
            second = tail(1);
        }
        else if (index == size() - 1) {
            first = head(index);
            second = insertedList;
        }
        else {
            first =
                    new ImmutableListConcatenation<E>(head(index), insertedList);
            second = tail(index + 1);
        }

        return new ImmutableListConcatenation<E>(first, second);
    }

    @Override
    public ImmutableList<E> insert(int index, E e) {
        return insert(index, new SingletonImmutableList<E>(e));
    }

    @Override
    public ImmutableList<E> insert(int index, ImmutableList<E> l) {
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
            first = new ImmutableListConcatenation<E>(head(index), l);
            second = tail(index);
        }

        return new ImmutableListConcatenation<E>(first, second);
    }

    @Override
    public ImmutableList<E> subList(int startIndex, int length) {
        return tail(startIndex).head(length);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("[");

        int sizeSanityCheck = 0;

        boolean first = true;
        Iterator<E> iterator = iterator();
        while (iterator.hasNext()) {
            if (!first) {
                buffer.append(", ");
            }

            buffer.append(iterator.next());

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
