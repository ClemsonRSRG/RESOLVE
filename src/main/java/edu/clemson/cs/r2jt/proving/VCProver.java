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

import java.math.BigInteger;

public interface VCProver {

    /**
     * <p>Attempts to prove a single VC.  If this method returns without 
     * throwing an exception, then the VC was proved.</p>
     * 
     * @param vC The verification condition to be proved.  May not be 
     *           <code>null</code>.
     * @param theorems A list of theorems that may be applied as part of the
     *                 proof.  May not be <code>null</code>.
     * @param maxDepth The maximum number of steps the prover should attempt 
     *                 before giving up on a proof.
     * @param metrics A reference to the metrics the prover should keep on the
     *                proof in progress.  May not be <code>null</code>.
     *            
     * @throws UnableToProveException If the VC cannot be proved in a
     *                                reasonable amount of time.
     * @throws VCInconsistentException If the VC can be proved inconsistent.
     * @throws NullPointerException If <code>vC</code>, <code>theorems</code>,
     *                              or <code>metrics</code> is 
     *                              <code>null</code>.
     */
    public void prove(final VerificationCondition vC,
            final ProverListener progressListener,
            ActionCanceller actionCanceller, long timeoutAt)
            throws VCInconsistentException,
                VCProvedException,
                UnableToProveException;

    /**
     * <p>Returns an approximation of the number of proofs this prover will
     * have to consider.  This approximation is intended to only be meaningful
     * as a ratio with the value returned from other <code>VCProver</code>s.
     * So, if <code>VCProver</code> <code>A</code> returns 10000 from a call
     * to this method and <code>VCProver</code> <code>B</code> returns 20000,
     * neither may produce anywhere near the approximated number of proofs
     * given, but prover <code>B</code> will have to search a proof space 
     * approximately twice large as <code>A</code>.</p>
     * 
     * <p>May return -1 if the prover is not able to provide any estimate at
     * all, for example if the proofs are being provided by a human user 
     * on-demand.</p>
     * 
     * @return A representation of the size of the proof space that is roughly
     * proportional to the size of the proof space with respect to values
     * returned by this method from other <code>VCProver</code>s.
     */
    public BigInteger getProofCountOrder();
}
