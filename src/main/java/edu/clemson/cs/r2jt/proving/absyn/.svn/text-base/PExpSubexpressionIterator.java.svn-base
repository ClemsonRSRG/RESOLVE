package edu.clemson.cs.r2jt.proving.absyn;

/**
 * <p>A <code>PExpSubexpressionIterator</p> defines the interface for classes
 * that iterate over the subexpressions of a 
 * {@link edu.clemson.cs.r2jt.proving.absyn.PExp PExp}, with the ability to get
 * a version of the original <code>PExp</code> in which any given subexpression
 * has been replaced with another.</p>
 */
public interface PExpSubexpressionIterator {
	
	/**
	 * <p>Returns <code>true</code> <strong>iff</strong> there are additional
	 * subexpressions.  I.e., returns <code>true</code> <strong>iff</strong>
	 * <code>next()</code> would return an element rather than throwing an
	 * exception.</p>
	 * 
	 * @return <code>true</code> if the iterator has more elements.
	 */
	public boolean hasNext();
	
	/**
	 * </p>Returns the next subexpression./p>
	 * 
	 * @return The next element in the iteration.
	 * 
	 * @throws NoSuchElementException If there are no further subexpressions.
	 */
	public PExp next();
	
	/**
	 * <p>Returns a version of the original <code>PExp</code> (i.e., the 
	 * <code>PExp</code> over whose subexpressions we are iterating) with the 
	 * subexpression most recently returned by <code>next()</code> replaced with
	 * <code>newExpression</code>.</p>
	 * 
	 * @param newExpression The argument to replace the most recently returned 
	 *                      one with.
	 *                    
	 * @return The new version.
	 */
	public PExp replaceLast(PExp newExpression);
}
