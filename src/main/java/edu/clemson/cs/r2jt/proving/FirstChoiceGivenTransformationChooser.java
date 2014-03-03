/**
 * FirstChoiceGivenTransformationChooser.java
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

public class FirstChoiceGivenTransformationChooser
        implements
            TransformationChooser {

    private final TransformationChooser myBaseChooser;
    private final VCTransformer myFirstChoice;

    public FirstChoiceGivenTransformationChooser(
            TransformationChooser baseChooser, VCTransformer choice) {

        myBaseChooser = baseChooser;
        myFirstChoice = choice;
    }

    @Override
    public void preoptimizeForVC(VC vc) {
        myBaseChooser.preoptimizeForVC(vc);
    }

    @Override
    public Iterator<ProofPathSuggestion> suggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d) {

        return new ChainingIterator<ProofPathSuggestion>(
                new SingletonIterator<ProofPathSuggestion>(
                        new ProofPathSuggestion(myFirstChoice, d)),
                myBaseChooser.suggestTransformations(vc, curLength, metrics, d));
    }

    @Override
    public String toString() {
        return "FirstChoiceGiven(" + myFirstChoice + ", added at the front of "
                + myBaseChooser + ")";
    }
}
