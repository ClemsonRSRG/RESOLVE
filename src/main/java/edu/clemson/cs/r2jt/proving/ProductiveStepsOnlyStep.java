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

import edu.clemson.cs.r2jt.utilities.Mapping;

/**
 * <p>A <code>ProductiveStepsOnlyStep</code> wraps an existing 
 * <code>VCTransformer</code> to return only those modified version of the
 * original VC that "significantly" change it.  Where significantly means that
 * the original and transformed VCs don't reduce to be equivalent after calls to
 * <code>simplify()</code>.</p>
 */
public class ProductiveStepsOnlyStep implements VCTransformer {

    private final VCTransformer myBaseTransformer;

    public ProductiveStepsOnlyStep(VCTransformer base) {
        myBaseTransformer = base;
    }

    @Override
    public Iterator<VC> transform(VC original) {

        GoodVCPredicate p = new GoodVCPredicate(original);

        return new PredicateIterator<VC>(myBaseTransformer.transform(original),
                p);

        //return new ProductiveIterator(myBaseTransformer.transform(original),
        //		original);
    }

    @Override
    public String toString() {
        return myBaseTransformer.toString();
    }

    @Override
    public Antecedent getPattern() {
        return myBaseTransformer.getPattern();
    }

    @Override
    public Consequent getReplacementTemplate() {
        return myBaseTransformer.getReplacementTemplate();
    }

    private class GoodVCPredicate implements Mapping<VC, Boolean> {

        private final VC myOriginalVC;

        public GoodVCPredicate(VC original) {
            myOriginalVC = original;
        }

        @Override
        public Boolean map(VC input) {
            return !input.simplify().equivalent(myOriginalVC);
        }
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        return myBaseTransformer.introducesQuantifiedVariables();
    }
}
