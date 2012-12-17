package edu.clemson.cs.r2jt.proving;

import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.FunctionExp;
import edu.clemson.cs.r2jt.absyn.InfixExp;
import edu.clemson.cs.r2jt.absyn.OutfixExp;
import edu.clemson.cs.r2jt.absyn.PrefixExp;

/**
 * <p>A <code>TransformerFitnessFunction</p> assigns a relevance ranking to
 * <code>VCTransformers</code> in light of a particular <code>VC</code>.</p>
 */
public abstract class TransformerFitnessFunction {

	/**
	 * <p>Utility function to count the total number of function applications in
	 * a list of <code>Exp</code>s.</p>
	 * 
	 * @param es A <code>List</code> of <code>Exp</code> in which to count the 
	 *           functions.
	 * @return The number of function applications.
	 */
	public static int functionCount(Iterable<Exp> es) {
		int count = 0;
		
		for (Exp e : es) {
			count += functionCount(e);
		}
		
		return count;
	}

	/**
	 * <p>Utility function to count the number of function applications in a
	 * given <code>Exp</code>.</p>
	 * 
	 * @param e An <code>Exp</code> in which to count the functions.
	 * @return The number of function applications.
	 */
	public static int functionCount(Exp e) {
		int retval = 0;
		
		List<Exp> subexpressions = e.getSubExpressions();
		for (Exp subexpression : subexpressions) {
			retval += functionCount(subexpression);
		}
		
		if (e instanceof FunctionExp || e instanceof InfixExp ||
				e instanceof OutfixExp || e instanceof PrefixExp) {
			
			retval += 1;
		}
		
		return retval;
	}

	/**
	 * <p>Returns a real value between -1 and 1, inclusive, indicating the
	 * relative likelihood a <code>t</code> would be useful to apply to 
	 * <code>vc</code> on the way to a proof.  In general, the magnitude of the
	 * returned values should not be considered meaningful--only the ordering 
	 * they impose on the various transformers.  However, fitness values less
	 * than 0 indicate that, in the opinion of the fitness function, the given
	 * transformation should not even be attempted.</p>
	 * 
	 * @param t The transformer whose fitness should be determined.
	 * @param vc The VC in the context of which <code>t</code>'s relevance
	 *           should be determined.
	 *           
	 * @return A double value between -1 and 1, inclusive, indicating this
	 *         fitness function's opinion on the relative likelihood that the
	 *         given transformation would be a useful one to apply.  Negative
	 *         values indicate that this fitness function's opinion is that it
	 *         is not even worth attempting the given transformer.
	 */
	public abstract double calculateFitness(VCTransformer t, VC vc);
	
	public final Iterable<VCTransformer> filter(
			Iterable<VCTransformer> transformers, VC vc, double threshhold) {
		
		List<VCTransformer> passedTransformers = 
			new LinkedList<VCTransformer>();
		
		for (VCTransformer t : transformers) {
			if (calculateFitness(t, vc) >= threshhold) {
				passedTransformers.add(t);
			}
		}
		
		return passedTransformers;
	}
}
