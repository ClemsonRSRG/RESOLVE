package edu.clemson.cs.r2jt.proving;


import java.util.List;

import edu.clemson.cs.r2jt.absyn.Exp;

public class ProofStep {
	private String myDescription;
	private List<Exp> myAlteredConfirms;
	
	private VerificationCondition myVC;
	
	public ProofStep(String description, List<Exp> confirms) {
		myDescription = description;
		myAlteredConfirms = confirms;
	}
	
	public ProofStep(String description, VerificationCondition vC) {
		myDescription = description;
		myVC = vC;
	}
	
	public String toString() {
		String retval = "\nApplied: " + myDescription + "\n\n";
		
		if (myVC == null) {
			retval += debugPrintConjuncts(myAlteredConfirms);	
		}
		else {
			retval += myVC;
		}
		return "\nApplied: " + myDescription + "\n\n" +
			debugPrintConjuncts(myAlteredConfirms);
	}
	
	private static String debugPrintConjuncts(List<Exp> conjuncts) {
		String retval = "";
		boolean first = true;
		for (Exp e : conjuncts) {
			if (!first) {
				retval += " and ";
			}
		    retval += e.toString(0);
			first = false;
		}
		retval += "\n";
		return retval;
	}
}
