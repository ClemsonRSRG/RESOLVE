package edu.clemson.cs.r2jt.proving;

import java.util.Set;

/**
 * <p>A straightforward implementation of 
 * <code>TransformerFitnessFunction</code> that gives greater relevance to those
 * transformers that operate on similar variable types and function names as
 * those that appear in the VC to be operated on.  Also gives greater relevance
 * to those transformers that simplify (i.e., reduce the number of variables or
 * functions) rather than complicate.</p>
 * 
 * <p>This fitness function advises against (i.e., returns a negative value from
 * <code>calculateFitness()</code>) the application of transformations that
 * introduce quantified variables.</p>
 */
public class SimpleTransformerFitnessFunction
        extends TransformerFitnessFunction {

    @Override
    public String toString() {
        return "Relevance Fitness";
    }

    @Override
    public double calculateFitness(VCTransformer t, VC vc) {
        double retval;

        try {
            if (t.introducesQuantifiedVariables()) {
                retval = -1;
            }
            else {
                Antecedent pattern = t.getPattern();
                Consequent template = t.getReplacementTemplate();

                Set<String> vcFunctions = vc.getConsequent().getSymbolNames();

                Set<String> ruleFunctions = pattern.getSymbolNames();
                ruleFunctions.addAll(template.getSymbolNames());

                int nonOverlaps = inAButNotB(ruleFunctions, vcFunctions);

                double findFunctionCount =
                        pattern.getFunctionApplications().size();
                double replaceFunctionCount =
                        template.getFunctionApplications().size();
                double simplificationRatio = (replaceFunctionCount + 1.0) /
                        (findFunctionCount + 1.0);

                double simplificationFactor =
                        Math.pow(0.9, simplificationRatio);

                retval = Math.min(
                        Math.pow(0.8, nonOverlaps) * simplificationFactor, 1.0);
            }
        }
        catch (UnsupportedOperationException e) {
            throw new RuntimeException(this.getClass() + " doesn't know how " +
                    "to rank a " + t.getClass());
        }

        return retval;
    }

    private static int inAButNotB(Set<String> a, Set<String> b) {
        int notThere = 0;

        for (String s : a) {
            if (!b.contains(s)) {
                notThere++;
            }
        }

        return notThere;
    }
}
