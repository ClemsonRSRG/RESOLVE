package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;
import java.util.Set;

import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 * <p>A <code>NewTermsOnlyStep</code> wraps an existing 
 * <code>VCTransformer</code> to return only those modified versions of the
 * original VC that introduce new function or variable names.  For example,
 * taking <code>|empty_string|</code> and transforming it into <code>0</code>
 * introduces a new term (0) that did not exist in the original.  However,
 * taking <code>x + 0</code> and transforming it into <code>x + 0 + 0</code>
 * introduces nothing new and so is discarded.</p>
 */
public class NewTermsOnlyDeveloper implements AntecedentDeveloper {
	
	private final AntecedentDeveloper myBaseDeveloper;
	
	public NewTermsOnlyDeveloper(AntecedentDeveloper base) {
		myBaseDeveloper = base;
	}
	
	@Override
	public Iterator<Antecedent> transform(Antecedent source) {

		GoodVCPredicate p = new GoodVCPredicate(source);
		
		return new PredicateIterator<Antecedent>(
				myBaseDeveloper.transform(source), p);
	}
	
	/**
	 * <p>A predicate that selects only those VCs that introduce new terms.</p>
	 */
	private class GoodVCPredicate implements Mapping<Antecedent, Boolean> {

		private Set<String> myOriginalVCSymbols;
		private int myOriginalApplicationCount;
		
		public GoodVCPredicate(Antecedent original) {
			myOriginalVCSymbols = original.getSymbolNames();
			myOriginalApplicationCount = 
				original.getFunctionApplications().size();
		}
		
		@Override
		public Boolean map(Antecedent input) {
			
			boolean retval = false; /*(input.getFunctionApplications().size() <= 
				myOriginalApplicationCount);*/
			
			if (!retval) {
				Set<String> inputSymbols = input.getSymbolNames();
				
				inputSymbols.removeAll(myOriginalVCSymbols);
				
				retval = (inputSymbols.size() != 0);
			}
			
			return retval;
		}
	}


}
