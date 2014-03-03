/**
 * ReplacementApplicatorFactory.java
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
 * <p>A <code>ReplacementApplicatorFactory</code> provides a mechanism for
 * delivering new <code>ReplacementApplicator</code>s for different sets of
 * <code>ImmutableConjuncts</code>.</p>
 */
public interface ReplacementApplicatorFactory {

    public ReplacementApplicator newApplicatorOver(ImmutableConjuncts c);
}
