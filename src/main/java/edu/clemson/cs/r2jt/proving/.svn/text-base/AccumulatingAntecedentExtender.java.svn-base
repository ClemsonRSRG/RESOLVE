package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>An <code>AccumulatingAntecedentExtender</code> modifies the functionality
 * of an existing <code>AntecedentDeveloper</code> by accumulating all of the
 * existing extender's additional antecedent conjuncts into a single 
 * transformation.  So, if the original antecedent contained conjuncts (A, B, C)
 * and the existing extender would suggest new antecedents ((D), (E), (F)), 
 * the result of a call to an <code>AccumulatingAntecedentExtender</code>'s 
 * <code>transform()</code> method will return ((D, E, F)).</p>
 */
public class AccumulatingAntecedentExtender implements AntecedentDeveloper {

	private final Transformer<Antecedent, Iterator<Antecedent>> 
			mySubTransformer;
	
	public AccumulatingAntecedentExtender(AntecedentDeveloper t) {
		mySubTransformer = t;
	}
	
	@Override
	public Iterator<Antecedent> transform(Antecedent original) {
		Iterator<Antecedent> singleBindingExtensions =
			mySubTransformer.transform(original);
		
		Antecedent singleBindingExtension;
		Antecedent workingAntecedent = Antecedent.EMPTY;
		while (singleBindingExtensions.hasNext()) {
			
			singleBindingExtension = singleBindingExtensions.next();
			
			workingAntecedent = 
				workingAntecedent.appended(singleBindingExtension);
		}
		
		Iterator<Antecedent> retval;
		
		if (workingAntecedent == Antecedent.EMPTY) {
			retval = DummyIterator.getInstance((Iterator<Antecedent>) null);
		}
		else {
			retval = new SingletonIterator<Antecedent>(workingAntecedent); 
		}
		
		return retval;
	}
	
	public String toString() {
		return mySubTransformer.toString();
	}
}
