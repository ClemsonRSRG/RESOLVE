package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 * <p>A <code>ProductiveStepsOnlyStep</code> wraps an existing 
 * <code>VCTransformer</code> to return only those modified version of the
 * original VC that "significantly" change it.  Where significantly means that
 * the original and transformed VCs don't reduce to be equivalent after calls to
 * <code>simplify()</code>.</p>
 */
public class ProductiveStepsOnlyStep implements VCTransformer {

	private final VCTransformer myBaseTransformer;
	
	public ProductiveStepsOnlyStep(VCTransformer base) {
		myBaseTransformer = base;
	}
	
	@Override
	public Iterator<VC> transform(VC original) {
		
		GoodVCPredicate p = new GoodVCPredicate(original);
		
		return new PredicateIterator<VC>(
				myBaseTransformer.transform(original), p);
		
		//return new ProductiveIterator(myBaseTransformer.transform(original),
		//		original);
	}
	
    @Override
	public String toString() {
		return myBaseTransformer.toString();
	}

    @Override
    public Antecedent getPattern() {
        return myBaseTransformer.getPattern();
    }

    @Override
    public Consequent getReplacementTemplate() {
        return myBaseTransformer.getReplacementTemplate();
    }
	
	private class GoodVCPredicate implements Mapping<VC, Boolean> {

		private final VC myOriginalVC;
		
		public GoodVCPredicate(VC original) {
			myOriginalVC = original;
		}
		
		@Override
		public Boolean map(VC input) {
			return !input.simplify().equivalent(myOriginalVC);
		}
	}

	@Override
	public boolean introducesQuantifiedVariables() {
		return myBaseTransformer.introducesQuantifiedVariables();
	}
}
