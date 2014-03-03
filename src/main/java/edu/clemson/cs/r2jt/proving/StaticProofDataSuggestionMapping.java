/**
 * StaticProofDataSuggestionMapping.java
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

public class StaticProofDataSuggestionMapping
        implements
            Mapping<VCTransformer, ProofPathSuggestion> {

    private final ProofData myData;

    public StaticProofDataSuggestionMapping(ProofData data) {
        myData = data;
    }

    @Override
    public ProofPathSuggestion map(VCTransformer i) {
        return new ProofPathSuggestion(i, myData);
    }
}
