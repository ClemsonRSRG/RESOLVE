package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

public class IterableIterator<T> implements Iterable<T> {

	private final Iterator<T> myIterator;
	
	public IterableIterator(Iterator<T> iterator) {
		myIterator = iterator;
	}
	
	@Override
	public Iterator<T> iterator() {
		return myIterator;
	}

}
