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

import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * <p>The <code>ProofData</code> class is an immutable class that represents the
 * state of a proof in progress.  It differs from <code>Metrics</code> in that,
 * unlike <code>Metrics</code> which contains data about the entire proof
 * search, <code>ProofData</code> contains only information about the currently
 * considered proof--i.e., data about the steps taken.</p>
 * 
 * <p><code>ProofData</code> is designed to be dynamically extensible so that
 * individual <code>TransformationChooser</code>s may record proof-state 
 * information.</p>
 */
public class ProofData {

    private Deque<VC> myPastStates = new LinkedList<VC>();

    private Map<TransformerData, Object> myTransformerData =
            new HashMap<TransformerData, Object>();

    public ProofData addStep(VC step) {
        ProofData retval = copy();
        retval.myPastStates.push(step);

        return retval;
    }

    public ProofData popStep() {
        ProofData retval = copy();
        retval.myPastStates.pop();

        return retval;
    }

    public Iterator<VC> stepIterator() {
        return new ImmutableIteratorWrapper<VC>(myPastStates.iterator());
    }

    public ProofData putAttribute(TransformationChooser c, Object key,
            Object value) {

        ProofData retval = copy();

        TransformerData t = new TransformerData(c, key);

        retval.myTransformerData.put(t, value);

        return retval;
    }

    public boolean attributeDefined(TransformationChooser c, Object key) {

        TransformerData t = new TransformerData(c, key);

        return myTransformerData.containsKey(t);
    }

    public Object getAttribute(TransformationChooser c, Object key) {
        return myTransformerData.get(new TransformerData(c, key));
    }

    public ProofData copy() {
        ProofData retval = new ProofData();
        retval.myPastStates = new LinkedList<VC>(myPastStates);
        retval.myTransformerData =
                new HashMap<TransformerData, Object>(myTransformerData);

        return retval;
    }

    private static class TransformerData {

        public TransformationChooser chooser;
        public Object key;

        public TransformerData(TransformationChooser c, Object k) {
            chooser = c;
            key = k;
        }

        public int hashCode() {
            return chooser.hashCode() + key.hashCode();
        }

        public boolean equals(Object o) {
            boolean retval = (o instanceof TransformerData);

            if (retval) {
                TransformerData oAsTransformerData = (TransformerData) o;

                retval =
                        oAsTransformerData.chooser.equals(chooser)
                                && oAsTransformerData.key.equals(key);
            }

            return retval;
        }
    }
}
