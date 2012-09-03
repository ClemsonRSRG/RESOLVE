package edu.clemson.cs.r2jt.proving;

/**
 * <p>A <code>NormalizingTransformerFitnessFunction</p> ranks a set of 
 * transforms based on their affect on the number of function applications in a 
 * <code>VC</code>.  Transformers that reduce this number more are given higher 
 * precedence.  Transformers that do not reduce the number (i.e., keep it the 
 * same or increase it) are recommended against (i.e., they will be given a 
 * negative weight.)</p>
 */
public class NormalizingTransformerFitnessFunction
        extends TransformerFitnessFunction {

    @Override
    public String toString() {
        return "Reduction Fitness";
    }

    @Override
    public double calculateFitness(VCTransformer t, VC vc) {

        double retval;

        if (t instanceof MatchReplaceStep) {
            Antecedent pattern = t.getPattern();
            Consequent template = t.getReplacementTemplate();

            if (template.containsQuantifiedVariableNotIn(pattern)) {
                retval = -1;
            }
            else {

                double findFunctionCount = 
                        pattern.getFunctionApplications().size();
                double replaceFunctionCount = template.getFunctionApplications()
                        .size();
                double difference = findFunctionCount - replaceFunctionCount;

                double simplificationFactor = Math.pow(0.5, difference);

                //Note at this point that 0 < simplificationFactor <= .5  if
                //the rule results in the reduction of at least one function
                //application.  And 1 <= simplificationFactor otherwise.
                retval = .9 - simplificationFactor;
            }
        }
        else {
            retval = -1;
        }

        return retval;
    }
}
