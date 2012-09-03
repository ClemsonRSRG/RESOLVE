package edu.clemson.cs.r2jt.proving.absyn;

import java.util.Iterator;

public class PSymbolArgumentIterator implements PExpSubexpressionIterator {

	private final PSymbol myOriginalSymbol;
	private final Iterator<PExp> myArgumentIterator;
	private int myLastReturnedIndex = -1;
	
	public PSymbolArgumentIterator(PSymbol s) {
		myOriginalSymbol = s;
		myArgumentIterator = s.arguments.iterator();
	}
	
	@Override
	public boolean hasNext() {
		return myArgumentIterator.hasNext();
	}

	@Override
	public PExp next() {
		PExp retval = myArgumentIterator.next();
		myLastReturnedIndex++;
		
		return retval;
	}

	@Override
	public PExp replaceLast(PExp newExpression) {
		if (myLastReturnedIndex == -1) {
			throw new IllegalStateException("Must call next() first.");
		}
		
		return myOriginalSymbol.setArgument(myLastReturnedIndex, newExpression);
	}
}
