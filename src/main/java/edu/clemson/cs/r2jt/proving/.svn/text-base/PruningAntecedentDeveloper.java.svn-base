package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.clemson.cs.r2jt.absyn.Exp;

/**
 * <p>A <code>PruningAntecedentDeveloper<code> wraps an existing 
 * <code>AntecedentDeveloper</code> and prunes any suggested developments that
 * states a fact in terms of the same literals as an existing fact.  So, for 
 * example, if the original antecedent contains the fact <code>X = 1 + 0</code>
 * and the wrapped <code>AntecedentDeveloper</code> suggests, possibly among 
 * some others, the development <code>X = 1 + 0 + 0</code>, this development
 * will be pruned since it does not introduce any new function, variable, or
 * constant literals.</p>
 * 
 * <p>This is used in batch theory development as a heuristic to arrive at
 * "useful" new facts that put existing facts in new terms rather than simply
 * making endless identity transformations.</p>
 */
public class PruningAntecedentDeveloper implements AntecedentDeveloper {

	@Override
	public Iterator<Antecedent> transform(Antecedent source) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
