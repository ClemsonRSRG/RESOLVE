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
package edu.clemson.cs.r2jt.proving2.model;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.justifications.Justification;
import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 *
 * @author hamptos
 */
public class LocalTheorem extends Theorem {

    public static final Mapping<LocalTheorem, PExp> UNWRAPPER =
            new LocalTheoremUnwrapper();

    /**
     * <p>Mathematically speaking, once we successfully move all the consequents
     * "above the line", i.e., establish them as things we know, we're done,
     * and we don't care which things above the line were part of our original
     * givens and which were the things we were originally trying to establish.
     * Practically, however, in order to reconstruct the proof we need to know
     * which antecedents started life as consequents.  This flag is set to 
     * indicate that an established truth is one of the things we were 
     * <em>trying</em> to establish, rather than some intermediary or original
     * given.</p>
     */
    private boolean myThingWeWereTryingToProveFlag;

    LocalTheorem(PExp assertion, Justification justification,
            boolean tryingToProveThis) {

        super(assertion, justification);

        myThingWeWereTryingToProveFlag = tryingToProveThis;
    }

    private static class LocalTheoremUnwrapper
            implements
                Mapping<LocalTheorem, PExp> {

        @Override
        public PExp map(LocalTheorem input) {
            return input.getAssertion();
        }
    }

    public boolean amTryingToProveThis() {
        return myThingWeWereTryingToProveFlag;
    }

    @Override
    public boolean editable() {
        return true;
    }

    @Override
    public boolean libraryTheorem() {
        return false;
    }
}
