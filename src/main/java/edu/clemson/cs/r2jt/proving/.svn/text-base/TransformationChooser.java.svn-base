package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>A <code>TransformationChooser</code> is the "brain" of the prover.  It
 * decides what step the prover should take next given information about the 
 * current state of the VC and the proof. It does this by returning an 
 * <code>Iterator</code> over a set of transformations in the order they should 
 * be attempted.</p>
 */
public interface TransformationChooser {

	/**
	 * <p>Called at the beginning of each proof attempt to give the
	 * <code>TransformationChooser</code> an opportunity to perform any per-VC
	 * optimizations on its data before proving of the VC begins.</p>
	 * 
	 * @param vc The VC to be proved.
	 */
	public void preoptimizeForVC(VC vc);
	
	/**
	 * <p>Returns a set of suggestions, in order of priority from highest to
	 * lowest, for the next transformation to apply to a VC to attempt to
	 * produce a proof.  The set of transformations may be empty if this
	 * <code>TransformerChooser</code> believes the prover should abandon the
	 * current proof-path, backtrack, and try another.</p>
	 * 
	 * @param vC The current VC to be transformed.
	 * @param curLength The number of steps in the current proof path that 
	 * 					represent contributions by this chooser.  (Steps from
	 *                  choosers that defer to this one are not included.)
	 * @param metrics A large collection of auxiliary data about the proof.
	 * @param pastStates A list of past VC states.
	 * 
	 * @return A non-null iterable set of <code>VCTransformer</code>s.
	 */
	public Iterator<ProofPathSuggestion> suggestTransformations(
			VC vc, int curLength, Metrics metrics, 
			ProofData d);
}
