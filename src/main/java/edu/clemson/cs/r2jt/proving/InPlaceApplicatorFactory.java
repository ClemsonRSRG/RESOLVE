package edu.clemson.cs.r2jt.proving;

public class InPlaceApplicatorFactory implements ReplacementApplicatorFactory {

	private final NewMatchReplace myMatcher;
	
	public InPlaceApplicatorFactory(NewMatchReplace m) {
		myMatcher = m;
	}
	
	@Override
	public ReplacementApplicator newApplicatorOver(ImmutableConjuncts c) {
		return new InPlaceApplicator(c, myMatcher);
	}
	
	public String toString() {
		return myMatcher.toString();
	}
}
