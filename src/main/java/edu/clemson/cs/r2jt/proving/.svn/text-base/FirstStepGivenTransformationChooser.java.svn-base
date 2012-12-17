package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

import edu.clemson.cs.r2jt.init.CompileEnvironment;

/**
 * <p>A <code>FirstStepGivenTransformationChooser</code> composes with
 * an existing <code>TransformationChooser</code> to insert a particular
 * <code>VCTransformer</code> as the first step in any proof.</p>
 */
public class FirstStepGivenTransformationChooser implements
		TransformationChooser {

	private final TransformationChooser myBaseChooser;
	private final VCTransformer myFirstStep;
	
	/**
	 * <p>Creates a new <code>FirstStepDevelopmentTransformationChooser</code>
	 * that inserts the given number of rounds of theory development before
	 * deferring to <code>baseChooser</code>.</p>
	 * 
	 * @param baseChooser The existing chooser to defer to.
	 * @param rounds The number of rounds of theory development.
	 */
	public FirstStepGivenTransformationChooser(
			TransformationChooser baseChooser, VCTransformer firstStep) {
		
		myFirstStep = firstStep;
		myBaseChooser = baseChooser;
	}
	
	@Override
	public void preoptimizeForVC(VC vc) {
		myBaseChooser.preoptimizeForVC(vc);
	}

	@Override
	public Iterator<ProofPathSuggestion> suggestTransformations(VC vc, 
			int curLength, Metrics metrics, ProofData d) {
		
		Iterator<ProofPathSuggestion> retval;
		
		if (curLength == 0) {
			retval = new SingletonIterator<ProofPathSuggestion>(
					new ProofPathSuggestion(myFirstStep, d, null,
							"After first step (" + myFirstStep + "), VC is: "));
		}
		else {
			retval = myBaseChooser.suggestTransformations(vc, curLength - 1, 
					metrics, d);
		}
		
		return retval;
	}

	@Override
	public String toString() {
		return "FirstStepGiven(" + myFirstStep + ", then continue with " + 
				myBaseChooser + ")";
	}
}
