package edu.clemson.cs.r2jt.proving;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>A <code>ConjunctGranularityBindingIterator</code> takes a pattern set of
 * universally quantified conjuncts and a target set of conjuncts, and attempts 
 * to bind each conjunct in the pattern against some conjunct in the target, 
 * iterating over the resulting variable bindings and the set of target 
 * conjuncts that were not matched against.</p>
 */
public class ConjunctGranularityBindingIterator 
		implements Iterator<
			ConjunctGranularityBindingIterator.BindingsAndRemainingConjuncts> {

	private static final Map<PExp, PExp> EMPTY_BINDINGS = 
		new HashMap<PExp, PExp>();
	
	private final PExp myLocalPattern;
	private final ImmutableConjuncts myRemainingPattern;
	
	private final ImmutableConjuncts myTarget;
	private final int myTargetSize;
	
	private int myLocalTargetConjunctIndex;
	private Map<PExp, PExp> myLocalBindings;
	
	private Iterator<BindingsAndRemainingConjuncts> myOtherBindings;
	
	private BindingsAndRemainingConjuncts myNextReturn;
	
	public ConjunctGranularityBindingIterator(ImmutableConjuncts pattern,
			ImmutableConjuncts target) {
		
		myTarget = target;
		myTargetSize = myTarget.size();
		myLocalTargetConjunctIndex = 0;
		
		if (pattern.size() > 0) {
			myLocalPattern = pattern.get(0);
			myRemainingPattern = pattern.removed(0);
			myOtherBindings = DummyIterator.getInstance(myOtherBindings);
		}
		else {
			myLocalPattern = null;
			myRemainingPattern = pattern;
			myLocalBindings = new HashMap<PExp, PExp>();  //TODO: Replace with ready made component after we figure out who's changing this one
			myOtherBindings = 
				new SingletonIterator<BindingsAndRemainingConjuncts>(
						new BindingsAndRemainingConjuncts(
								new HashMap<PExp, PExp>(), myTarget));  //TODO : Same as above
		}
		
		setUpNext();
	}
	
	private void setUpNext() {
		
		PExp curLocalTargetConjunct;
		while (!myOtherBindings.hasNext() && myLocalPattern != null &&
				myLocalTargetConjunctIndex < myTargetSize) {
			
			curLocalTargetConjunct = myTarget.get(myLocalTargetConjunctIndex);
			
			try {
				myLocalBindings = 
						myLocalPattern.bindTo(curLocalTargetConjunct);
						
				myOtherBindings = new ConjunctGranularityBindingIterator(
						myRemainingPattern, myTarget.removed(
								myLocalTargetConjunctIndex).substitute(
										myLocalBindings));
			}
			catch(BindingException e) {
				myOtherBindings = DummyIterator.getInstance(myOtherBindings);
			}
			
			myLocalTargetConjunctIndex++;
		}
		
		if (myOtherBindings.hasNext()) {
			BindingsAndRemainingConjuncts otherBindings = 
				myOtherBindings.next();
			
			otherBindings.bindings.putAll(myLocalBindings);
			
			myNextReturn = otherBindings;
		}
		else {
			myNextReturn = null;
		}
	}

	@Override
	public boolean hasNext() {
		return myNextReturn != null;
	}

	@Override
	public BindingsAndRemainingConjuncts next() {
		BindingsAndRemainingConjuncts retval = myNextReturn;
		
		setUpNext();
		
		return retval;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public static class BindingsAndRemainingConjuncts {
		public final Map<PExp, PExp> bindings;
		public final ImmutableConjuncts remainingConjuncts;
		
		private BindingsAndRemainingConjuncts(Map<PExp, PExp> bindings,
				ImmutableConjuncts remainingConjuncts) {
			this.bindings = bindings;
			this.remainingConjuncts = remainingConjuncts;
		}
	}
}
