/**
 * StaticAntecedentIterator.java
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

public class StaticAntecedentIterator implements Iterator<VC> {

    private final Antecedent myAntecedent;
    private final Iterator<Consequent> myConsequentIterator;
    private String myOriginalVCName;

    public StaticAntecedentIterator(String originalVCName, Antecedent a,
            Iterator<Consequent> i) {

        myAntecedent = a;
        myConsequentIterator = i;
        myOriginalVCName = originalVCName;
    }

    @Override
    public boolean hasNext() {
        return myConsequentIterator.hasNext();
    }

    @Override
    public VC next() {
        return new VC(myOriginalVCName, myAntecedent, myConsequentIterator
                .next(), true);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
