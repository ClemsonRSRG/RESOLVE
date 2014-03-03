/**
 * ProverResult.java
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
package edu.clemson.cs.r2jt.proving2;

import java.util.HashMap;
import java.util.Map;

public class ProverResult {

    private Map<VC, ProofResult> myResults = new HashMap<VC, ProofResult>();

    public void addVCResult(VC vc, ProofResult result) {
        myResults.put(vc, result);
    }
}
