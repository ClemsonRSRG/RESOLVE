package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>A <code>DevelopmentAppender</code> adapts an
 * <code>AntecedentDeveloper</code> so that, rather than returning 
 * <code>Antecedent</code>s consisting of just those conjuncts that have been
 * added, it returns <code>Antecedent</code>s consisting of the original 
 * conjuncts with the new conjuncts appended at the end.</p>
 */
public class DevelopmentAppender implements AntecedentTransformer {

	private final AntecedentDeveloper myDeveloper;
	
	public DevelopmentAppender(AntecedentDeveloper d) {
		myDeveloper = d;
	}
	
	@Override
	public Iterator<Antecedent> transform(Antecedent source) {
		return new Extender(source, myDeveloper.transform(source));
	}
	
	private class Extender implements Iterator<Antecedent> {

		private final Antecedent myOriginal;
		private final Iterator<Antecedent> myExtensions;
		
		public Extender(Antecedent original, Iterator<Antecedent> extensions) {
			myOriginal = original;
			myExtensions = extensions;
		}
		
		@Override
		public boolean hasNext() {
			return myExtensions.hasNext();
		}

		@Override
		public Antecedent next() {
			return myOriginal.appended(myExtensions.next());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}

}
