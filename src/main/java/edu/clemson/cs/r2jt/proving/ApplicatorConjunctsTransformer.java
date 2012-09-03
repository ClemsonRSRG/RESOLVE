package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>An <code>ApplicatorConjunctsTransformer</code> wraps the behavior of a
 * <code>ReplacementApplicator</code> into a <code>ConjunctsTransformer</code>.
 * </p>
 */
public class ApplicatorConjunctsTransformer implements ConjunctsTransformer {

	private final ReplacementApplicatorFactory myFactory;
	
	public ApplicatorConjunctsTransformer(ReplacementApplicatorFactory f) {
		myFactory = f;
	}

	@Override
	public Iterator<ImmutableConjuncts> transform(ImmutableConjuncts original) {
		return new ApplicatorIterator(myFactory.newApplicatorOver(original));
	}
	
	public String toString() {
		return myFactory.toString();
	}
	
	private class ApplicatorIterator implements Iterator<ImmutableConjuncts> {

		private final ReplacementApplicator myApplicator;
		private ImmutableConjuncts myNextConjuncts;
		
		public ApplicatorIterator(ReplacementApplicator a) {
			myApplicator = a;
			myNextConjuncts = a.getNextApplication();
		}
		
		@Override
		public boolean hasNext() {
			return myNextConjuncts != null;
		}

		@Override
		public ImmutableConjuncts next() {
			ImmutableConjuncts retval = myNextConjuncts;
			
			myNextConjuncts = myApplicator.getNextApplication();
			
			return retval;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
