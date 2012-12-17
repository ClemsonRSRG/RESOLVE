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
