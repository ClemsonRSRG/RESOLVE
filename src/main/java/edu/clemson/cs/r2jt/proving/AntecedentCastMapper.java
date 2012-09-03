package edu.clemson.cs.r2jt.proving;

public class AntecedentCastMapper 
		implements Mapper<ImmutableConjuncts, Antecedent>{

	@Override
	public Antecedent map(ImmutableConjuncts i) {
		return new Antecedent(i);
	}
}
