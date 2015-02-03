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

/**
 * <p>A <code>ProofPathSuggestion</code> is the immutable representation of a 
 * single suggestion of a <code>TransformationChooser</code> regarding what step
 * a proof should follow next.  This amounts to the suggestion of a 
 * <code>VCTransformer</code> and the new <code>ProofData</code> that should 
 * accompany the proof should this step be chosen.</p>
 */
public class ProofPathSuggestion {

    public final VCTransformer step;
    public final ProofData data;

    /**
     * <p>A note to be added to the proof file before continuing with 
     * information about this step.  This allows you to place a note in the 
     * proof file about when a particular TransformationChooser took over, for 
     * example.  If this value is <code>null</code>, then no message will be 
     * printed.</p>
     */
    public final String pathNote;

    /**
     * <p>A note to be printed to the console along with the state of the VC
     * when this suggestion is tried.  Depending on the value of 
     * <code>debugPrevious</code>, either the VC before or after following this
     * suggestion will be printed.  This printing occurs when the step is tried
     * and will thus be printed even if this step is not included in any
     * successful proof.  Care must be taken to avoid printing debug messages
     * from suggestions that will crop up many, many times.</p>
     * 
     * <p>These debugging messages will only be printed if 
     * <code>Prover.FLAG_VERBOSE</code> is on.  A <code>null</code> message
     * indicates that no message should be printed.</p>
     */
    public final String debugNote;

    /**
     * <p>Indicates whether the message in <code>debugNote</code> should be
     * printed before (<code>false</code>) or after (<code>true</code>) the
     * VC transformation suggested by this step.  That is, should the VC be
     * printed as it was before the application of this step or as it is
     * after.  If <code>debugNote</code> is <code>null</code> this value is
     * ignored.</p>
     */
    public final boolean debugPrevious;

    public ProofPathSuggestion(VCTransformer step, ProofData data) {
        this(step, data, null, null, false);
    }

    public ProofPathSuggestion(VCTransformer step, ProofData data,
            String pathNote, String debugNote) {
        this(step, data, null, null, false);
    }

    public ProofPathSuggestion(VCTransformer step, ProofData data,
            String pathNote, String debugNote, boolean debugPrevious) {
        this.step = step;
        this.data = data;
        this.pathNote = pathNote;
        this.debugNote = debugNote;
        this.debugPrevious = debugPrevious;
    }

    public String toString() {
        return step.toString();
    }
}
