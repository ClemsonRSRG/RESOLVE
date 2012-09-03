package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>This <code>TransformationChooser</code> composes with an existing one to
 * alternate applications of the <code>Simplifier</code> transform with whatever
 * the existing chooser would normally recommend.  A minimum depth may be chosen
 * before which this chooser simply defers to the existing chooser.  The set of
 * suggested transformations will always contain at least one transformation and
 * will always end on a <code>Simplifier</code>.  The first 
 * <code>Simplifier</code> will be delivered at the minimum depth, thus setting
 * minimum depth to 0 will cause the first transformer to always be a 
 * <code>Simplifier</code>.</p>
 */
public class SimplifyingTransformationChooser implements TransformationChooser {

	private final static VCTransformer SIMPLIFY = new Simplifier();
	
	private final TransformationChooser mySourceChooser;
	private int myMinimumDepth;
	
	/**
	 * <p>Creates a new <code>SimplifyingTransformationChooser</code> that will
	 * suggest its first <Code>Simplifier</code> transformation at depth
	 * <code>minimumDepth</code> and then alternate applications of 
	 * <code>original</code> with simplifications after that.</p>
	 * 
	 * @param original The <code>TransformationChooser</code> to compose with.
	 * @param minimumDepth The first depth at which simplification should be
	 *                     suggested and after which simplification should be
	 *                     woven in.
	 */
	public SimplifyingTransformationChooser(TransformationChooser original,
			int minimumDepth) {
		
		mySourceChooser = original;
		myMinimumDepth = minimumDepth;
	}
	
	@Override
	public void preoptimizeForVC(VC vc) {
		mySourceChooser.preoptimizeForVC(vc);
	}

	@Override
	public Iterator<ProofPathSuggestion> suggestTransformations(VC vc, 
			int curLength, Metrics metrics, ProofData d) {
		
		Iterator<ProofPathSuggestion> retval;
		
		int distanceFromMin = curLength - myMinimumDepth;
		
		if (distanceFromMin < 0 || distanceFromMin % 2 == 1) {
			
			//This is the number of steps that have been contributed by our base
			//prover so far
			int childContributionCount;
			if (distanceFromMin < 0) {
				 childContributionCount = curLength;
			}
			else {
				childContributionCount =
					((curLength - myMinimumDepth + 1) / 2 + myMinimumDepth) - 1;
			}
			
			retval = mySourceChooser.suggestTransformations(vc, 
					childContributionCount, metrics, d);
		}
		else {
			retval = new SingletonIterator<ProofPathSuggestion>(
					new ProofPathSuggestion(SIMPLIFY, d));
		}
		
		return retval;
	}
	
	@Override
	public String toString() {
		return "Simplifying(" + mySourceChooser + ", starting at depth " + 
				myMinimumDepth + ")";
	}
}
