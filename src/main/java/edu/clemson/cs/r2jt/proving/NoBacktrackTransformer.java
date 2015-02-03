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
import java.util.NoSuchElementException;

/**
 * <p>A <code>NoBacktrackTransformer</code> wraps an existing transformer to
 * return either zero or one transformed VCs by truncating after the first
 * suggestion if such a suggestion exists.</p>
 */
public class NoBacktrackTransformer implements VCTransformer {

    private VCTransformer myBaseTransformer;
    private ZeroOrOneIterator<VC> myLastTransformationIterator;

    public NoBacktrackTransformer(VCTransformer base) {
        myBaseTransformer = base;
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> the
     * <code>Iterator</code> returned by the last call to
     * {@link #transform(VC) transform()} has returned an element via
     * <code>next()</code> at the time of the call to this method.</p>
     *
     * @return <code>true</code> <strong>iff</strong> said <code>Iterator</code>
     *         has returned an element at the time of this call.
     */
    public boolean hasReturned() {
        return myLastTransformationIterator.hasReturned();
    }

    @Override
    public Iterator<VC> transform(VC original) {
        myLastTransformationIterator =
                new ZeroOrOneIterator<VC>(new NonEquivalentIterator(
                        myBaseTransformer.transform(original), original));

        return myLastTransformationIterator;
    }

    public String toString() {
        return myBaseTransformer.toString();
    }

    @Override
    public Antecedent getPattern() {
        return myBaseTransformer.getPattern();
    }

    @Override
    public Consequent getReplacementTemplate() {
        return myBaseTransformer.getReplacementTemplate();
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        return myBaseTransformer.introducesQuantifiedVariables();
    }

    /**
     * <p>This class wraps an <code>Iterator</code> over <code>VC</code>s and
     * returns only those not equivalent (via
     * {@link VC#equivalent(VC) equivalent()}, after simplification via
     * {@link VC#simplify()}) to some provided <code>VC</code>.</p>
     */
    private class NonEquivalentIterator implements Iterator<VC> {

        private final Iterator<VC> mySource;
        private final VC myOriginal;
        private VC myNext;

        public NonEquivalentIterator(Iterator<VC> i, VC original) {
            mySource = i;
            myOriginal = original;
        }

        @Override
        public boolean hasNext() {

            boolean retval = false;

            while (!retval && mySource.hasNext()) {
                myNext = mySource.next();

                retval = !myNext.equivalent(myOriginal);
            }

            if (!retval) {
                myNext = null;
            }

            return retval;
        }

        @Override
        public VC next() {
            if (myNext == null) {
                throw new NoSuchElementException();
            }

            return myNext;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class ZeroOrOneIterator<T> implements Iterator<T> {

        private final Iterator<T> myBaseIterator;
        private boolean myReturnedFlag = false;

        public ZeroOrOneIterator(Iterator<T> base) {
            myBaseIterator = base;
        }

        public boolean hasReturned() {
            return myReturnedFlag;
        }

        @Override
        public boolean hasNext() {
            return !myReturnedFlag && myBaseIterator.hasNext();
        }

        @Override
        public T next() {
            if (myReturnedFlag) {
                throw new NoSuchElementException();
            }

            //Note that if there is no next element, next() will throw a
            //NoSuchElementException and myReturnedFlag will remain false
            T retval = myBaseIterator.next();
            myReturnedFlag = true;

            return retval;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
