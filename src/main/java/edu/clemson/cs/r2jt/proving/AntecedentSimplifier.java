/**
 * AntecedentSimplifier.java
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

public class AntecedentSimplifier implements AntecedentTransformer {

    private final AntecedentTransformer myBaseTransformer;

    public AntecedentSimplifier(AntecedentTransformer t) {
        myBaseTransformer = t;
    }

    @Override
    public Iterator<Antecedent> transform(Antecedent source) {
        return new SimplifyingIterator(myBaseTransformer.transform(source));
    }

    public class SimplifyingIterator implements Iterator<Antecedent> {

        private final Iterator<Antecedent> myBaseIterator;

        public SimplifyingIterator(Iterator<Antecedent> i) {
            myBaseIterator = i;
        }

        @Override
        public boolean hasNext() {
            return myBaseIterator.hasNext();
        }

        @Override
        public Antecedent next() {
            return myBaseIterator.next().eliminateObviousConjuncts()
                    .eliminateRedundantConjuncts();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
