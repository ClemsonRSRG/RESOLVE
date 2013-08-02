/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.translation.bookkeeping.book;


public class JavaEnhancementChapter extends AbstractBookDecorator {
		
	public JavaEnhancementChapter(Book workingBook) {
		super(workingBook);
	}
	
	@Override
	public String getTitle() {
		return decoratedBook.getTitle();
	}
	
	@Override
    public String getString() {
        throw new UnsupportedOperationException("Not supported yet.");
	}
}
