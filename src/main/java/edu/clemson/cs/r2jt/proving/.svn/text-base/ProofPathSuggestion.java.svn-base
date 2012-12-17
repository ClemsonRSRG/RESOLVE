package edu.clemson.cs.r2jt.proving;

/**
 * <p>A <code>ProofPathSuggestion</code> is the immutable representation of a 
 * single suggestion of a <code>TransformationChooser</code> regarding what step
 * a proof should follow next.  This amounts to the suggestion of a 
 * <code>VCTransformer</code> and the new <code>ProofData</code> that should 
 * accompany the proof should this step be chosen.</p>
 */
public class ProofPathSuggestion {

	public final VCTransformer step;
	public final ProofData data;

	/**
	 * <p>A note to be added to the proof file before continuing with 
	 * information about this step.  This allows you to place a note in the 
	 * proof file about when a particular TransformationChooser took over, for 
	 * example.  If this value is <code>null</code>, then no message will be 
	 * printed.</p>
	 */
	public final String pathNote;
	
	/**
	 * <p>A note to be printed to the console along with the state of the VC
	 * when this suggestion is tried.  Depending on the value of 
	 * <code>debugPrevious</code>, either the VC before or after following this
	 * suggestion will be printed.  This printing occurs when the step is tried
	 * and will thus be printed even if this step is not included in any
	 * successful proof.  Care must be taken to avoid printing debug messages
	 * from suggestions that will crop up many, many times.</p>
	 * 
	 * <p>These debugging messages will only be printed if 
	 * <code>Prover.FLAG_VERBOSE</code> is on.  A <code>null</code> message
	 * indicates that no message should be printed.</p>
	 */
	public final String debugNote;
	
	/**
	 * <p>Indicates whether the message in <code>debugNote</code> should be
	 * printed before (<code>false</code>) or after (<code>true</code>) the
	 * VC transformation suggested by this step.  That is, should the VC be
	 * printed as it was before the application of this step or as it is
	 * after.  If <code>debugNote</code> is <code>null</code> this value is
	 * ignored.</p>
	 */
	public final boolean debugPrevious;

	public ProofPathSuggestion(VCTransformer step, ProofData data) {
		this(step, data, null, null, false);
	}

	public ProofPathSuggestion(VCTransformer step, ProofData data, 
			String pathNote, String debugNote) {
		this(step, data, null, null, false);
	}
	
	public ProofPathSuggestion(VCTransformer step, ProofData data, 
			String pathNote, String debugNote, boolean debugPrevious) {
		this.step = step;
		this.data = data;
		this.pathNote = pathNote;
		this.debugNote = debugNote;
		this.debugPrevious = debugPrevious;
	}

	public String toString() {
		return step.toString();
	}
}
