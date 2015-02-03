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

import java.math.BigInteger;
import java.util.Iterator;

/**
 * <p>A <code>CycleDetectingTransformationChooser</code> composes with an 
 * existing chooser to detect and backtrack on cycles, otherwise simply 
 * deferring to the existing chooser.</p>
 */
public class CycleDetectingTransformationChooser
        implements
            TransformationChooser {

    private static final Iterator<ProofPathSuggestion> TYPE_SAFE_ITERATOR =
            null;

    private final TransformationChooser mySourceChooser;

    public CycleDetectingTransformationChooser(TransformationChooser source) {
        mySourceChooser = source;
    }

    @Override
    public void preoptimizeForVC(VC vc) {
        mySourceChooser.preoptimizeForVC(vc);
    }

    @Override
    public Iterator<ProofPathSuggestion> suggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData proofData) {

        Iterator<ProofPathSuggestion> retval;

        boolean cycle = false;
        Iterator<VC> pastStatesIterator = proofData.stepIterator();
        while (!cycle && pastStatesIterator.hasNext()) {
            cycle = vc.equivalent(pastStatesIterator.next());
        }

        if (cycle) {
            retval = DummyIterator.getInstance(TYPE_SAFE_ITERATOR);
            metrics.numTimesBacktracked =
                    metrics.numTimesBacktracked.add(BigInteger.ONE);
        }
        else {
            retval =
                    mySourceChooser.suggestTransformations(vc, curLength,
                            metrics, proofData);
        }

        return retval;
    }
}
