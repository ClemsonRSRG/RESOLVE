/**
 * ConcatenatingTransformationChooser.java
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
 * <p>A <code>ConcatenatingTransformationChooser</code> conbines two existing
 * <code>TransormationChooser</code>s by simply suggesting transformations from
 * each in sequent.  That is, all the suggestions from the first, then all the
 * suggestions from the second.</p>
 */
public class ConcatenatingTransformationChooser
        implements
            TransformationChooser {

    private final TransformationChooser myFirst, mySecond;

    public ConcatenatingTransformationChooser(TransformationChooser first,
            TransformationChooser second) {

        myFirst = first;
        mySecond = second;
    }

    @Override
    public String toString() {
        return "Concatenate(" + myFirst + " with " + mySecond + ")";
    }

    @Override
    public void preoptimizeForVC(VC vc) {
        myFirst.preoptimizeForVC(vc);
        mySecond.preoptimizeForVC(vc);
    }

    @Override
    public Iterator<ProofPathSuggestion> suggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d) {

        return new ChainingIterator<ProofPathSuggestion>(myFirst
                .suggestTransformations(vc, curLength, metrics, d), mySecond
                .suggestTransformations(vc, curLength, metrics, d));
    }
}
