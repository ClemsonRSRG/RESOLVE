/**
 * AntecedentTransformer.java
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
 * <p>An <code>AntecedentTransformer</code> provides a mechanism for iterating
 * over various new versions of <code>Antecedent</code>s according to some
 * pre-defined rule.</p>
 */
public interface AntecedentTransformer
        extends
            Transformer<Antecedent, Iterator<Antecedent>> {

}
