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

/**
 * <p>A <code>TransformationChooser</code> is the "brain" of the prover.  It
 * decides what step the prover should take next given information about the 
 * current state of the VC and the proof. It does this by returning an 
 * <code>Iterator</code> over a set of transformations in the order they should 
 * be attempted.</p>
 */
public interface TransformationChooser {

    /**
     * <p>Called at the beginning of each proof attempt to give the
     * <code>TransformationChooser</code> an opportunity to perform any per-VC
     * optimizations on its data before proving of the VC begins.</p>
     * 
     * @param vc The VC to be proved.
     */
    public void preoptimizeForVC(VC vc);

    /**
     * <p>Returns a set of suggestions, in order of priority from highest to
     * lowest, for the next transformation to apply to a VC to attempt to
     * produce a proof.  The set of transformations may be empty if this
     * <code>TransformerChooser</code> believes the prover should abandon the
     * current proof-path, backtrack, and try another.</p>
     * 
     * @param vC The current VC to be transformed.
     * @param curLength The number of steps in the current proof path that 
     * 					represent contributions by this chooser.  (Steps from
     *                  choosers that defer to this one are not included.)
     * @param metrics A large collection of auxiliary data about the proof.
     * @param pastStates A list of past VC states.
     * 
     * @return A non-null iterable set of <code>VCTransformer</code>s.
     */
    public Iterator<ProofPathSuggestion> suggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d);
}
