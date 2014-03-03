/**
 * ProofState.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.proving;

/**
 * <p>A <code>ProofState</code> is the immutable cartesian product of a 
 * <code>VC</code> and a <code>ProofData</code>.  That is, it represents 
 * the current state of a proof--the form of the <code>VC</code> and data about
 * the proof process so far.</p>
 */
public class ProofState {

    public final VC vc;
    public final ProofData data;

    public ProofState(VC vc, ProofData data) {
        this.vc = vc;
        this.data = data;
    }
}
