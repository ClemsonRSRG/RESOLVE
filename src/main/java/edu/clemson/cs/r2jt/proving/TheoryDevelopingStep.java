package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>A <code>TheoryDevelopingStep</code> uses an implication theorem to expand
 * the set of known facts in the VC's antecedent, by applying all possible
 * bindings of the theorem's antecedent against the VC's antecedent and globally
 * known facts, then extending the VC antecedent with the consequent of the 
 * theorem under each of those bindings.</p>
 * 
 * <p>Note that the random quirk mentioned in 
 * <code>ConditionalAntecedentExtender</code>'s class comments applies here as
 * well.</p>
 */
public class TheoryDevelopingStep implements VCTransformer {

    private final AntecedentDeveloper myDerivedTransformer;
    private final Antecedent myAntecedent;
    private final Consequent myConsequent;
    
    private final boolean myIntroducesQuantifiedVariablesFlag;

    public TheoryDevelopingStep(Antecedent theoremAntecedent,
            Consequent theoremConsequent, Iterable<PExp> globalFacts) {

        myDerivedTransformer = new AccumulatingAntecedentExtender(
                new ConditionalAntecedentExtender(theoremAntecedent,
                theoremConsequent, globalFacts));

        myAntecedent = theoremAntecedent;
        myConsequent = theoremConsequent;
        
        myIntroducesQuantifiedVariablesFlag = 
        	myConsequent.containsQuantifiedVariableNotIn(myAntecedent);
    }

    @Override
    public Iterator<VC> transform(VC original) {

        Antecedent originalAntecedent = original.getAntecedent();

        return new StaticConsequentIterator(original.getSourceName(),
                new AntecedentDevelopmentIterator(originalAntecedent,
                myDerivedTransformer.transform(originalAntecedent)),
                original.getConsequent());
    }

    @Override
    public String toString() {
        return "Develop antecedent with " + myDerivedTransformer;
    }

    @Override
    public Antecedent getPattern() {
        return myAntecedent;
    }

    @Override
    public Consequent getReplacementTemplate() {
        return myConsequent;
    }

	@Override
	public boolean introducesQuantifiedVariables() {
		return myIntroducesQuantifiedVariablesFlag;
	}
}
