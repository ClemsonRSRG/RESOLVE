package edu.clemson.cs.r2jt.proving;

import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>Defines a general search/replace mechanism.</p>
 * 
 * <p><strong>N.B.:</strong>  This is intended as a drop-in replacement for
 * {@link edu.clemson.cs.r2jt.proving.MatchReplace MatchReplace} except that it
 * operates on {@link edu.clemson.cs.r2jt.proving.absyn.PExp PExp}s rather than
 * {@link edu.clemson.cs.r2jt.absyn.Exp Exp}s.  When the new prover is complete
 * and well-tested, <code>MatchReplace</code> should be removed entirely and
 * this class should be renamed.</p>
 * 
 * @author H. Smith
 */
public interface NewMatchReplace {
	/**
	 * <p>Returns <code>true</code> <strong>iff</strong> the provided expression
	 * could be replaced.</p>
	 * 
	 * @param e The expression to test.
	 * 
	 * @return <code>true</code> <strong>iff</strong> the provided expression
	 *         could be replaced.
	 */
	public boolean couldReplace(PExp e);
	
	/**
	 * <p>Returns the <code>PExp</code> which should replace the expression that
	 * was provided in the last call to <code>couldReplace</code>().</p>
	 * 
	 * @return The <code>PExp</code> which should replace the expression that
	 *         was provided in the last call to <code>couldReplace</code>().  
	 *         Undefined if <code>couldReplace</code>() has not yet been called.
	 */
	public PExp getReplacement();
	
	/**
	 * <p>Gets a copy of the pattern this MatchReplace is looking for.</p>
	 * 
	 * @return A deep copy of the pattern being used to match.
	 */
	public PExp getPattern();
	
	/**
	 * <p>Gets a copy of the template this MatchReplace will use to produce the
	 * replacements for anything matched by the pattern.</p>
	 * 
	 * @return A deep copy of the template used to replace.
	 */
	public PExp getExpansionTemplate();
}
