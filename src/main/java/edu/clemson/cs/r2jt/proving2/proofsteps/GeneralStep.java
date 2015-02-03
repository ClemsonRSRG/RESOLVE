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
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class GeneralStep extends AbstractProofStep {

    private final List<Conjunct> myRemovedConjuncts;
    private final List<Integer> myRemovedConjunctsIndecis;

    private Map<Conjunct, PExp> myOriginalValues;

    private Set<Conjunct> myIntroducedConjuncts;

    public GeneralStep(List<Conjunct> removedConjuncts,
            List<Integer> removedConjunctsIndecis,
            Map<Conjunct, PExp> originalValues,
            Set<Conjunct> introducedConjuncts, Transformation t, Application a,
            Collection<Site> boundSites) {
        super(t, a, boundSites);

        myRemovedConjuncts = removedConjuncts;
        myRemovedConjunctsIndecis = removedConjunctsIndecis;

        myOriginalValues = originalValues;

        myIntroducedConjuncts = introducedConjuncts;
    }

    public Set<Conjunct> getIntroducedConjuncts() {
        return myIntroducedConjuncts;
    }

    @Override
    public void undo(PerVCProverModel m) {
        //Reintroduce removed conjuncts
        Iterator<Conjunct> removedConjunctIter = myRemovedConjuncts.iterator();
        Iterator<Integer> removedConjunctIndexIter =
                myRemovedConjunctsIndecis.iterator();

        while (removedConjunctIter.hasNext()) {
            m.insertConjunct(removedConjunctIter.next(),
                    removedConjunctIndexIter.next());
        }

        //Restore original values
        for (Map.Entry<Conjunct, PExp> originalValue : myOriginalValues
                .entrySet()) {
            m.alterConjunct(originalValue.getKey(), originalValue.getValue());
        }

        //Remove introduced conjuncts
        for (Conjunct introducedConjunct : myIntroducedConjuncts) {
            m.removeConjunct(introducedConjunct);
        }
    }

    @Override
    public String toString() {
        return "" + getTransformation();
    }
}
