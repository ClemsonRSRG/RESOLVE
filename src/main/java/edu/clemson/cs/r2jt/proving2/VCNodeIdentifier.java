/**
 * VCNodeIdentifier.java
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

import edu.clemson.cs.r2jt.proving.absyn.NodeIdentifier;

public class VCNodeIdentifier {

    private final VC myVC;
    private final boolean myAntecedentFlag;
    private final int myConjunctIndex;
    private final NodeIdentifier myNodeIdentifier;

    public VCNodeIdentifier(VC vc, boolean antecedent, int conjunct,
            NodeIdentifier nid) {

        myVC = vc;
        myAntecedentFlag = antecedent;
        myConjunctIndex = conjunct;
        myNodeIdentifier = nid;
    }
}
