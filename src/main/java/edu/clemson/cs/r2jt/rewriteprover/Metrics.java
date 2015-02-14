/**
 * Metrics.java
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
package edu.clemson.cs.r2jt.rewriteprover;

import java.math.BigInteger;

/**
 * <p>The <code>Metrics</code> class contains aggregate data about a full proof
 * attempt.</p>
 */
public class Metrics {

    public BigInteger numProofsConsidered;
    public BigInteger numTimesBacktracked;

    public long ruleCount, rulesTried;
    public ProverListener progressListener;

    public ActionCanceller actionCanceller;

    private long myProofDuration, myTimeout;

    public Metrics(long duration, long timeout) {
        clear();
        myProofDuration = duration;
        myTimeout = timeout;
    }

    public BigInteger getNumProofsConsidered() {
        return numProofsConsidered;
    }

    public void incrementProofsConsidered() {
        numProofsConsidered = numProofsConsidered.add(BigInteger.ONE);
    }

    public void accumulate(Metrics m) {
        numProofsConsidered = numProofsConsidered.add(m.numProofsConsidered);
        numTimesBacktracked = numTimesBacktracked.add(m.numTimesBacktracked);
    }

    public long getTimeout() {
        return myTimeout;
    }

    public long getProofDuration() {
        return myProofDuration;
    }

    public void clear() {
        numTimesBacktracked = BigInteger.ZERO;
        numProofsConsidered = BigInteger.ZERO;
        ruleCount = 0;
        rulesTried = 0;
        myProofDuration = 0;
        myTimeout = 0;
    }
}
