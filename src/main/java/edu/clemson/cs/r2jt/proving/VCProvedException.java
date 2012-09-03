package edu.clemson.cs.r2jt.proving;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import edu.clemson.cs.r2jt.verification.AssertiveCode;

public class VCProvedException extends ProverException {
	private static final long serialVersionUID = -167079179043597290L;
	
	private Deque<Object> mySteps = new LinkedList<Object>();
	
	private VC myOriginalVC;
	
	public VCProvedException(Metrics metrics) {
		super(metrics);
	}
	
	public VCProvedException(String msg, AssertiveCode code, 
			Metrics metrics) {
		super(msg, code, metrics);
	}
	
	public void addStep(Object s) {
		mySteps.add(s);
	}
	
	public void setOriginal(VC vc) {
		myOriginalVC = vc;
	}
	
	public void setMetrics(Metrics m) {
		myMetrics = m;
	}
	
	public String toString() {
		String retval = "";
		
		if (myOriginalVC != null) {
			retval += "==== Proof for VC " + myOriginalVC.getName() + 
				" ====\n\n";
			
			retval += myOriginalVC + "\n";
		}
		
		Iterator<Object> iter = mySteps.descendingIterator();
		while (iter.hasNext()) {
			retval += iter.next();
		}
		
		retval += "Done.\n\n";
		
		return retval;
	}
}
