package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

public class SizedIterator<T> implements KnownSizeIterator<T> {

	private Iterator<T> myInternalIterator;
	private final int mySize;
	
	public SizedIterator(Iterator<T> i, int size) {
		myInternalIterator = i;
		mySize = size; 
	}

	public Iterator<T> getInternalIterator() {
		return myInternalIterator;
	}
	
	public int size() {
		return mySize;
	}

	public boolean hasNext() {
		return myInternalIterator.hasNext();
	}

	public T next() {
		return myInternalIterator.next();
	}

	public void remove() {
		myInternalIterator.remove();
	}

}
