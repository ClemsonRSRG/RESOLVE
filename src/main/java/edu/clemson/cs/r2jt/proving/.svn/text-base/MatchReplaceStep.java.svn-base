package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

public class MatchReplaceStep implements VCTransformer {

    private final NewMatchReplace myMatcher;
    private final MatchReplaceDevelopmentStep myAntecedentExtender;
    private final ConsequentSubstitutor myConsequentSubstitutor;
    private final Antecedent myTheoremAntecedent;
    private final Consequent myTheoremConsequent;
    
    private final boolean myIntroducesQuantifiedVariablesFlag;

    public MatchReplaceStep(NewMatchReplace r) {
        myMatcher = r;
        myAntecedentExtender = new MatchReplaceDevelopmentStep(r);
        myConsequentSubstitutor = new ConsequentSubstitutor(r);

        myTheoremAntecedent = new Antecedent(r.getPattern());
        myTheoremConsequent = new Consequent(r.getExpansionTemplate());
        
        myIntroducesQuantifiedVariablesFlag = 
        	myTheoremConsequent.containsQuantifiedVariableNotIn(
        			myTheoremAntecedent);
    }

    @Override
    public Iterator<VC> transform(VC original) {
        return new ChainingIterator<VC>(
                myConsequentSubstitutor.transform(original),
                myAntecedentExtender.transform(original));
    }

    @Override
    public String toString() {
        return myMatcher.toString();
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
