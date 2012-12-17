package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DummyIterator<T> implements Iterator<T> {

	private final static DummyIterator<Object> INSTANCE = 
		new DummyIterator<Object>();
	
	private DummyIterator() {
		
	}

	@SuppressWarnings("unchecked")
	public static <T> Iterator<T> getInstance(Iterator<T> i) {
		return (Iterator<T>) INSTANCE;
	}
	
	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public T next() {
		throw new NoSuchElementException();
	}

	@Override
	public void remove() {
		
	}

}
