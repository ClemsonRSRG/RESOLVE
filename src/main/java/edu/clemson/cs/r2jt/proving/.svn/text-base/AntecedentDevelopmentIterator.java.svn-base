package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

public class AntecedentDevelopmentIterator implements Iterator<Antecedent> {

	private final Antecedent myOriginal;
	
	private final Iterator<Antecedent> myDevelopments;
	
	public AntecedentDevelopmentIterator(Antecedent original, 
			Iterator<Antecedent> d) {
		
		myOriginal = original;
		myDevelopments = d;
	}
	
	@Override
	public boolean hasNext() {
		return myDevelopments.hasNext();
	}

	@Override
	public Antecedent next() {
		return myOriginal.appended(myDevelopments.next());
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
