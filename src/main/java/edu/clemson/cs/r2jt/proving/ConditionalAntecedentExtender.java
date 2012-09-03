package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;
import java.util.Map;

import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>A <code>ConditionalAntecedentExtender</code> attempts to develop given
 * <code>Antecedent</code>s by applying an implication theorem to them.  Calling
 * <code>transform()</code> returns an iterator over versions of the consequents
 * of the theorem bound in all possible ways to match bindings of the antecedent
 * of the theorem to the set of global facts and antecedent of the the VC. That
 * is, if there are six different ways to bind the antecedent of the theorem to
 * the set of global facts and the set of antecedent conjuncts of the VC, then
 * the returned iterator will iterate over six different versions of the
 * consequent of the theorem, each reflecting a different one of the bindings.
 * </p>
 * 
 * <p><strong>Random Quirk:</strong> Because the intention of this class is to
 * extend available assumptions based on local contextual data, at least one
 * variable binding when matching the theorem antecedent against known facts
 * must come from the VC's antecedent.  That is, the VC will not be extended
 * with applications of this conditional theorem entirely to global facts--those
 * "extensions" should themselves be listed as global theorems.</p>
 */
public class ConditionalAntecedentExtender implements AntecedentDeveloper {
	
	private final Antecedent myTheoremAntecedent;
	private final int myTheoremAntecedentSize;
	private final Consequent myTheoremConsequent;
	private final Iterable<PExp> myTheorems;
	
	public ConditionalAntecedentExtender(Antecedent theoremAntecedent, 
			Consequent theoremConsequent, Iterable<PExp> globalTheorems) {
		
		myTheorems = globalTheorems;
		myTheoremAntecedent = theoremAntecedent;
		myTheoremAntecedentSize = myTheoremAntecedent.size();
		
		myTheoremConsequent = theoremConsequent;
	}
	
	@Override
	public Iterator<Antecedent> transform(Antecedent original) {
		return new ExtendedAntecedentsIterator(original);
	}
	
	public String toString() {
		return (myTheoremAntecedent + " ==> " + 
				myTheoremConsequent).replace('\n', ' ');
	}
	
	/**
	 * <p>An <code>ExtendedAntecedentsIterator</code> iterates over variations
	 * of the application of the conditional theorem embedded in this
	 * <code>ConditionalAntecedentExtender</code> to a given VC antecedent,
	 * given a set of global facts.</p>
	 * 
	 * <p>See the note on the random quirk in the parent class comments.</p>
	 */
	private class ExtendedAntecedentsIterator implements Iterator<Antecedent> {
		
		private final Antecedent myVCAntecedent;
		
		private int myLocalConditionIndex;
		
		private Iterator<Antecedent> myLocalConditionApplications;
		private Antecedent myNextAntecedent;
		
		public ExtendedAntecedentsIterator(Antecedent vcAntecedent) {
			
			myVCAntecedent = vcAntecedent;
			myLocalConditionIndex = 0;
			
			myLocalConditionApplications = 
				DummyIterator.getInstance(myLocalConditionApplications); 
			
			setUpNext();
		}

		private void setUpNext() {
			while (!myLocalConditionApplications.hasNext() &&
					myLocalConditionIndex < myTheoremAntecedentSize) {
				
				myLocalConditionApplications = new QuirkyBindingIterator(
						myTheoremAntecedent.get(myLocalConditionIndex),
						myTheoremAntecedent.removed(myLocalConditionIndex),
						myVCAntecedent);
				myLocalConditionIndex++;
			}
			
			if (myLocalConditionApplications.hasNext()) {
				myNextAntecedent = myLocalConditionApplications.next();				
			}
			else {
				myNextAntecedent = null;
			}
		}
		
		@Override
		public boolean hasNext() {
			return myNextAntecedent != null;
		}

		@Override
		public Antecedent next() {
			Antecedent retval = myNextAntecedent;
			
			setUpNext();
			
			return retval.eliminateRedundantConjuncts();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * <p>A <code>QuirkyBindingIterator</code> enforces the random quirk listed
	 * in <code>ConditionalAntecedentExtender</code>'s class comments.  It
	 * accepts a pattern expression to match against conjuncts in the VC 
	 * antecedent and then a pattern antecedent to match against conjuncts in
	 * the VC antecedent or global theorems, then iterates over all possible
	 * bindings.</p>
	 */
	private class QuirkyBindingIterator implements Iterator<Antecedent> {

		private final Antecedent myVCAntecedent;
		private final ChainingIterable<PExp> myFacts;
		
		private final IncrementalBindingIterator myFirstBinding;
		private Iterator<Map<PExp, PExp>> myOtherBindings;
		
		private final Antecedent myOtherPatterns;
		
		private Antecedent myNextAntecedent;
		
		public QuirkyBindingIterator(PExp firstPattern, Antecedent otherPatterns,
				Antecedent vcAntecedent) {
			
			myOtherPatterns = otherPatterns;
			myVCAntecedent = vcAntecedent;
			
			myFacts = new ChainingIterable<PExp>();
			myFacts.add(vcAntecedent);
			myFacts.add(myTheorems);
			
			myFirstBinding = new IncrementalBindingIterator(firstPattern,
					myVCAntecedent);
			
			myOtherBindings = DummyIterator.getInstance(myOtherBindings);
			
			setUpNext();
		}
		
		private void setUpNext() {
			
			Map<PExp, PExp> firstBindings;
			while (!myOtherBindings.hasNext() && myFirstBinding.hasNext()) {
				firstBindings = myFirstBinding.next();
				myOtherBindings = new TotalBindingIterator(myOtherPatterns, 
						myFacts, firstBindings);
			}
			
			if (myOtherBindings.hasNext()) {
				Map<PExp, PExp> bindings = myOtherBindings.next();
				myNextAntecedent = 
					myTheoremConsequent.substitute(bindings).assumed();
			}
			else {
				myNextAntecedent = null;
			}
		}
		
		@Override
		public boolean hasNext() {
			return myNextAntecedent != null;
		}

		@Override
		public Antecedent next() {
			Antecedent retval = myNextAntecedent;
			
			setUpNext();
			
			return retval;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
