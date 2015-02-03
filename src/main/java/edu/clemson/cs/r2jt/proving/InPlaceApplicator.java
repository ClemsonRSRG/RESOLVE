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

/**
 * <p>An <code>InPlaceApplicator</code> applies a <code>MatchReplace</code> by
 * identifying expressions against which it can match and replacing those
 * expressions in-place, generating new <code>ImmutableConjuncts</code> with the
 * same number of, but possibly different, conjuncts.</p>
 */
public class InPlaceApplicator implements ReplacementApplicator {

    /**
     * <p>The original list of conjuncts, on which to iterate over possible
     * replacements.</p>
     * 
     * <p>INVARIANT: <code>myConjuncts != null</code></p>
     */
    private final ImmutableConjuncts myConjuncts;

    /**
     * <p>The workhorse to perform the actual matches and suggest the 
     * replacements.</p>
     */
    private final ReplacementSuggester mySuggester;

    /**
     * <p>Creates a new <code>InPlaceApplicator</code> that will iterate 
     * over all single replacements (according to the provided 
     * <code>matcher</code>) available in the provided list of conjuncts, in
     * place.</p>
     * 
     * @param conjuncts The expressions in which to make the single replacement.
     * @param matcher The matcher to govern what gets replaced and with what.
     */
    public InPlaceApplicator(ImmutableConjuncts conjuncts,
            NewMatchReplace matcher) {

        myConjuncts = conjuncts;

        mySuggester = new ReplacementSuggester(conjuncts, matcher);
    }

    @Override
    public ImmutableConjuncts getNextApplication() {

        ImmutableConjuncts retval;

        ReplacementSuggester.Suggestion s = mySuggester.nextMatch();

        if (s == null) {
            retval = null;
        }
        else {
            ImmutableConjuncts newStuff = new ImmutableConjuncts(s.newConjunct);

            int sConjunctIndex = s.conjunctIndex;

            retval =
                    myConjuncts.removed(sConjunctIndex).inserted(
                            sConjunctIndex, newStuff);
        }

        return retval;
    }
}
