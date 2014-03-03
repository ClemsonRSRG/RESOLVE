/**
 * ConsequentCastMapping.java
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

import edu.clemson.cs.r2jt.utilities.Mapping;

public class ConsequentCastMapping
        implements
            Mapping<ImmutableConjuncts, Consequent> {

    @Override
    public Consequent map(ImmutableConjuncts i) {
        return new Consequent(i);
    }
}
