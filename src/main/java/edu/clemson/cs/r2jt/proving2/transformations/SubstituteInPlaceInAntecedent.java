/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.justifications.TheoremApplication;
import edu.clemson.cs.r2jt.proving2.model.AtLeastOneLocalTheoremBinder;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.InductiveAntecedentBinder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.proofsteps.ModifyAntecedentStep;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.Collection;
import java.util.Collections;
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
public class SubstituteInPlaceInAntecedent implements Transformation {

    private final BindResultToApplication BIND_RESULT_TO_APPLICATION =
            new BindResultToApplication();

    private PExp myMatchPattern;
    private List<PExp> myMatchPatternConjuncts;
    private int myMatchPatternConjunctsSize;

    private PExp myTransformationTemplate;
    private List<PExp> myTransformationTemplateConjuncts;
    private final Theorem myTheorem;

    public SubstituteInPlaceInAntecedent(Theorem t, PExp tMatchPattern,
            PExp tTransformationTemplate) {

        myTheorem = t;

        myMatchPattern = tMatchPattern;
        myMatchPatternConjuncts = tMatchPattern.splitIntoConjuncts();
        myMatchPatternConjunctsSize = myMatchPatternConjuncts.size();

        myTransformationTemplate = tTransformationTemplate;
        myTransformationTemplateConjuncts =
                tTransformationTemplate.splitIntoConjuncts();
    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        Set<Binder> binders = new HashSet<Binder>();

        if (myMatchPatternConjuncts.size() == 1) {
            binders.add(new InductiveAntecedentBinder(myMatchPattern));
        }
        else {
            Binder binder;
            for (PExp matchConjunct : myMatchPatternConjuncts) {
                binder =
                        new AtLeastOneLocalTheoremBinder(matchConjunct,
                                myMatchPatternConjunctsSize);

                binders.add(binder);
            }
        }

        Iterator<PerVCProverModel.BindResult> bindResults = m.bind(binders);

        return new LazyMappingIterator<PerVCProverModel.BindResult, Application>(
                bindResults, BIND_RESULT_TO_APPLICATION);
    }

    @Override
    public boolean couldAffectAntecedent() {
        return false;
    }

    @Override
    public boolean couldAffectConsequent() {
        return true;
    }

    @Override
    public int functionApplicationCountDelta() {
        return myTransformationTemplate.getFunctionApplications().size()
                - myMatchPattern.getFunctionApplications().size();
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        Set<PSymbol> introduced =
                myTransformationTemplate.getQuantifiedVariables();

        introduced.removeAll(myMatchPattern.getFunctionApplications());

        return !introduced.isEmpty();
    }

    @Override
    public Set<String> getPatternSymbolNames() {
        return myMatchPattern.getSymbolNames();
    }

    @Override
    public Set<String> getReplacementSymbolNames() {
        return myTransformationTemplate.getSymbolNames();
    }

    @Override
    public Transformation.Equivalence getEquivalence() {
        return Transformation.Equivalence.EQUIVALENT;
    }

    private class BindResultToApplication
            implements
                Mapping<PerVCProverModel.BindResult, Application> {

        @Override
        public Application map(PerVCProverModel.BindResult input) {
            Map<Conjunct, PExp> newValues = new HashMap<Conjunct, PExp>();
            List<PExp> newTheorems = new LinkedList<PExp>();
            List<Integer> newIndecis = new LinkedList<Integer>();

            if (myMatchPatternConjunctsSize > 1) {

                //Delete the existing things we matched
                int maxBindIndex = -1;
                int curBindIndex;
                for (Site s : input.bindSites.values()) {
                    if (s.conjunct.editable()) {
                        curBindIndex = s.getModel().getConjunctIndex(s.conjunct);
                        if (curBindIndex > maxBindIndex) {
                            maxBindIndex = curBindIndex;
                        }

                        //We're going to want to delete each of these
                        newValues.put(s.conjunct, null);
                    }
                }

                //Add the new, transformed conjuncts
                for (PExp newTheorem : myTransformationTemplateConjuncts) {
                    newTheorems.add(newTheorem
                            .substitute(input.freeVariableBindings));
                    newIndecis.add((Integer) maxBindIndex);
                    maxBindIndex++;
                }
            }
            else {
                Site bindSite = input.bindSites.values().iterator().next();

                PExp transformed =
                        myTransformationTemplate
                                .substitute(input.freeVariableBindings);
                PExp topLevelTransformed =
                        bindSite.root.exp.withSiteAltered(bindSite
                                .pathIterator(), transformed);

                List<PExp> topLevelConjuncts =
                        topLevelTransformed.splitIntoConjuncts();

                if (topLevelConjuncts.size() == 1) {
                    //Change the value in place
                    newValues.put(bindSite.conjunct, topLevelTransformed);
                }
                else {
                    //Delete the current conjunct
                    newValues.put(bindSite.conjunct, null);

                    //Add the new ones
                    int index =
                            bindSite.getModel().getConjunctIndex(
                                    bindSite.conjunct);
                    for (PExp c : topLevelConjuncts) {
                        newTheorems.add(c);
                        newIndecis.add(index);
                        index++;
                    }

                }
            }

            return new SubstituteInPlaceInAntecedentApplication(input.bindSites
                    .values(), newValues, newTheorems, newIndecis);
        }
    }

    //TODO: This application is needlessly complicated because it's intended to
    //      be further generalized into a "GeneralApplication" with a 
    //      corrseponding "GeneralProofStep" that can take care of the needs of
    //      SubstituteInPlaceInAntecedent, ExpandAntecedentBy*, and
    //      StrengthenConsequent
    private class SubstituteInPlaceInAntecedentApplication
            implements
                Application {

        //This is stuff we're initialized with
        private final Map<Conjunct, PExp> myConjunctsToUpdate;
        private final List<PExp> myLocalTheoremsToAdd;
        private final List<Integer> myLocalTheoremsToAddIndecis;
        private final Collection<Site> myInvolvedSubExpressions;

        //This is stuff that gets filled in after apply() that helps our proof
        //step perform an undo()
        private List<Conjunct> myRemovedConjuncts = new LinkedList<Conjunct>();
        private List<Integer> myRemovedConjunctsIndecis =
                new LinkedList<Integer>();

        private Map<Conjunct, PExp> myOriginalConjunctValues =
                new HashMap<Conjunct, PExp>();
        private Set<Conjunct> myAddedConjuncts = new HashSet<Conjunct>();

        private Set<Site> myAffectedSites = new HashSet<Site>();

        public SubstituteInPlaceInAntecedentApplication(
                Collection<Site> involvedSubExpressions,
                Map<Conjunct, PExp> updateConjuncts,
                List<PExp> addLocalTheorems,
                List<Integer> addLocalTheoremsIndecis) {

            if (addLocalTheorems.size() != addLocalTheoremsIndecis.size()) {
                throw new IllegalArgumentException("addLocalTheorems and "
                        + "addLocalTheoremsIndecis must have the same size.");
            }

            myConjunctsToUpdate = updateConjuncts;
            myLocalTheoremsToAdd = addLocalTheorems;
            myLocalTheoremsToAddIndecis = addLocalTheoremsIndecis;
            myInvolvedSubExpressions = involvedSubExpressions;
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
                                new TheoremApplication(
                                        SubstituteInPlaceInAntecedent.this),
                                false, index);
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
            m
                    .addProofStep(new ModifyAntecedentStep(myRemovedConjuncts,
                            myRemovedConjunctsIndecis,
                            myOriginalConjunctValues, myAddedConjuncts,
                            SubstituteInPlaceInAntecedent.this, this));
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

            result.add(myTheorem);

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

    @Override
    public String toString() {
        return "" + myMatchPattern + " = " + myTransformationTemplate;
    }
}
