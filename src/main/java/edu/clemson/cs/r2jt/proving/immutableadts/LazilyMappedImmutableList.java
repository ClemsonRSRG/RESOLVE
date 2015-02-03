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

import edu.clemson.cs.r2jt.utilities.ImmutableIterator;
import edu.clemson.cs.r2jt.utilities.Mapping;

public class LazilyMappedImmutableList<T, R> extends AbstractImmutableList<R> {

    private final ImmutableList<T> myOriginalList;
    private final R[] myMappedCache;

    private final Mapping<T, R> myMapping;

    /**
     * <p>Constructs a new immutable list based on <code>original</code>, in 
     * which each entry in this new list will be the sister entry in that 
     * original list, filtered through <code>m</code>.  <code>m</code> must 
     * represent a functional mapping--that is, if <code>x.equals(y)</code>, 
     * then <code>m.map(x).equals(m.map(y))</codE> in all cases, otherwise the 
     * resulting list may appear to "mutate" to the client, despite the original
     * underlying list remaining unchanged.</p>
     * 
     * @param original The original list.
     * @param m The mapping to apply to each entry.
     */
    @SuppressWarnings("unchecked")
    public LazilyMappedImmutableList(ImmutableList<T> original, Mapping<T, R> m) {

        myOriginalList = original;
        myMapping = m;
        myMappedCache = (R[]) new Object[myOriginalList.size()];
    }

    @Override
    public ImmutableList<R> tail(int startIndex) {
        return new LazilyMappedImmutableList<T, R>(myOriginalList
                .tail(startIndex), myMapping);
    }

    @Override
    public ImmutableList<R> head(int length) {
        return new LazilyMappedImmutableList<T, R>(myOriginalList.head(length),
                myMapping);
    }

    @Override
    public R get(int index) {
        R result = myMappedCache[index];

        if (result == null) {
            result = myMapping.map(myOriginalList.get(index));
            myMappedCache[index] = result;
        }

        return result;
    }

    @Override
    public Iterator<R> iterator() {
        return new ImmutableIterator<R>(new CacheCheckingIterator());
    }

    @Override
    public int size() {
        return myOriginalList.size();
    }

    private class CacheCheckingIterator implements Iterator<R> {

        private Iterator<T> myOriginalIterator = myOriginalList.iterator();
        private int myIndex = 0;

        @Override
        public boolean hasNext() {
            return myOriginalIterator.hasNext();
        }

        @Override
        public R next() {
            R result = myMappedCache[myIndex];

            T nextOriginalElement = myOriginalIterator.next();
            if (result == null) {
                result = myMapping.map(nextOriginalElement);
                myMappedCache[myIndex] = result;
            }

            myIndex++;

            return result;
        }

        @Override
        public void remove() {
            myOriginalIterator.remove();
        }
    }
}
