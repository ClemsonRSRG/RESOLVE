/**
 * GuidedTransformationChooser.java
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

public class GuidedTransformationChooser extends AbstractTransformationChooser {

    public GuidedTransformationChooser(Iterable<VCTransformer> library) {
        super(library);
    }

    @Override
    public Iterator<ProofPathSuggestion> doSuggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d,
            Iterable<VCTransformer> localTheorems) {

        return new GuidedListSelectIterator<ProofPathSuggestion>("Choose rule",
                vc.toString(),
                new LazyMappingIterator<VCTransformer, ProofPathSuggestion>(
                        getTransformerLibrary().iterator(),
                        new StaticProofDataSuggestionMapping(d)));
    }
}
