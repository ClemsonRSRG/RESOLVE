/**
 * InPlaceApplicatorFactory.java
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

public class InPlaceApplicatorFactory implements ReplacementApplicatorFactory {

    private final NewMatchReplace myMatcher;

    public InPlaceApplicatorFactory(NewMatchReplace m) {
        myMatcher = m;
    }

    @Override
    public ReplacementApplicator newApplicatorOver(ImmutableConjuncts c) {
        return new InPlaceApplicator(c, myMatcher);
    }

    public String toString() {
        return myMatcher.toString();
    }
}
