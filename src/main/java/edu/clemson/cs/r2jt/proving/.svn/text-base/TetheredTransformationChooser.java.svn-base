package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>A <code>TetheredTransformationChooser</code> composes with an existing
 * chooser to limit the length of proofs by suggesting the prover backtrack once
 * a given proof depth is reached.</p>
 */
public class TetheredTransformationChooser implements TransformationChooser {

	private static final Iterator<ProofPathSuggestion> 
			TYPE_SAFE_ITERATOR = null;
	
	private final TransformationChooser mySourceChooser;
	private final int myMaxDepth;
	
	public TetheredTransformationChooser(TransformationChooser sourceChooser,
			int maxDepth) {
		
		mySourceChooser = sourceChooser;
		myMaxDepth = maxDepth;
	}
	
	@Override
	public void preoptimizeForVC(VC vc) {
		mySourceChooser.preoptimizeForVC(vc);
	}

	@Override
	public Iterator<ProofPathSuggestion> suggestTransformations(VC vc, 
			int curLength, Metrics metrics, ProofData d) {
		
		Iterator<ProofPathSuggestion> retval;
		
		if (curLength >= myMaxDepth) {
			retval = DummyIterator.getInstance(TYPE_SAFE_ITERATOR);
		}
		else {
			retval = mySourceChooser.suggestTransformations(vc, curLength, 
					metrics, d);
		}
		
		return retval;
	}

	@Override
	public String toString() {
		return "Tethered(" + mySourceChooser + ", tethered to " + myMaxDepth + 
				" steps.)";
	}
}
