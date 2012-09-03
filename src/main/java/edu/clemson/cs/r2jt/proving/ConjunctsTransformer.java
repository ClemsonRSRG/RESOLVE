package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>A <code>ConjunctsTransformer</code> maps a set of 
 * <code>ImmutableConjuncts</code> into one or more new sets of conjuncts based
 * on some predefined rule.</p>
 */
public interface ConjunctsTransformer 
		extends Transformer<ImmutableConjuncts, Iterator<ImmutableConjuncts>> {

}
