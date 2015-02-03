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

public class ChooserEncapsulationStep implements VCTransformer {

    private final String myName;
    private final NoBacktrackChooser myChooser;

    public ChooserEncapsulationStep(String name, TransformationChooser c) {
        myChooser = new NoBacktrackChooser(c);
        myName = name;
    }

    public ChooserEncapsulationStep(String name, NoBacktrackChooser c) {
        myChooser = c;
        myName = name;
    }

    @Override
    public Iterator<VC> transform(VC original) {

        Metrics m = new Metrics();
        ProofData d = new ProofData();

        myChooser.preoptimizeForVC(original);

        Iterator<ProofPathSuggestion> transformations =
                myChooser.suggestTransformations(original, 0, m, d);

        int length = 0;
        VC curVC = original;
        VC newVC = null;
        ProofPathSuggestion next;
        Iterator<VC> suggestions;
        boolean foundNext;
        while (transformations.hasNext()) {
            next = transformations.next();

            suggestions = next.step.transform(curVC);

            foundNext = false;
            while (suggestions.hasNext() && !foundNext) {
                newVC = suggestions.next();

                foundNext = (!newVC.equivalent(curVC));
            }

            if (foundNext) {
                curVC = newVC;
                length++;

                transformations =
                        myChooser.suggestTransformations(curVC, length, m,
                                next.data);
            }
        }

        return new SingletonIterator<VC>(curVC);
    }

    public String toString() {
        return myName;
    }

    @Override
    public Antecedent getPattern() {
        throw new UnsupportedOperationException("Not applicable.");
    }

    @Override
    public Consequent getReplacementTemplate() {
        throw new UnsupportedOperationException("Not applicable.");
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        return true;
    }
}
