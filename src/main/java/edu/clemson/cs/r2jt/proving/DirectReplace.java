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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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
 * <p>Replaces expressions that exactly match (via <code>equals</code>()) a
 * provided expression, replacing it with a clone of a provided replacement.</p>
 * 
 * @author H. Smith
 */
public class DirectReplace implements MatchReplace {

    private final Exp myFind, myReplace;

    /**
     * <p>Creates a new <code>DirectReplace</code> which will replace instances
     * of <code>find</code> with clones of <code>replace</code>.</p>
     * 
     * @param find The thing to match against with <code>equals</code>().
     * @param replace The thing to replace matches with.
     */
    public DirectReplace(final Exp find, final Exp replace) {
        myFind = find;
        myReplace = replace;
    }

    public boolean couldReplace(Exp e) {
        return (e.equivalent(myFind));
    }

    public Exp getReplacement() {
        return Exp.copy(myReplace);
    }

    public String toString() {
        return myFind.toString(0) + " --> " + myReplace.toString(0);
    }

    @Override
    public Exp getExpansionTemplate() {
        return Exp.copy(myFind);
    }

    @Override
    public Exp getPattern() {
        return Exp.copy(myReplace);
    }
}
