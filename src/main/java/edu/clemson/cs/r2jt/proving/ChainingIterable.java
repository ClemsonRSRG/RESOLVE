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
package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChainingIterable<T> implements Iterable<T> {

    private final List<Iterable<T>> mySubIterables =
            new LinkedList<Iterable<T>>();

    /**
     * <p>Adds a new iterable set of <code>T</code>s to the list of 
     * <code>T</code>s iterated over.</p>
     * 
     * @param i A non-null <code>Iterable</code>.
     */
    public void add(Iterable<T> i) {
        mySubIterables.add(i);
    }

    public void add(T t) {
        List<T> wrapper = new LinkedList<T>();
        wrapper.add(t);

        mySubIterables.add(wrapper);
    }

    @Override
    public Iterator<T> iterator() {
        return new ListOfListsIterator(mySubIterables.iterator());
    }

    private class ListOfListsIterator implements Iterator<T> {

        private final Iterator<Iterable<T>> myLists;
        private Iterator<T> myCurList;
        private T myCurElement;

        public ListOfListsIterator(Iterator<Iterable<T>> l) {
            myLists = l;

            if (myLists.hasNext()) {
                myCurList = myLists.next().iterator();
                myCurElement = nextElement();
            }
            else {
                myCurElement = null;
            }
        }

        @Override
        public boolean hasNext() {
            return myCurElement != null;
        }

        @Override
        public T next() {
            T retval = myCurElement;

            myCurElement = nextElement();

            return retval;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private T nextElement() {
            T retval;

            boolean curListHasNext = myCurList.hasNext();
            while (!curListHasNext && myLists.hasNext()) {
                myCurList = myLists.next().iterator();
                curListHasNext = myCurList.hasNext();
            }

            if (curListHasNext) {
                retval = myCurList.next();
            }
            else {
                retval = null;
            }

            return retval;
        }
    }

}
