package edu.clemson.cs.r2jt.utilities;

import java.util.Iterator;

/**
 * <p>Wraps an existing <code>Iterator</code> and disables its 
 * <code>remove()</code> method, ensuring that clients cannot change the
 * contents of encapsulated lists.  Note that if the iterator returns mutable
 * objects, the contained objects themselves could still be changed.</p>
 */
public class ImmutableIterator<T> implements Iterator<T> {

	private final Iterator<T> myInnerIterator;
	
	public ImmutableIterator(Iterator<T> inner) {
		myInnerIterator = inner;
	}
	
	@Override
	public boolean hasNext() {
		return myInnerIterator.hasNext();
	}

	@Override
	public T next() {
		return myInnerIterator.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Iterator is immutable.");
	}

}
