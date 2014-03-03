/**
 * ConsequentTransformerAdapter.java
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

import edu.clemson.cs.r2jt.utilities.Mapping;

public class ConsequentTransformerAdapter implements ConsequentTransformer {

    private static final Mapping<ImmutableConjuncts, Consequent> MAP_TO_CONSEQUENTS =
            new ConsequentCastMapping();

    private final ConjunctsTransformer myTransformer;

    public ConsequentTransformerAdapter(ConjunctsTransformer t) {
        myTransformer = t;
    }

    public Iterator<Consequent> transform(Consequent original) {
        return new LazyMappingIterator<ImmutableConjuncts, Consequent>(
                myTransformer.transform(original), MAP_TO_CONSEQUENTS);
    }

    public String toString() {
        return myTransformer.toString();
    }
}
