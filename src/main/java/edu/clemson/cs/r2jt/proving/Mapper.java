/**
 * Mapper.java
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

/**
 * <p>A <code>Mapper</code> provides a general mechanism for mapping objects of
 * type <code>I</code> to objects of type <code>O</code>.</p>
 *
 * @param <I> The input type of the mapper.
 * @param <O> The output type of the mapper.
 */
public interface Mapper<I, O> {

    public O map(I i);
}
