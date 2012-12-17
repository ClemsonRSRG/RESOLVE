package edu.clemson.cs.r2jt.proving;

import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>A <code>RuleNormalizer</code> acts as a bridge between theorems and other
 * mathematical rules for transformation expressed as <code>Exp</code>s and
 * the <code>VCTransformer</code>s used by the proof subsystem to apply such
 * transformations.  It transforms <code>Exp</code>s into iterable sets of 
 * <code>VCTransformer</code>s.</p>
 * 
 * <p>In addition, it acts as a filter, eliminating those <code>Exp</code>
 * unsuitable to its purpose by returning empty sets for them.</p>
 */
public interface RuleNormalizer {
	
	/**
	 * <p>Takes an <code>Exp</code> representing a mathematical rule and returns
	 * an <code>Iterable</codE> over <code>VCTransformer</code>s that represent
	 * the applications of that rule.  May return an empty set, but will not
	 * return <code>null</code>.</p>
	 * 
	 * @param e The mathematical rule.
	 * @return A non-null iterable set of <code>VCTransformer</code>s.
	 */
	public Iterable<VCTransformer> normalize(PExp e);
}
