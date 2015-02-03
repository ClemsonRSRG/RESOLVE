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

import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 * <p>An <code>OnlyBeforeChooser</code> merges two existing 
 * <code>TransformationChooser</code>s, <code>A</code> and <code>B</code>, such
 * that suggested <code>VCTransformer</code>s are the result of concatenating
 * the results of <code>A</code> and <code>B</code> (in a configurable order) if
 * this chooser has not, previously in the currently-being-explored proof, 
 * already contributed a transformer from <code>B</code>; otherwise, only the
 * results of <code>B</code> are returned.</p>
 *
 * <p>Put more simply, once this chooser contributes a transformer from 
 * <code>B</code> to the current proof, it will <em>only</em> contribute 
 * transformers from <code>B</code> farther along in the same proof.</p>
 */
public class OnlyBeforeChooser implements TransformationChooser {

    private static final Object B_SUGGESTED = new Object();
    private final ProofDataAugmenter AUGMENTER = new ProofDataAugmenter(this);

    private final TransformationChooser myA;
    private final TransformationChooser myB;
    private final boolean myBFirstFlag;

    public OnlyBeforeChooser(TransformationChooser a, TransformationChooser b) {
        this(a, b, false);
    }

    public OnlyBeforeChooser(TransformationChooser a, TransformationChooser b,
            boolean bFirst) {

        myA = a;
        myB = b;
        myBFirstFlag = bFirst;
    }

    @Override
    public String toString() {
        String retval =
                "OnlyBefore(Steps from " + myA + " only before those from "
                        + myB;

        if (myBFirstFlag) {
            retval += ", but trying to avoid steps from the former.";
        }
        else {
            retval += ", favoring steps from the former.";
        }

        return retval + ")";
    }

    @Override
    public void preoptimizeForVC(VC vc) {
        myA.preoptimizeForVC(vc);
        myB.preoptimizeForVC(vc);
    }

    @Override
    public Iterator<ProofPathSuggestion> suggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d) {

        Iterator<ProofPathSuggestion> retval;

        Iterator<ProofPathSuggestion> bResults =
                myB.suggestTransformations(vc, curLength, metrics, d);

        if (d.attributeDefined(this, B_SUGGESTED)) {
            retval = bResults;
        }
        else {
            bResults =
                    new LazyMappingIterator<ProofPathSuggestion, ProofPathSuggestion>(
                            bResults, AUGMENTER);

            Iterator<ProofPathSuggestion> aResults, first, second;

            aResults = myA.suggestTransformations(vc, curLength, metrics, d);

            if (myBFirstFlag) {
                first = bResults;
                second = aResults;
            }
            else {
                first = aResults;
                second = bResults;
            }

            retval = new ChainingIterator<ProofPathSuggestion>(first, second);
        }

        return retval;
    }

    private static class ProofDataAugmenter
            implements
                Mapping<ProofPathSuggestion, ProofPathSuggestion> {

        private static final Object DUMMY = new Object();
        private final TransformationChooser myParent;

        public ProofDataAugmenter(TransformationChooser parent) {
            myParent = parent;
        }

        @Override
        public ProofPathSuggestion map(ProofPathSuggestion i) {
            return new ProofPathSuggestion(i.step, i.data.putAttribute(
                    myParent, B_SUGGESTED, DUMMY));
        }
    }
}
