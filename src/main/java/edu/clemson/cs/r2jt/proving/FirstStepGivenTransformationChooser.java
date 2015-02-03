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

import edu.clemson.cs.r2jt.init.CompileEnvironment;

/**
 * <p>A <code>FirstStepGivenTransformationChooser</code> composes with
 * an existing <code>TransformationChooser</code> to insert a particular
 * <code>VCTransformer</code> as the first step in any proof.</p>
 */
public class FirstStepGivenTransformationChooser
        implements
            TransformationChooser {

    private final TransformationChooser myBaseChooser;
    private final VCTransformer myFirstStep;

    /**
     * <p>Creates a new <code>FirstStepDevelopmentTransformationChooser</code>
     * that inserts the given number of rounds of theory development before
     * deferring to <code>baseChooser</code>.</p>
     * 
     * @param baseChooser The existing chooser to defer to.
     * @param rounds The number of rounds of theory development.
     */
    public FirstStepGivenTransformationChooser(
            TransformationChooser baseChooser, VCTransformer firstStep) {

        myFirstStep = firstStep;
        myBaseChooser = baseChooser;
    }

    @Override
    public void preoptimizeForVC(VC vc) {
        myBaseChooser.preoptimizeForVC(vc);
    }

    @Override
    public Iterator<ProofPathSuggestion> suggestTransformations(VC vc,
            int curLength, Metrics metrics, ProofData d) {

        Iterator<ProofPathSuggestion> retval;

        if (curLength == 0) {
            retval =
                    new SingletonIterator<ProofPathSuggestion>(
                            new ProofPathSuggestion(myFirstStep, d, null,
                                    "After first step (" + myFirstStep
                                            + "), VC is: "));
        }
        else {
            retval =
                    myBaseChooser.suggestTransformations(vc, curLength - 1,
                            metrics, d);
        }

        return retval;
    }

    @Override
    public String toString() {
        return "FirstStepGiven(" + myFirstStep + ", then continue with "
                + myBaseChooser + ")";
    }
}
