/**
 * StrengthenConsequentStep.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
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
