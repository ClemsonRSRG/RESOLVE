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

import java.util.Iterator;

/**
 * <p>An <code>AccumulatingAntecedentExtender</code> modifies the functionality
 * of an existing <code>AntecedentDeveloper</code> by accumulating all of the
 * existing extender's additional antecedent conjuncts into a single 
 * transformation.  So, if the original antecedent contained conjuncts (A, B, C)
 * and the existing extender would suggest new antecedents ((D), (E), (F)), 
 * the result of a call to an <code>AccumulatingAntecedentExtender</code>'s 
 * <code>transform()</code> method will return ((D, E, F)).</p>
 */
public class AccumulatingAntecedentExtender implements AntecedentDeveloper {

    private final Transformer<Antecedent, Iterator<Antecedent>> mySubTransformer;

    public AccumulatingAntecedentExtender(AntecedentDeveloper t) {
        mySubTransformer = t;
    }

    @Override
    public Iterator<Antecedent> transform(Antecedent original) {
        Iterator<Antecedent> singleBindingExtensions =
                mySubTransformer.transform(original);

        Antecedent singleBindingExtension;
        Antecedent workingAntecedent = Antecedent.EMPTY;
        while (singleBindingExtensions.hasNext()) {

            singleBindingExtension = singleBindingExtensions.next();

            workingAntecedent =
                    workingAntecedent.appended(singleBindingExtension);
        }

        Iterator<Antecedent> retval;

        if (workingAntecedent == Antecedent.EMPTY) {
            retval = DummyIterator.getInstance((Iterator<Antecedent>) null);
        }
        else {
            retval = new SingletonIterator<Antecedent>(workingAntecedent);
        }

        return retval;
    }

    public String toString() {
        return mySubTransformer.toString();
    }
}
