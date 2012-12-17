package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

public class MatchReplaceDevelopmentStep implements VCTransformer {

    private final AntecedentTransformer myTransformer;
    private final Antecedent myTheoremAntecedent;
    private final Consequent myTheoremConsequent;
    
    private final boolean myIntroducesQuantifiedVariablesFlag;

    public MatchReplaceDevelopmentStep(NewMatchReplace m) {
        myTransformer = new AntecedentTransformerAdapter(
                new ApplicatorConjunctsTransformer(
                new ExtendingApplicatorFactory(m)));

        myTheoremAntecedent = new Antecedent(m.getPattern());
        myTheoremConsequent = new Consequent(m.getExpansionTemplate());
        
        myIntroducesQuantifiedVariablesFlag = 
        	myTheoremConsequent.containsQuantifiedVariableNotIn(
        			myTheoremAntecedent);
    }

    @Override
    public Iterator<VC> transform(VC original) {
        return new StaticConsequentIterator(original.getSourceName(),
                myTransformer.transform(original.getAntecedent()),
                original.getConsequent());
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
