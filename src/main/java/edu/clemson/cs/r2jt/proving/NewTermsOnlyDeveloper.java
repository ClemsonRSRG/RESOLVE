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
import java.util.Set;

import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 * <p>A <code>NewTermsOnlyStep</code> wraps an existing 
 * <code>VCTransformer</code> to return only those modified versions of the
 * original VC that introduce new function or variable names.  For example,
 * taking <code>|empty_string|</code> and transforming it into <code>0</code>
 * introduces a new term (0) that did not exist in the original.  However,
 * taking <code>x + 0</code> and transforming it into <code>x + 0 + 0</code>
 * introduces nothing new and so is discarded.</p>
 */
public class NewTermsOnlyDeveloper implements AntecedentDeveloper {

    private final AntecedentDeveloper myBaseDeveloper;

    public NewTermsOnlyDeveloper(AntecedentDeveloper base) {
        myBaseDeveloper = base;
    }

    @Override
    public Iterator<Antecedent> transform(Antecedent source) {

        GoodVCPredicate p = new GoodVCPredicate(source);

        return new PredicateIterator<Antecedent>(myBaseDeveloper
                .transform(source), p);
    }

    /**
     * <p>A predicate that selects only those VCs that introduce new terms.</p>
     */
    private class GoodVCPredicate implements Mapping<Antecedent, Boolean> {

        private Set<String> myOriginalVCSymbols;
        private int myOriginalApplicationCount;

        public GoodVCPredicate(Antecedent original) {
            myOriginalVCSymbols = original.getSymbolNames();
            myOriginalApplicationCount =
                    original.getFunctionApplications().size();
        }

        @Override
        public Boolean map(Antecedent input) {

            boolean retval = false; /*(input.getFunctionApplications().size() <= 
                                    myOriginalApplicationCount);*/

            if (!retval) {
                Set<String> inputSymbols = input.getSymbolNames();

                inputSymbols.removeAll(myOriginalVCSymbols);

                retval = (inputSymbols.size() != 0);
            }

            return retval;
        }
    }

}
