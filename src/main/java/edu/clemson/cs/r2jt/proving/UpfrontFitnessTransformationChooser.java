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
