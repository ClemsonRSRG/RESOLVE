package edu.clemson.cs.r2jt.proving;

import edu.clemson.cs.r2jt.absyn.Exp;

public interface FitnessFunction<T extends Exp> {
	
	/**
	 * <p>Returns a value between 0 and 1, inclusive representing the relative
	 * fitness of the given rule to be applied to the verification condition
	 * provided.</p>
	 * 
	 * @param rule
	 * @param vc
	 * @return
	 */
	public double determineFitness(T rule, VerificationCondition vc);
}
