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

import java.util.Iterator;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PExpNavigator;

/**
 * <p>A <code>ReplacementSuggester</code> provides a mechanism for iterating 
 * over all possible applications of a <code>MatchReplace</code> to a set of 
 * conjuncts.</p>
 */
public class ReplacementSuggester {

    private final Iterator<PExp> myOriginalConjuncts;
    private PExpNavigator myCurConjunctNavigator;
    private int myCurConjunctIndex = 0;

    private final NewMatchReplace myMatcher;

    private boolean myDoneFlag;

    public ReplacementSuggester(ImmutableConjuncts conjuncts,
            NewMatchReplace matcher) {

        myOriginalConjuncts = conjuncts.iterator();
        myDoneFlag = !myOriginalConjuncts.hasNext();

        if (!myDoneFlag) {
            myCurConjunctNavigator =
                    new PExpNavigator(myOriginalConjuncts.next());
        }

        myMatcher = matcher;
    }

    /**
     * <p>Returns the next application suggestion of this 
     * <code>ReplacementSuggester</code>'s <code>MatchReplace</code> to its 
     * conjuncts.</p>
     * 
     * @return A suggested replacement or <code>null</code> if there are no
     *         further replacements.
     */
    public Suggestion nextMatch() {
        Suggestion retval = null;

        if (!myDoneFlag) {

            retval = nextMatchInNavigator(myCurConjunctNavigator);
            while (retval == null && myOriginalConjuncts.hasNext()) {
                myCurConjunctNavigator =
                        new PExpNavigator(myOriginalConjuncts.next());
                myCurConjunctIndex++;

                retval = nextMatchInNavigator(myCurConjunctNavigator);
            }

            if (retval == null) {
                myDoneFlag = true;
            }
        }

        return retval;
    }

    private Suggestion nextMatchInNavigator(PExpNavigator n) {
        Suggestion retval = null;

        while (retval == null && n.hasNext()) {
            retval = matchExp(n.next(), n);
        }

        return retval;
    }

    private Suggestion matchExp(PExp exp, PExpNavigator sourceNavigator) {
        Suggestion retval = null;

        if (myMatcher.couldReplace(exp)) {
            retval =
                    new Suggestion(myCurConjunctIndex, sourceNavigator
                            .replaceLast(myMatcher.getReplacement()));
        }

        return retval;
    }

    /**
     * <p>A <code>Suggestion</code> represents a single application of the 
     * matcher provided to this class.  It indicates the index of the conjunct
     * that should be modified, and a deep copy of that conjunct with the change
     * in place.</p>
     */
    public static class Suggestion {

        /**
         * The index (zero-based) of the conjunct within the original set of
         * conjuncts where the match was made.
         */
        public final int conjunctIndex;

        /**
         * A deep copy of the <code>conjunctIndex</code>th conjunct with the
         * replacement made.
         */
        public final PExp newConjunct;

        private Suggestion(int index, PExp newConjunct) {
            conjunctIndex = index;
            this.newConjunct = newConjunct;
        }
    }
}
