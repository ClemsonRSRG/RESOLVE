/**
 * ProverException.java
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

import edu.clemson.cs.r2jt.verification.AssertiveCode;

public abstract class ProverException extends Exception {

    private AssertiveCode myVC;
    protected Metrics myMetrics;

    public ProverException(Metrics metrics) {
        super();
        myMetrics = metrics;
    }

    public ProverException(String msg, AssertiveCode VC, Metrics metrics) {
        super(msg);
        myMetrics = metrics;
        myVC = VC;
    }

    public Metrics getMetrics() {
        return myMetrics;
    }

    public AssertiveCode getOffendingVC() {
        return myVC;
    }
}
