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
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving2.AutomatedProver;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.proofsteps.LabelStep;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class NoOpLabel implements Transformation {

    private final String myLabel;
    private final AutomatedProver myProver;

    public NoOpLabel(AutomatedProver p, String label) {
        myLabel = label;
        myProver = p;
    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        return Collections.singletonList(
                (Application) new NoOpLabelApplication()).iterator();
    }

    @Override
    public boolean couldAffectAntecedent() {
        return false;
    }

    @Override
    public boolean couldAffectConsequent() {
        return false;
    }

    @Override
    public int functionApplicationCountDelta() {
        return 0;
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        return false;
    }

    @Override
    public Set<String> getPatternSymbolNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Set<String> getReplacementSymbolNames() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Equivalence getEquivalence() {
        return Equivalence.EQUIVALENT;
    }

    @Override
    public String toString() {
        return myLabel;
    }

    @Override
    public String getKey() {
        return myLabel + " " + this.getClass().getName();
    }

    private class NoOpLabelApplication implements Application {

        @Override
        public void apply(PerVCProverModel m) {
            m.addProofStep(new LabelStep(myLabel, NoOpLabel.this, this));

            //Useful for debugging--pauses automated prover when a label is 
            //reached
            //myProver.markToPause();
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            return Collections.EMPTY_SET;
        }

        @Override
        public String description() {
            return "Label with \"" + myLabel + "\"";
        }

        @Override
        public Set<Conjunct> getPrerequisiteConjuncts() {
            return Collections.EMPTY_SET;
        }

        @Override
        public Set<Conjunct> getAffectedConjuncts() {
            return Collections.EMPTY_SET;
        }

        @Override
        public Set<Site> getAffectedSites() {
            return Collections.EMPTY_SET;
        }
    }
}
