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
package edu.clemson.cs.r2jt.proving2.applications;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.justifications.TheoremApplication;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.proofsteps.GeneralStep;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class GeneralApplication implements Application {

    //This is stuff we're initialized with
    private final Map<Conjunct, PExp> myConjunctsToUpdate;
    private final List<PExp> myLocalTheoremsToAdd;
    private final List<Integer> myLocalTheoremsToAddIndecis;
    private final Collection<Site> myInvolvedSubExpressions;

    //This is stuff that gets filled in after apply() that helps our proof
    //step perform an undo()
    private List<Conjunct> myRemovedConjuncts = new LinkedList<Conjunct>();
    private List<Integer> myRemovedConjunctsIndecis = new LinkedList<Integer>();

    private Map<Conjunct, PExp> myOriginalConjunctValues =
            new HashMap<Conjunct, PExp>();
    private Set<Conjunct> myAddedConjuncts = new HashSet<Conjunct>();

    private Set<Site> myAffectedSites = new HashSet<Site>();

    private final Transformation myTransformation;
    private final Theorem myTheorem;

    public GeneralApplication(Collection<Site> involvedSubExpressions,
            Map<Conjunct, PExp> updateConjuncts, List<PExp> addLocalTheorems,
            List<Integer> addLocalTheoremsIndecis, Transformation t,
            Theorem theorem) {

        if (addLocalTheorems.size() != addLocalTheoremsIndecis.size()) {
            throw new IllegalArgumentException("addLocalTheorems and "
                    + "addLocalTheoremsIndecis must have the same size.");
        }

        myConjunctsToUpdate = updateConjuncts;
        myLocalTheoremsToAdd = addLocalTheorems;
        myLocalTheoremsToAddIndecis = addLocalTheoremsIndecis;
        myInvolvedSubExpressions = involvedSubExpressions;
        myTransformation = t;
        myTheorem = theorem;
    }

    @Override
    public String description() {
        String result;

        if (myLocalTheoremsToAdd.isEmpty()) {
            result = "To " + myConjunctsToUpdate.values().iterator().next();
        }
        else {
            result = "To " + myLocalTheoremsToAdd.get(0);
        }

        return result;
    }

    @Override
    public void apply(PerVCProverModel m) {
        //First, do any adding
        List<LocalTheorem> mLocalTheoremList = m.getLocalTheoremList();
        LocalTheorem addedTheorem;

        Iterator<PExp> newTheoremIter = myLocalTheoremsToAdd.iterator();
        Iterator<Integer> newTheoremIndexIter =
                myLocalTheoremsToAddIndecis.iterator();
        while (newTheoremIter.hasNext()) {

            Integer index = newTheoremIndexIter.next();
            if (index == null) {
                index = mLocalTheoremList.size();
            }

            addedTheorem =
                    m.addLocalTheorem(newTheoremIter.next(),
                            new TheoremApplication(myTransformation), false,
                            index);
            myAddedConjuncts.add(addedTheorem);
            myAffectedSites.add(addedTheorem.toSite(m));
        }

        //Now, make any changes and removals
        Set<Conjunct> removed = new HashSet<Conjunct>();
        int removedIndex;
        for (Map.Entry<Conjunct, PExp> toUpdate : myConjunctsToUpdate
                .entrySet()) {

            Conjunct key = toUpdate.getKey();
            PExp value = toUpdate.getValue();
            if (value == null) {
                if (!removed.contains(key)) {
                    removed.add(key);

                    removedIndex = m.removeConjunct(key);
                    myRemovedConjuncts.add(0, key);
                    myRemovedConjunctsIndecis.add(0, removedIndex);
                }
            }
            else {
                myOriginalConjunctValues.put(key, key.getExpression());
                m.alterConjunct(key, value);
                myAffectedSites.add(key.toSite(m));
            }
        }

        //Finally, add a proof step that represents this application
        m.addProofStep(new GeneralStep(myRemovedConjuncts,
                myRemovedConjunctsIndecis, myOriginalConjunctValues,
                myAddedConjuncts, myTransformation, this,
                myInvolvedSubExpressions));
    }

    @Override
    public Set<Site> involvedSubExpressions() {
        return new HashSet<Site>(myInvolvedSubExpressions);
    }

    @Override
    public Set<Conjunct> getPrerequisiteConjuncts() {
        Set<Conjunct> result = new HashSet<Conjunct>();

        for (Site s : myInvolvedSubExpressions) {
            result.add(s.conjunct);
        }

        result.addAll(myRemovedConjuncts);
        result.addAll(myConjunctsToUpdate.keySet());

        if (myTheorem != null) {
            result.add(myTheorem);
        }

        return result;
    }

    @Override
    public Set<Conjunct> getAffectedConjuncts() {
        Set<Conjunct> result = new HashSet<Conjunct>();

        result.addAll(myAddedConjuncts);
        result.addAll(myConjunctsToUpdate.keySet());

        return result;
    }

    @Override
    public Set<Site> getAffectedSites() {
        return myAffectedSites;
    }
}
