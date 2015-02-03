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

import java.util.Map;

import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;

/**
 * <p>Uses a provided search pattern to match expressions that will bind against
 * that pattern and replace them with a provided replacement pattern that will 
 * be expanded using the bindings from the original match.</p>
 * 
 * <p><strong>N.B.:</strong>  This is intended as a drop-in replacement for
 * {@link edu.clemson.cs.r2jt.proving.BindReplace BindReplace} except that it
 * operates on {@link edu.clemson.cs.r2jt.proving.absyn.PExp PExp}s rather than
 * {@link edu.clemson.cs.r2jt.absyn.Exp Exp}s.  When the new prover is complete
 * and well-tested, <code>BindReplace</code> should be removed entirely and
 * this class should be renamed.</p>
 */
public class NewBindReplace implements NewMatchReplace {

    private PExp myFindPattern, myReplacePattern;
    private Map<PExp, PExp> myBindings;

    /**
     * <p>Creates a new <code>BindReplace</code> that will replace expressions 
     * that can be bound to <code>findPattern</code> with copies of the 
     * provided replace pattern in which expansions have been made based on the
     * binding step.</p>
     * 
     * @param findPattern The pattern to bind with.
     * @param replacePattern The pattern to expand as a replacement.
     */
    public NewBindReplace(PExp findPattern, PExp replacePattern) {

        myFindPattern = findPattern;
        myReplacePattern = replacePattern;
    }

    public boolean couldReplace(PExp e) {
        myBindings = null;

        try {
            myBindings = myFindPattern.bindTo(e);
        }
        catch (BindingException ex) {

        }

        return (myBindings != null);
    }

    public PExp getReplacement() {
        return myReplacePattern.substitute(myBindings);
    }

    public String toString() {
        return "Replace " + myFindPattern + " with " + myReplacePattern;
    }

    @Override
    public PExp getExpansionTemplate() {
        return myReplacePattern;
    }

    @Override
    public PExp getPattern() {
        return myFindPattern;
    }
}
