package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ChainingIterator<T> implements Iterator<T> {

	private Iterator<T> myStartIterator;
	private boolean myStartHasNext = true;
	private Iterator<T> myEndIterator;
	private boolean myLastFromStartFlag;
	
	public ChainingIterator(Iterator<T> start, Iterator<T> end) {
		
		//TODO : This can be removed to increase performance
		if (start == null || end == null) {
			throw new IllegalArgumentException();
		}
		
		myStartIterator = start;
		myEndIterator = end;
	}
	
	public boolean hasNext() {
		if (myStartHasNext) {
			myStartHasNext = myStartIterator.hasNext();
		}
		
		return (myStartHasNext || myEndIterator.hasNext());
	}

	public T next() {
		T retval;
		
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		
		if (myStartHasNext) {
			retval = myStartIterator.next();
			myLastFromStartFlag = true;
		}
		else {
			retval = myEndIterator.next();
			myLastFromStartFlag = false;
		}
		
		return retval;
	}

	public void remove() {
		if (myLastFromStartFlag) {
			myStartIterator.remove();
		}
		else {
			myEndIterator.remove();
		}
	}

}
