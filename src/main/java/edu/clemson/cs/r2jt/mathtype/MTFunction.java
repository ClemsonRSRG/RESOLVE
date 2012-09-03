package edu.clemson.cs.r2jt.mathtype;

public class MTFunction extends MTAbstract<MTFunction> {

	private final MTType myDomain;
	private final MTType myRange;
	
	public MTFunction(MTType domain, MTType range) {
		myDomain = domain;
		myRange = range;
	}
	
	public MTType getDomain() {
		return myDomain;
	}
	
	public MTType getRange() {
		return myRange;
	}
	
	@Override
	public boolean valueEqual(MTFunction t) {
		return myDomain.equals(t.getDomain()) && 
				myRange.equals(t.getRange());
	}
	
	@Override
	public int hashCode() {
		return myDomain.hashCode() * 31 + myRange.hashCode();
	}
}
