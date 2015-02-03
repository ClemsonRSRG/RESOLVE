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
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.transformations.ExpandAntecedentBySubstitution;
import edu.clemson.cs.r2jt.proving2.transformations.NoOpLabel;
import edu.clemson.cs.r2jt.proving2.transformations.SubstituteInPlaceInAntecedent;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hamptos
 */
public class AntecedentMinimizer implements Automator {

    private static final ApplyAll DUMMY_APPLIER =
            new ApplyAll(new NoOpLabel(null, "Dummy"));

    public List<ExpandAntecedentBySubstitution> myReducingTranformations =
            new LinkedList<ExpandAntecedentBySubstitution>();

    private Iterator<ExpandAntecedentBySubstitution> myCurrentRound;
    private ApplyAll myCurrentApplier;
    private boolean myProductiveRoundFlag = false;

    public AntecedentMinimizer(ImmutableList<Theorem> theoremLibrary) {
        for (Theorem theorem : theoremLibrary) {
            for (Transformation t : theorem.getTransformations()) {
                if (t instanceof ExpandAntecedentBySubstitution
                        && !t.introducesQuantifiedVariables()
                        && t.functionApplicationCountDelta() < 0) {
                    myReducingTranformations
                            .add((ExpandAntecedentBySubstitution) t);
                }
            }
        }

        myCurrentRound = myReducingTranformations.iterator();
        myCurrentApplier = DUMMY_APPLIER; //This will never be applied
    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        if (myCurrentRound.hasNext()) {
            myProductiveRoundFlag =
                    myProductiveRoundFlag
                            || (myCurrentApplier.getApplicationCount() > 0);

            ExpandAntecedentBySubstitution expander = myCurrentRound.next();
            SubstituteInPlaceInAntecedent substituter =
                    new SubstituteInPlaceInAntecedent(expander.getTheorem(),
                            expander.getMatchPattern(), expander
                                    .getTransformationTemplate());

            myCurrentApplier = new ApplyAll(substituter);
            stack.push(myCurrentApplier);
        }
        else {
            if (myProductiveRoundFlag) {
                myCurrentRound = myReducingTranformations.iterator();
                myProductiveRoundFlag = false;
                myCurrentApplier = DUMMY_APPLIER; //This will never be applied
            }
            else {
                stack.pop();
            }
        }
    }

}
