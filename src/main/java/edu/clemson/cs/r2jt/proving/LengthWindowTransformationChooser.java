package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>A <code>LengthWindowTransformationChooser</code> is a convenience class
 * to wrap both a <code>SimplifyingTransformationChooser</code> and a
 * <code>TetheredTransformationChooser</code> to define an "interesting window"
 * over the depths of the generated proofs--before which simplifications are
 * not attempted and after which proofs are tethered.</p>
 */
public class LengthWindowTransformationChooser 
		implements TransformationChooser {

	private final TransformationChooser mySourceChooser;
	
	/**
	 * <p>Creates a new <code>LengthWindowTransformationChooser</code> that will
	 * begin interleaving simplification steps at depth <code>minDepth</code>
	 * and consider proof-paths of up to length <code>maxDepth</code>, 
	 * calculated from the perspective of <code>source</code>, i.e., not 
	 * counting the interleaved simplification steps.</p>
	 * 
	 * @param source The source chooser to defer to.
	 * @param minDepth The depth at which simplification steps should begin to
	 *                 be interleaved.
	 * @param maxDepth The depth, from the perspective of <code>source</code>
	 *                 at which the proof should begin to backtrack.
	 */
	public LengthWindowTransformationChooser(TransformationChooser source,
			int minDepth, int maxDepth) {
		
		mySourceChooser = new SimplifyingTransformationChooser(
				new TetheredTransformationChooser(source, maxDepth), minDepth);
		/*
		mySourceChooser = new TetheredTransformationChooser(
				new SimplifyingTransformationChooser(source, minDepth), 
				SimplifyingTransformationChooser.getTrueDepth(
						maxDepth, minDepth));*/
	}

	@Override
	public void preoptimizeForVC(VC vc) {
		mySourceChooser.preoptimizeForVC(vc);
	}

	@Override
	public Iterator<ProofPathSuggestion> suggestTransformations(VC vc, 
			int curLength, Metrics metrics, ProofData d) {
		
		return mySourceChooser.suggestTransformations(vc, curLength, metrics, 
				d);
	}
	
	@Override
	public String toString() {
		return "" + mySourceChooser;
	}
}
