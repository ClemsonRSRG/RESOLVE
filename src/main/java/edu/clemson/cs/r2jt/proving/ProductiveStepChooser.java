/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 * <p>A <code>ProductiveStepChooser</code> wraps an existing 
 * <code>TransformationChooser</code> and transparently returns only its
 * productive transformations, as defined in the class comments of
 * {@link edu.clemson.cs.r2jt.proving.ProductiveStepsOnlyStep 
 *    ProductiveStepsOnlyStep}.</p>
 */
public class ProductiveStepChooser implements TransformationChooser {

    private static final ProductiveFilterMapping PRODUCTIVE_FILTER =
            new ProductiveFilterMapping();

    private final TransformationChooser myBaseChooser;

    public ProductiveStepChooser(TransformationChooser c) {

        myBaseChooser = c;
    }

    @Override
    public void preoptimizeForVC(VC vc) {
        myBaseChooser.preoptimizeForVC(vc);
    }

    @Override
    public Iterator<ProofPathSuggestion> suggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d) {

        return new LazyMappingIterator<ProofPathSuggestion, ProofPathSuggestion>(
                myBaseChooser.suggestTransformations(vc, curLength, metrics, d),
                PRODUCTIVE_FILTER);
    }

    @Override
    public String toString() {
        return "ProductiveStep(Steps from " + myBaseChooser + ")";
    }

    private static class ProductiveFilterMapping
            implements
                Mapping<ProofPathSuggestion, ProofPathSuggestion> {

        @Override
        public ProofPathSuggestion map(ProofPathSuggestion i) {

            return new ProofPathSuggestion(new ProductiveStepsOnlyStep(i.step),
                    i.data, i.pathNote, i.debugNote);
        }
    }
}
