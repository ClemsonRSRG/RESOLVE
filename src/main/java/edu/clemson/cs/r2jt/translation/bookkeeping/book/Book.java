package edu.clemson.cs.r2jt.translation.bookkeeping.book;

import java.util.List;

public interface Book {
	
	// Title of a fxn is public void blah blah blah. title of an
	// enhancement is enhancement X is Y realized by Z. Each require
	// just a single set of parameters.
	public String getTitle();
	
	public List<String> getParameters();
	
	public String getDescription();
	
	public String getString();
	
}