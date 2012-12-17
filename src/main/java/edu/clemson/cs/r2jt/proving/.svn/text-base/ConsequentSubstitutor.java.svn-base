package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

public class ConsequentSubstitutor implements VCTransformer {

    private final ConsequentTransformerAdapter myTransformer;

    private final Antecedent myTheoremAntecedent;
    private final Consequent myTheoremConsequent;

    private final boolean myIntroducesQuantifiedVariablesFlag;
    
    public ConsequentSubstitutor(NewMatchReplace m) {

        myTheoremAntecedent = new Antecedent(m.getPattern());
        myTheoremConsequent = new Consequent(m.getExpansionTemplate());
        
        myIntroducesQuantifiedVariablesFlag = 
        	myTheoremConsequent.containsQuantifiedVariableNotIn(
        			myTheoremAntecedent);

        myTransformer = new ConsequentTransformerAdapter(
                new ApplicatorConjunctsTransformer(
                    new InPlaceApplicatorFactory(m)));
    }

    @Override
    public Iterator<VC> transform(VC original) {
        return new StaticAntecedentIterator(original.getSourceName(),
                original.getAntecedent(),
                myTransformer.transform(original.getConsequent()));
    }

    @Override
    public String toString() {
        return myTransformer.toString();
    }

    @Override
    public Antecedent getPattern() {
        return myTheoremAntecedent;
    }

    @Override
    public Consequent getReplacementTemplate() {
        return myTheoremConsequent;
    }

	@Override
	public boolean introducesQuantifiedVariables() {
		return myIntroducesQuantifiedVariablesFlag;
	}
}
