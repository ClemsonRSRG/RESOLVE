package edu.clemson.cs.r2jt.proving;

public class ExtendingApplicatorFactory 
		implements ReplacementApplicatorFactory {

	private final NewMatchReplace myMatcher;
	
	public ExtendingApplicatorFactory(NewMatchReplace m) {
		myMatcher = m;
	}
	
	@Override
	public ReplacementApplicator newApplicatorOver(ImmutableConjuncts c) {
		return new ExtendingApplicator(c, myMatcher);
	}
	
	public String toString() {
		return myMatcher.toString();
	}
}
