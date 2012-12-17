package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

public class StaticConsequentIterator implements Iterator<VC> {
	private final Iterator<Antecedent> myAntecedentIterator;
	private final Consequent myConsequent;
	private final String myOriginalVCName;
	
	public StaticConsequentIterator(String originalVCName, 
			Iterator<Antecedent> i, Consequent c) {
		myAntecedentIterator = i;
		myConsequent = c;
		myOriginalVCName = originalVCName;
	}
	
	@Override
	public boolean hasNext() {
		return myAntecedentIterator.hasNext();
	}

	@Override
	public VC next() {
		return new VC(myOriginalVCName, myAntecedentIterator.next(), 
				myConsequent, true);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
