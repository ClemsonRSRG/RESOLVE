package edu.clemson.cs.r2jt.proving;

import edu.clemson.cs.r2jt.absyn.Exp;

/**
 * <p>Defines a general search/replace mechanism.</p>
 * 
 * @author H. Smith
 */
public interface MatchReplace {
	/**
	 * <p>Returns <code>true</code> <strong>iff</strong> the provided expression
	 * could be replaced.</p>
	 * 
	 * @param e The expression to test.
	 * 
	 * @return <code>true</code> <strong>iff</strong> the provided expression
	 *         could be replaced.
	 */
	public boolean couldReplace(Exp e);
	
	/**
	 * <p>Returns the <code>Exp</code> which should replace the expression that
	 * was provided in the last call to <code>couldReplace</code>().</p>
	 * 
	 * @return The <code>Exp</code> which should replace the expression that
	 *         was provided in the last call to <code>couldReplace</code>().  
	 *         Undefined if <code>couldReplace</code>() has not yet been called.
	 */
	public Exp getReplacement();
	
	/**
	 * <p>Gets a copy of the pattern this MatchReplace is looking for.</p>
	 * 
	 * @return A deep copy of the pattern being used to match.
	 */
	public Exp getPattern();
	
	/**
	 * <p>Gets a copy of the template this MatchReplace will use to produce the
	 * replacements for anything matched by the pattern.</p>
	 * 
	 * @return A deep copy of the template used to replace.
	 */
	public Exp getExpansionTemplate();
}
