/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation.bookkeeping.book;

import java.util.List;

// uh Dis ain't gonna work boss.
public abstract class AbstractBookDecorator implements Book {
	protected Book decoratedBook;
	
	public AbstractBookDecorator(Book decoBook) {
		this.decoratedBook = decoBook;
	}
	
	@Override
	public String getTitle() {
		return decoratedBook.getTitle();
	}
	
	@Override 
	public List<String> getParameters() {
		return decoratedBook.getParameters();
	}
	
	@Override 
	public String getDescription() {
		return decoratedBook.getDescription();
	}
	
	@Override
	public abstract String getString();
}
