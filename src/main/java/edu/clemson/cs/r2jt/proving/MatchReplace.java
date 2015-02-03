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
package edu.clemson.cs.r2jt.proving;

import edu.clemson.cs.r2jt.absyn.Exp;

/**
 * <p>Defines a general search/replace mechanism.</p>
 * 
 * @author H. Smith
 */
public interface MatchReplace {

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> the provided expression
     * could be replaced.</p>
     * 
     * @param e The expression to test.
     * 
     * @return <code>true</code> <strong>iff</strong> the provided expression
     *         could be replaced.
     */
    public boolean couldReplace(Exp e);

    /**
     * <p>Returns the <code>Exp</code> which should replace the expression that
     * was provided in the last call to <code>couldReplace</code>().</p>
     * 
     * @return The <code>Exp</code> which should replace the expression that
     *         was provided in the last call to <code>couldReplace</code>().  
     *         Undefined if <code>couldReplace</code>() has not yet been called.
     */
    public Exp getReplacement();

    /**
     * <p>Gets a copy of the pattern this MatchReplace is looking for.</p>
     * 
     * @return A deep copy of the pattern being used to match.
     */
    public Exp getPattern();

    /**
     * <p>Gets a copy of the template this MatchReplace will use to produce the
     * replacements for anything matched by the pattern.</p>
     * 
     * @return A deep copy of the template used to replace.
     */
    public Exp getExpansionTemplate();
}
