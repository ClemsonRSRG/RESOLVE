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

import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;

/**
 * <p>Uses a provided search pattern to match expressions that will bind (via 
 * <code>Utilities.bind</code>()) against that pattern and replace them with a
 * provided replacement pattern that will be expanded using the bindings from 
 * the original match.</p>
 * 
 * @author H. Smith
 */
public class BindReplace implements MatchReplace {

    private Exp myFindPattern, myReplacePattern;
    private Map<Exp, Exp> myBindings;

    /**
     * <p>Creates a new <code>BindReplace</code> that will replace expressions 
     * that can be bound to <code>findPattern</code> with copies of the 
     * provided replace pattern in which expansions have been made based on the
     * binding step.</p>
     * 
     * @param findPattern The pattern to bind with.
     * @param replacePattern The pattern to expand as a replacement.
     */
    public BindReplace(Exp findPattern, Exp replacePattern) {

        myFindPattern = findPattern;
        myReplacePattern = replacePattern;
    }

    public boolean couldReplace(Exp e) {

        myBindings = Utilities.newBind(myFindPattern, e);

        return (myBindings != null);
    }

    public Exp getReplacement() {
        return myReplacePattern.substitute(myBindings);
    }

    public String toString() {
        return "Replace " + myFindPattern.toString(0) + " with "
                + myReplacePattern.toString(0);
    }

    @Override
    public Exp getExpansionTemplate() {
        return Exp.copy(myReplacePattern);
    }

    @Override
    public Exp getPattern() {
        return Exp.copy(myFindPattern);
    }
}
