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
package edu.clemson.cs.r2jt.proving2.proofsteps;

import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class StrengthenConsequentStep extends AbstractProofStep {

    private final Set<Conjunct> myNewSites;
    private final ConjunctWithIndex[] myEliminatedConjuncts;

    public StrengthenConsequentStep(List<Conjunct> eliminatedConjuncts,
            List<Integer> eliminatedConjunctsIndecis,
            Set<Conjunct> newConjuncts, Transformation t, Application a,
            Collection<Site> boundSites) {
        super(t, a, boundSites);

        myEliminatedConjuncts =
                new ConjunctWithIndex[eliminatedConjuncts.size()];
        Iterator<Conjunct> conjunctIter = eliminatedConjuncts.iterator();
        Iterator<Integer> indexIter = eliminatedConjunctsIndecis.iterator();
        int index = 0;
        while (conjunctIter.hasNext()) {
            myEliminatedConjuncts[index] =
                    new ConjunctWithIndex(conjunctIter.next(), indexIter.next());
            index++;
        }
        Arrays.sort(myEliminatedConjuncts);

        myNewSites = newConjuncts;
    }

    @Override
    public void undo(PerVCProverModel m) {
        for (Conjunct newConjunct : myNewSites) {
            m.removeConjunct(newConjunct);
        }

        Set<Conjunct> alreadyAdded = new HashSet<Conjunct>();
        for (ConjunctWithIndex elminatedConjunct : myEliminatedConjuncts) {
            if (!alreadyAdded.contains(elminatedConjunct.getConjunct())) {
                m.insertConjunct(elminatedConjunct.getConjunct(),
                        elminatedConjunct.getIndex());

                alreadyAdded.add(elminatedConjunct.getConjunct());
            }
        }
    }

    @Override
    public String toString() {
        return "" + getTransformation();
    }

    private class ConjunctWithIndex implements Comparable<ConjunctWithIndex> {

        private final Conjunct myConjunct;
        private final int myIndex;

        public ConjunctWithIndex(Conjunct c, int i) {
            myConjunct = c;
            myIndex = i;
        }

        @Override
        public int compareTo(ConjunctWithIndex o) {
            return myIndex - o.myIndex;
        }

        public Conjunct getConjunct() {
            return myConjunct;
        }

        public int getIndex() {
            return myIndex;
        }
    }
}
