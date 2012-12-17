package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

public class SingletonIterator<T> implements Iterator<T> {

	private final T myElement;
	private boolean myReturnedFlag = false;
	
	public SingletonIterator(T element) {
		myElement = element;
	}
	
	@Override
	public boolean hasNext() {
		return !myReturnedFlag;
	}

	@Override
	public T next() {
		myReturnedFlag = true;
		return myElement;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
