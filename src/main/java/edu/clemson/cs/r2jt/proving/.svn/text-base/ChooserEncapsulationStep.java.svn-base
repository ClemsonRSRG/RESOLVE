package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

public class ChooserEncapsulationStep implements VCTransformer {

	private final String myName;
	private final NoBacktrackChooser myChooser;
	
	public ChooserEncapsulationStep(String name, TransformationChooser c) {
		myChooser = new NoBacktrackChooser(c);
		myName = name;
	}
	
	public ChooserEncapsulationStep(String name, NoBacktrackChooser c) {
		myChooser = c;
		myName = name;
	}
	
	@Override
	public Iterator<VC> transform(VC original) {
		
		Metrics m = new Metrics();
		ProofData d = new ProofData();
		
		myChooser.preoptimizeForVC(original);
		
		Iterator<ProofPathSuggestion> transformations = 
			myChooser.suggestTransformations(original, 0, m, d);
		
		int length = 0;
		VC curVC = original;
		VC newVC = null;
		ProofPathSuggestion next;
		Iterator<VC> suggestions;
		boolean foundNext;
		while (transformations.hasNext()) {
			next = transformations.next();
		
			suggestions = next.step.transform(curVC);
			
			foundNext = false;
			while (suggestions.hasNext() && !foundNext) {
				newVC = suggestions.next();
				
				foundNext = (!newVC.equivalent(curVC));
			}
			
			if (foundNext) {
				curVC = newVC;				
				length++;
				
				transformations = 
					myChooser.suggestTransformations(curVC, length, m, 
							next.data);
			}
		}
		
		return new SingletonIterator<VC>(curVC);
	}

	public String toString() {
		return myName;
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
