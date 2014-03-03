/**
 * AlternativeProofStep.java
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

public class AlternativeProofStep {

    private final ProofPathSuggestion myStep;
    private final VC myVCAfter;

    public AlternativeProofStep(ProofPathSuggestion step, VC after) {
        myStep = step;
        myVCAfter = after;
    }

    public ProofPathSuggestion getStep() {
        return myStep;
    }

    public VC getVCAfter() {
        return myVCAfter;
    }

    public String toString() {
        String retval;

        if (myStep.pathNote != null) {
            retval = myStep.pathNote + "\n\n";
        }
        else {
            retval = "";
        }

        retval += "Applying " + myStep + "...\n\n";

        retval += myVCAfter + "\n";

        return retval;
    }
}
