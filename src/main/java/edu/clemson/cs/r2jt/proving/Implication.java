package edu.clemson.cs.r2jt.proving;

import edu.clemson.cs.r2jt.absyn.Exp;

public class Implication {
	private Exp myAntecedent;
	private Exp myConsequent;
	
	public Implication(Exp antecedent, Exp consequent) {
		myAntecedent = antecedent;
		myConsequent = consequent;
	}
	
	public Exp getAntecedent() {
		return myAntecedent;
	}
	
	public Exp getConsequent() {
		return myConsequent;
	}
	
	public String toString() {
		return myAntecedent + " --> " + myConsequent;
	}
}
