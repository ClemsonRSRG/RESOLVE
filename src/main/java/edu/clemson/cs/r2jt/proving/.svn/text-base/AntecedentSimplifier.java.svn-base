package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

public class AntecedentSimplifier implements AntecedentTransformer {

	private final AntecedentTransformer myBaseTransformer;
	
	public AntecedentSimplifier(AntecedentTransformer t) {
		myBaseTransformer = t;
	}
	
	@Override
	public Iterator<Antecedent> transform(Antecedent source) {
		return new SimplifyingIterator(myBaseTransformer.transform(source));
	}
	
	public class SimplifyingIterator implements Iterator<Antecedent> {

		private final Iterator<Antecedent> myBaseIterator;
		
		public SimplifyingIterator(Iterator<Antecedent> i) {
			myBaseIterator = i;
		}
		
		@Override
		public boolean hasNext() {
			return myBaseIterator.hasNext();
		}

		@Override
		public Antecedent next() {
			return myBaseIterator.next().eliminateObviousConjuncts()
					.eliminateRedundantConjuncts();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}

}
