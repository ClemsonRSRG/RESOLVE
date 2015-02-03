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
package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>A <code>ConcatenatingTransformationChooser</code> conbines two existing
 * <code>TransormationChooser</code>s by simply suggesting transformations from
 * each in sequent.  That is, all the suggestions from the first, then all the
 * suggestions from the second.</p>
 */
public class ConcatenatingTransformationChooser
        implements
            TransformationChooser {

    private final TransformationChooser myFirst, mySecond;

    public ConcatenatingTransformationChooser(TransformationChooser first,
            TransformationChooser second) {

        myFirst = first;
        mySecond = second;
    }

    @Override
    public String toString() {
        return "Concatenate(" + myFirst + " with " + mySecond + ")";
    }

    @Override
    public void preoptimizeForVC(VC vc) {
        myFirst.preoptimizeForVC(vc);
        mySecond.preoptimizeForVC(vc);
    }

    @Override
    public Iterator<ProofPathSuggestion> suggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d) {

        return new ChainingIterator<ProofPathSuggestion>(myFirst
                .suggestTransformations(vc, curLength, metrics, d), mySecond
                .suggestTransformations(vc, curLength, metrics, d));
    }
}
