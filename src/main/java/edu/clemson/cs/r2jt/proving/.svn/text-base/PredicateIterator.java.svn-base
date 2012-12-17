package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 * <p>A <code>PredicateIterator</code> wraps an existing <code>Iterator</code>
 * and iterates over those elements from the original that satisfy a given
 * predicate (in the form of a <code>Mapping</code> from <code>T</code> to
 * <code>Boolean</code>).
 *
 * @param <T> The type of the elements returned by the Iterator;
 */
public class PredicateIterator<T> implements Iterator<T> {

	private final Iterator<T> myBaseIterator;
	private T myNext;
	private Mapping<T, Boolean> myPredicate;
	
	public PredicateIterator(Iterator<T> base, Mapping<T, Boolean> p) {
		myBaseIterator = base;
		myPredicate = p;
	}
	
	@Override
	public boolean hasNext() {
		boolean retval = false;
		
		if (myNext == null) {
			while (myBaseIterator.hasNext() && !retval) {
				myNext = myBaseIterator.next();
				retval = myPredicate.map(myNext);
			}
		}
		
		return (myNext != null);
	}

	@Override
	public T next() {
		T retval = myNext;
		
		myNext = null;
		
		return retval;
	}

	@Override
	public void remove() {
		myBaseIterator.remove();
	}

}
