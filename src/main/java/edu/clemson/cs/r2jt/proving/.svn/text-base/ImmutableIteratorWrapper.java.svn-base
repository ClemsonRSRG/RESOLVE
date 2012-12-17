package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

/**
 * <p>An <code>ImmutableIteratorWrapper</code> wraps an existing 
 * <code>Iterator</code> and ensures that <code>remove()</code> calls result in
 * an <code>UnsupportedOperationException</code> with no effect on the 
 * underlying <code>Iterator</code>.</p>
 */
public class ImmutableIteratorWrapper<T> implements Iterator<T> {

	private final Iterator<T> myBaseIterator;
	
	public ImmutableIteratorWrapper(Iterator<T> baseIterator) {
		myBaseIterator = baseIterator;
	}
	
	@Override
	public boolean hasNext() {
		return myBaseIterator.hasNext();
	}

	@Override
	public T next() {
		return myBaseIterator.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
