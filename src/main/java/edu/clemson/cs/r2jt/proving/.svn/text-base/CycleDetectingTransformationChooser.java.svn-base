package edu.clemson.cs.r2jt.proving;

import java.math.BigInteger;
import java.util.Iterator;

/**
 * <p>A <code>CycleDetectingTransformationChooser</code> composes with an 
 * existing chooser to detect and backtrack on cycles, otherwise simply 
 * deferring to the existing chooser.</p>
 */
public class CycleDetectingTransformationChooser 
		implements TransformationChooser {
	
	private static final 
			Iterator<ProofPathSuggestion> TYPE_SAFE_ITERATOR = null;
	
	private final TransformationChooser mySourceChooser;
	
	public CycleDetectingTransformationChooser(TransformationChooser source) {
		mySourceChooser = source;
	}
	
	@Override
	public void preoptimizeForVC(VC vc) {
		mySourceChooser.preoptimizeForVC(vc);
	}

	@Override
	public Iterator<ProofPathSuggestion> suggestTransformations(VC vc, 
			int curLength, Metrics metrics, ProofData proofData) {
		
		Iterator<ProofPathSuggestion> retval;
		
		boolean cycle = false;
		Iterator<VC> pastStatesIterator = proofData.stepIterator();
		while (!cycle && pastStatesIterator.hasNext()) {
			cycle = vc.equivalent(pastStatesIterator.next());
		}
		
		if (cycle) {
			retval = DummyIterator.getInstance(TYPE_SAFE_ITERATOR);
			metrics.numTimesBacktracked = 
				metrics.numTimesBacktracked.add(BigInteger.ONE);
		}
		else {
			retval = mySourceChooser.suggestTransformations(vc, curLength, 
					metrics, proofData);
		}
		
		return retval;
	}
}
