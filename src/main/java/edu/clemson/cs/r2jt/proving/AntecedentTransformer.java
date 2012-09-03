package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>An <code>AntecedentTransformer</code> provides a mechanism for iterating
 * over various new versions of <code>Antecedent</code>s according to some
 * pre-defined rule.</p>
 */
public interface AntecedentTransformer
		extends Transformer<Antecedent, Iterator<Antecedent>> {
	
}
