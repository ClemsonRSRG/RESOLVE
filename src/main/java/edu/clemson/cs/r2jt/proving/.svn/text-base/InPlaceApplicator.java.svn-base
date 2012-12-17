package edu.clemson.cs.r2jt.proving;

/**
 * <p>An <code>InPlaceApplicator</code> applies a <code>MatchReplace</code> by
 * identifying expressions against which it can match and replacing those
 * expressions in-place, generating new <code>ImmutableConjuncts</code> with the
 * same number of, but possibly different, conjuncts.</p>
 */
public class InPlaceApplicator implements ReplacementApplicator {
	
	/**
	 * <p>The original list of conjuncts, on which to iterate over possible
	 * replacements.</p>
	 * 
	 * <p>INVARIANT: <code>myConjuncts != null</code></p>
	 */
	private final ImmutableConjuncts myConjuncts;
	
	/**
	 * <p>The workhorse to perform the actual matches and suggest the 
	 * replacements.</p>
	 */
	private final ReplacementSuggester mySuggester;
		
	/**
	 * <p>Creates a new <code>InPlaceApplicator</code> that will iterate 
	 * over all single replacements (according to the provided 
	 * <code>matcher</code>) available in the provided list of conjuncts, in
	 * place.</p>
	 * 
	 * @param conjuncts The expressions in which to make the single replacement.
	 * @param matcher The matcher to govern what gets replaced and with what.
	 */
	public InPlaceApplicator(ImmutableConjuncts conjuncts, 
			NewMatchReplace matcher) {
		
		myConjuncts = conjuncts;
		
		mySuggester = new ReplacementSuggester(conjuncts, matcher);
	}
	
	@Override
	public ImmutableConjuncts getNextApplication() {
		
		ImmutableConjuncts retval;
		
		ReplacementSuggester.Suggestion s = mySuggester.nextMatch();
		
		if (s == null) {
			retval = null;
		}
		else {
			ImmutableConjuncts newStuff = new ImmutableConjuncts(s.newConjunct);
			
			int sConjunctIndex = s.conjunctIndex;
			
			retval = myConjuncts.removed(sConjunctIndex).inserted(
					sConjunctIndex, newStuff);
		}
		
		return retval;
	}
}
