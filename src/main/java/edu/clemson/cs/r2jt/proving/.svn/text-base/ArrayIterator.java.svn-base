package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<E> implements Iterator<E> {
	
	private final E[] myArray;
	
	private final int myFirstUnincludedIndex;
	
	private int myCursor = 0;
	
	public ArrayIterator(E[] array) {
		this(array, 0, array.length);
	}
	
	public ArrayIterator(E[] array, int start, int length) {
		myArray = array;
		
		myCursor = start;
		myFirstUnincludedIndex = myCursor + length;
	}
	
	@Override
	public boolean hasNext() {
		return myCursor < myFirstUnincludedIndex;
	}

	@Override
	public E next() {
		E retval;
		
		try {
			retval = myArray[myCursor];
		}
		catch (IndexOutOfBoundsException ex) {
			throw new NoSuchElementException();
		}
		
		myCursor++;
		
		return retval;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
