package edu.clemson.cs.r2jt.proving;

/**
 * <p>A <code>ReplacementApplicator</code> provides a mechanism for iterating 
 * over all possible single applications of a <code>MatchReplace</code> over a 
 * set of conjuncts.</p>
 * 
 * <p>This class is intended to succeed MatchApplicator.  The former should be
 * phased out and eventually wholly replaced with this one.</p>
 */
public interface ReplacementApplicator {
	
	/**
	 * <p>Returns a deep copy of the conjuncts provided to the constructor with
	 * a single possible replacement made (defined by the matcher provided to
	 * the constructor).  Each call to this method will return a new deep copy,
	 * each with a different single replacement from any previous call, until
	 * no such replacement is possible, at which time it will return 
	 * <code>null</code>.</p>
	 *  
	 * @return Either the next possible single replacement, or 
	 *         <code>null</code> if there are no further non-redundant
	 *         replacements.
	 */
	public ImmutableConjuncts getNextApplication();
}
