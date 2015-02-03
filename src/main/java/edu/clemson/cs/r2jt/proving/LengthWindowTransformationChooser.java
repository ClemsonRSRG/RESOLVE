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
 * <p>A <code>LengthWindowTransformationChooser</code> is a convenience class
 * to wrap both a <code>SimplifyingTransformationChooser</code> and a
 * <code>TetheredTransformationChooser</code> to define an "interesting window"
 * over the depths of the generated proofs--before which simplifications are
 * not attempted and after which proofs are tethered.</p>
 */
public class LengthWindowTransformationChooser implements TransformationChooser {

    private final TransformationChooser mySourceChooser;

    /**
     * <p>Creates a new <code>LengthWindowTransformationChooser</code> that will
     * begin interleaving simplification steps at depth <code>minDepth</code>
     * and consider proof-paths of up to length <code>maxDepth</code>, 
     * calculated from the perspective of <code>source</code>, i.e., not 
     * counting the interleaved simplification steps.</p>
     * 
     * @param source The source chooser to defer to.
     * @param minDepth The depth at which simplification steps should begin to
     *                 be interleaved.
     * @param maxDepth The depth, from the perspective of <code>source</code>
     *                 at which the proof should begin to backtrack.
     */
    public LengthWindowTransformationChooser(TransformationChooser source,
            int minDepth, int maxDepth) {

        mySourceChooser =
                new SimplifyingTransformationChooser(
                        new TetheredTransformationChooser(source, maxDepth),
                        minDepth);
        /*
        mySourceChooser = new TetheredTransformationChooser(
        		new SimplifyingTransformationChooser(source, minDepth), 
        		SimplifyingTransformationChooser.getTrueDepth(
        				maxDepth, minDepth));*/
    }

    @Override
    public void preoptimizeForVC(VC vc) {
        mySourceChooser.preoptimizeForVC(vc);
    }

    @Override
    public Iterator<ProofPathSuggestion> suggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d) {

        return mySourceChooser
                .suggestTransformations(vc, curLength, metrics, d);
    }

    @Override
    public String toString() {
        return "" + mySourceChooser;
    }
}
