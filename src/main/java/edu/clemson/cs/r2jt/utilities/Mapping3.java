/**
 * Mapping3.java
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
package edu.clemson.cs.r2jt.utilities;

/**
 * <p>A three-parameter mapping.</p>
 */
public interface Mapping3<P1, P2, P3, R> {

    public R map(P1 p1, P2 p2, P3 p3);
}
