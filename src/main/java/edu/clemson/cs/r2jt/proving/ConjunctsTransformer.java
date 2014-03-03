/**
 * ConjunctsTransformer.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>A <code>ConjunctsTransformer</code> maps a set of 
 * <code>ImmutableConjuncts</code> into one or more new sets of conjuncts based
 * on some predefined rule.</p>
 */
public interface ConjunctsTransformer
        extends
            Transformer<ImmutableConjuncts, Iterator<ImmutableConjuncts>> {

}
