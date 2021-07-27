/*
 * GeneralApplication.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.applications;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.justifications.TheoremApplication;
import edu.clemson.cs.r2jt.rewriteprover.model.Conjunct;
import edu.clemson.cs.r2jt.rewriteprover.model.LocalTheorem;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.model.Site;
import edu.clemson.cs.r2jt.rewriteprover.model.Theorem;
import edu.clemson.cs.r2jt.rewriteprover.proofsteps.GeneralStep;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
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

    // This is stuff we're initialized with
    private final Map<Conjunct, PExp> myConjunctsToUpdate;
    private final List<PExp> myLocalTheoremsToAdd;
    private final List<Integer> myLocalTheoremsToAddIndecis;
    private final Collection<Site> myInvolvedSubExpressions;

    // This is stuff that gets filled in after apply() that helps our proof
    // step perform an undo()
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
        // First, do any adding
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

            addedTheorem = m.addLocalTheorem(newTheoremIter.next(),
                    new TheoremApplication(myTransformation), false, index);
            myAddedConjuncts.add(addedTheorem);
            myAffectedSites.add(addedTheorem.toSite(m));
        }

        // Now, make any changes and removals
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

        // Finally, add a proof step that represents this application
        m.addProofStep(
                new GeneralStep(myRemovedConjuncts, myRemovedConjunctsIndecis,
                        myOriginalConjunctValues, myAddedConjuncts,
                        myTransformation, this, myInvolvedSubExpressions));
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
