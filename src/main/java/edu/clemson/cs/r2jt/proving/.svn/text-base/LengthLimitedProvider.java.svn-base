package edu.clemson.cs.r2jt.proving;

import java.util.Deque;
import java.util.LinkedList;

public class LengthLimitedProvider extends RuleProvider {

	private RuleProvider mySourceProvider;
	private int myMaxLength;
	private KnownSizeIterator<MatchReplace> dummyIterator = 
		new SizedIterator<MatchReplace>(
				new LinkedList<MatchReplace>().iterator(), 0);
	
	public LengthLimitedProvider(RuleProvider sourceProvider, int maxLength) {
		mySourceProvider = sourceProvider;
		myMaxLength = maxLength;
	}
	
	public KnownSizeIterator<MatchReplace> consider(VerificationCondition vC, 
			int curLength, Metrics metrics, 
			Deque<VerificationCondition> pastStates) {
		
		KnownSizeIterator<MatchReplace> retval;
		
		if (curLength <= myMaxLength) {
			retval = mySourceProvider.consider(vC, curLength, metrics, 
					pastStates);
		}
		else {
			retval = dummyIterator;
		}
		
		return retval;
	}

	public int getApproximateRuleSetSize() {
		return mySourceProvider.getApproximateRuleSetSize();
	}
}
