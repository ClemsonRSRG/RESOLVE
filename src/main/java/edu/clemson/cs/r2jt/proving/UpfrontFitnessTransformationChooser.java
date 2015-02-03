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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.proving.absyn.PExp;

public class UpfrontFitnessTransformationChooser
        extends
            AbstractTransformationChooser {

    private final TransformerFitnessFunction myFitnessFunction;
    private final double myThreshold;
    private List<VCTransformer> myPerVCOrdering;
    private final CompileEnvironment myInstanceEnvironment;

    public UpfrontFitnessTransformationChooser(TransformerFitnessFunction f,
            Iterable<VCTransformer> library, double threshold,
            CompileEnvironment e) {

        super(library);
        myFitnessFunction = f;
        myThreshold = threshold;

        myInstanceEnvironment = e;
    }

    @Override
    public void preoptimizeForVC(VC vc) {
        myPerVCOrdering = new LinkedList<VCTransformer>();

        List<PriorityAugmentedObject<VCTransformer>> priorityList =
                new LinkedList<PriorityAugmentedObject<VCTransformer>>();

        double curFitness;
        Iterable<VCTransformer> library = getTransformerLibrary();
        for (VCTransformer curRule : library) {
            curFitness = myFitnessFunction.calculateFitness(curRule, vc);

            if (curFitness >= myThreshold) {
                priorityList.add(new PriorityAugmentedObject<VCTransformer>(
                        curRule, curFitness));
            }
        }

        Collections.sort(priorityList);

        if (myInstanceEnvironment.flags.isFlagSet(Prover.FLAG_VERBOSE)) {
            System.out.println(vc);
            System.out.println("Rules sorted by: " + myFitnessFunction);
        }

        for (PriorityAugmentedObject<VCTransformer> curRule : priorityList) {
            if (myInstanceEnvironment.flags.isFlagSet(Prover.FLAG_VERBOSE)) {
                System.out.println("  " + curRule.getPriority() + " \t\t "
                        + curRule.getObject());
            }

            myPerVCOrdering.add(curRule.getObject());
        }

        RuleNormalizer n = new SubstitutionRuleNormalizer(false);
        for (PExp e : vc.getAntecedent()) {
            for (VCTransformer t : n.normalize(e)) {
                myPerVCOrdering.add(t);
            }
        }
    }

    protected Iterator<ProofPathSuggestion> doSuggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d,
            Iterable<VCTransformer> localTheorems) {

        Iterator<ProofPathSuggestion> retval;

        retval =
                new LazyMappingIterator<VCTransformer, ProofPathSuggestion>(
                        myPerVCOrdering.iterator(),
                        new StaticProofDataSuggestionMapping(d));

        return retval;
    }

    @Override
    public String toString() {
        return "UpfrontFitness(Ranked by " + myFitnessFunction + ")";
    }
}
