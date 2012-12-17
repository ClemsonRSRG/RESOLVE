package edu.clemson.cs.r2jt.proving.absyn;

import java.util.NoSuchElementException;

public class EmptySubexpressionIterator implements PExpSubexpressionIterator {

	public static final EmptySubexpressionIterator INSTANCE = 
		new EmptySubexpressionIterator();
	
	private EmptySubexpressionIterator() {
		
	}
	
	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public PExp next() {
		throw new NoSuchElementException();
	}

	@Override
	public PExp replaceLast(PExp newExpression) {
		throw new IllegalStateException("Must call next() first.");
	}

}