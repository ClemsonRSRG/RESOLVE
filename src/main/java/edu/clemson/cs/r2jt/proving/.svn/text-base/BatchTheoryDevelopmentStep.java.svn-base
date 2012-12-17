package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>A <code>BatchTheoryDevelopmentStep</code> extends the antecedents of given
 * VCs by repeatedly applying a set of implication theorems in a finite number
 * of rounds, where each round is a complete pass through all the theorems.</p>
 */
public class BatchTheoryDevelopmentStep implements VCTransformer {

    private final Iterable<PExp> myGlobalTheorems;
    private final int myIterationCount;
    private DevelopmentAlternativesTransformer myExtenders =
            new DevelopmentAlternativesTransformer();

    public BatchTheoryDevelopmentStep(Iterable<PExp> globalTheorems,
            int iterationCount) {
        myGlobalTheorems = globalTheorems;
        myIterationCount = iterationCount;
    }

    public void addImplicationTheorem(Antecedent a, Consequent c) {
        ConditionalAntecedentExtender e =
                new ConditionalAntecedentExtender(a, c, myGlobalTheorems);

        addExtender(e);
    }

    public void addExtender(ConditionalAntecedentExtender e) {
        myExtenders.addAlternative(new NewTermsOnlyDeveloper(e));
    }

    @Override
    public Iterator<VC> transform(VC vc) {

        AccumulatingAntecedentExtender accumulator =
                new AccumulatingAntecedentExtender(myExtenders);

        RepeatedApplicationTransformer<Antecedent> repeater =
                new RepeatedApplicationTransformer<Antecedent>(
                new AntecedentSimplifier(
                new DevelopmentAppender(accumulator)),
                myIterationCount);

        return new StaticConsequentIterator(vc.getSourceName(),
                repeater.transform(vc.getAntecedent()), vc.getConsequent());
    }

    @Override
    public String toString() {
        return "general theory development step";
    }

    @Override
    public Antecedent getPattern() {
        throw new UnsupportedOperationException("Not applicable.");
    }

    @Override
    public Consequent getReplacementTemplate() {
        throw new UnsupportedOperationException("Not applicable.");
    }

	@Override
	public boolean introducesQuantifiedVariables() {
		return true;
	}
}
