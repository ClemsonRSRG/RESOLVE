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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>Provides some skeleton code for implementing a 
 * <code>TransformationChooser</code> to simplify development.</p>
 */
public abstract class AbstractTransformationChooser
        implements
            TransformationChooser {

    private final Iterable<VCTransformer> myTransformerLibrary;

    private ChainingIterable<VCTransformer> myPerVCSubstitutions;

    public AbstractTransformationChooser(Iterable<VCTransformer> library) {
        myTransformerLibrary = library;
    }

    /**
     * <p>A dummy implementation of 
     * {@link TransformationChooser#preoptimizeForVC} that does nothing.
     * Subclasses that need to implement per-VC optimizations should override
     * this method.</p>
     * 
     * @param vc The VC to be proved.
     */
    @Override
    public void preoptimizeForVC(VC vc) {

    }

    /**
     * <p>Returns the set of <code>VCTransformers</code> available for the 
     * current <code>VC</code>, which include the global library and those
     * theorems drawn from the VC's antecedent.</p>
     * 
     * @return An <code>Iterable</code> set of <code>VCTransformers</code> that
     *         may be applied to the current VC (i.e., the VC provided in the
     *         last call to <code>preoptimizeForVC()</code>.
     */
    protected final Iterable<VCTransformer> getTransformerLibrary() {
        return myTransformerLibrary;
    }

    @Override
    public final Iterator<ProofPathSuggestion> suggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d) {

        myPerVCSubstitutions = new ChainingIterable<VCTransformer>();
        myPerVCSubstitutions.add(myTransformerLibrary);

        List<VCTransformer> localTheorems = new LinkedList<VCTransformer>();

        RuleNormalizer n = new SubstitutionRuleNormalizer(false);

        for (PExp e : vc.getAntecedent()) {
            for (VCTransformer t : n.normalize(e)) {
                localTheorems.add(t);
            }
        }

        return new ChainingIterator<ProofPathSuggestion>(
                new LazyMappingIterator<VCTransformer, ProofPathSuggestion>(
                        localTheorems.iterator(),
                        new StaticProofDataSuggestionMapping(d)),
                doSuggestTransformations(vc, curLength, metrics, d,
                        localTheorems));
    }

    protected abstract Iterator<ProofPathSuggestion> doSuggestTransformations(
            VC vc, int curLength, Metrics metrics, ProofData d,
            Iterable<VCTransformer> localTheorems);
}
