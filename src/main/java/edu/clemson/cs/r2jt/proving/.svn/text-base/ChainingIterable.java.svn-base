package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChainingIterable<T> implements Iterable<T> {

	private final List<Iterable<T>> mySubIterables = 
		new LinkedList<Iterable<T>>();
	
	/**
	 * <p>Adds a new iterable set of <code>T</code>s to the list of 
	 * <code>T</code>s iterated over.</p>
	 * 
	 * @param i A non-null <code>Iterable</code>.
	 */
	public void add(Iterable<T> i) {
		mySubIterables.add(i);
	}
	
	public void add(T t) {
		List<T> wrapper = new LinkedList<T>();
		wrapper.add(t);
		
		mySubIterables.add(wrapper);
	}
	
	@Override
	public Iterator<T> iterator() {
		return new ListOfListsIterator(mySubIterables.iterator());
	}
	
	private class ListOfListsIterator implements Iterator<T> {

		private final Iterator<Iterable<T>> myLists;
		private Iterator<T> myCurList;
		private T myCurElement;
		
		public ListOfListsIterator(Iterator<Iterable<T>> l) {
			myLists = l;
			
			if (myLists.hasNext()) {
				myCurList = myLists.next().iterator();
				myCurElement = nextElement();
			}
			else {
				myCurElement = null;
			}
		}
		
		@Override
		public boolean hasNext() {
			return myCurElement != null;
		}

		@Override
		public T next() {
			T retval = myCurElement;
			
			myCurElement = nextElement();
			
			return retval;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		private T nextElement() {
			T retval;
			
			boolean curListHasNext = myCurList.hasNext();
			while (!curListHasNext && myLists.hasNext()) {
				myCurList = myLists.next().iterator();
				curListHasNext = myCurList.hasNext();
			}
			
			if (curListHasNext) {
				retval = myCurList.next();
			}
			else {
				retval = null;
			}
			
			return retval;
		}
	}

}
