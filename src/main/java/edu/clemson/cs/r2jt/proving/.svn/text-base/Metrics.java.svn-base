package edu.clemson.cs.r2jt.proving;

import java.math.BigInteger;

/**
 * <p>The <code>Metrics</code> class contains aggregate data about a full proof
 * attempt.</p>
 */
class Metrics {
	public BigInteger numProofsConsidered;
	public BigInteger numTimesBacktracked;
	
	public long ruleCount, rulesTried;
	public ProverListener progressListener;
	
	public ActionCanceller actionCanceller;
	
	public Metrics() {
		clear();
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
	
	public void clear() {
		numTimesBacktracked = BigInteger.ZERO;
		numProofsConsidered = BigInteger.ZERO;
		ruleCount = 0;
		rulesTried = 0;
	}
}
