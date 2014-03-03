/**
 * Transformation.java
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
 * <p>A <code>Transformation</code> defines a rule which may be applied to an
 * input (perhaps in multiple different ways) to yield an output.</p>
 *
 * @param <T> The type accepted by the transformation.
 */
public interface Transformation<T> {

    public Iterator<T> transform(T original);
}
