/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
