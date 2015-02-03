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

import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 * <p>A <code>PredicateIterator</code> wraps an existing <code>Iterator</code>
 * and iterates over those elements from the original that satisfy a given
 * predicate (in the form of a <code>Mapping</code> from <code>T</code> to
 * <code>Boolean</code>).
 *
 * @param <T> The type of the elements returned by the Iterator;
 */
public class PredicateIterator<T> implements Iterator<T> {

    private final Iterator<T> myBaseIterator;
    private T myNext;
    private Mapping<T, Boolean> myPredicate;

    public PredicateIterator(Iterator<T> base, Mapping<T, Boolean> p) {
        myBaseIterator = base;
        myPredicate = p;
    }

    @Override
    public boolean hasNext() {
        boolean retval = false;

        if (myNext == null) {
            while (myBaseIterator.hasNext() && !retval) {
                myNext = myBaseIterator.next();
                retval = myPredicate.map(myNext);
            }
        }

        return (myNext != null);
    }

    @Override
    public T next() {
        T retval = myNext;

        myNext = null;

        return retval;
    }

    @Override
    public void remove() {
        myBaseIterator.remove();
    }

}
