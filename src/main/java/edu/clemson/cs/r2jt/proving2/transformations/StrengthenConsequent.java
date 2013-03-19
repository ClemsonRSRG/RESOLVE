/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.Utilities;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.TopLevelConsequentBinder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.proofsteps.StrengthenConsequentStep;
import edu.clemson.cs.r2jt.proving2.proofsteps.RemoveConsequentStep;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>This transformation applies an implication to the consequent.  Assuming
 * we have a theorem that reads "A and B implies C", we look for a consequent
 * matching C and replace it with A and B.  Note that this could be a strictly
 * stronger statement--we could take the VC from a provable place to a 
 * not-provable place.</p>
 */
public class StrengthenConsequent implements Transformation {

    private final BindResultToApplication BIND_RESULT_TO_APPLICATION =
            new BindResultToApplication();

    private final List<PExp> myAntecedents;
    private final List<PExp> myConsequents;

    public StrengthenConsequent(List<PExp> theoremAntecedents,
            List<PExp> theoremConsequent) {

        myAntecedents = theoremAntecedents;
        myConsequents = theoremConsequent;
    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        Set<PerVCProverModel.Binder> binders =
                new HashSet<PerVCProverModel.Binder>();
        for (PExp c : myConsequents) {
            binders.add(new TopLevelConsequentBinder(c));
        }

        return new LazyMappingIterator(m.bind(binders),
                BIND_RESULT_TO_APPLICATION);
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
        int antecedentFunctionCount = 0;
        for (PExp a : myAntecedents) {
            antecedentFunctionCount += a.getFunctionApplications().size();
        }

        int consequentFunctionCount = 0;
        for (PExp c : myConsequents) {
            consequentFunctionCount += c.getFunctionApplications().size();
        }

        return antecedentFunctionCount - consequentFunctionCount;
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        Set<PSymbol> antecedentQuantifiedVariables = new HashSet<PSymbol>();
        for (PExp a : myAntecedents) {
            antecedentQuantifiedVariables.addAll(a.getQuantifiedVariables());
        }

        Set<PSymbol> consequentQuantifiedVariables = new HashSet<PSymbol>();
        for (PExp c : myConsequents) {
            consequentQuantifiedVariables.addAll(c.getQuantifiedVariables());
        }

        antecedentQuantifiedVariables.removeAll(consequentQuantifiedVariables);

        return !antecedentQuantifiedVariables.isEmpty();
    }

    @Override
    public Set<String> getPatternSymbolNames() {
        Set<String> consequentSymbolNames = new HashSet<String>();

        for (PExp c : myConsequents) {
            consequentSymbolNames.addAll(c.getSymbolNames());
        }

        return consequentSymbolNames;
    }

    @Override
    public Set<String> getReplacementSymbolNames() {
        Set<String> antecedentSymbolNames = new HashSet<String>();

        for (PExp a : myAntecedents) {
            antecedentSymbolNames.addAll(a.getSymbolNames());
        }

        return antecedentSymbolNames;
    }

    @Override
    public Equivalence getEquivalence() {
        return Equivalence.STRONGER;
    }

    private class BindResultToApplication
            implements
                Mapping<PerVCProverModel.BindResult, Application> {

        @Override
        public Application map(PerVCProverModel.BindResult input) {
            return new StrengthenConsequentApplication(
                    input.freeVariableBindings, input.bindSites.values());
        }
    }

    @Override
    public String toString() {
        return "Strengthen to " + Utilities.conjunctListToString(myAntecedents);
    }

    private class StrengthenConsequentApplication implements Application {

        private final Map<PExp, PExp> myBindings;
        private final int[] myBindSiteIndecis;
        private final Collection<Site> myBindSites;

        public StrengthenConsequentApplication(Map<PExp, PExp> bindings,
                Collection<Site> bindSites) {
            myBindings = bindings;
            myBindSites = bindSites;

            myBindSiteIndecis = new int[bindSites.size()];
            Iterator<Site> siteIter = bindSites.iterator();
            for (int i = 0; i < myBindSiteIndecis.length; i++) {
                myBindSiteIndecis[i] = siteIter.next().index;
            }
            Arrays.sort(myBindSiteIndecis);
        }

        @Override
        public String description() {
            return "Strengthen to "
                    + Utilities.conjunctListToString(myAntecedents);
        }

        @Override
        public void apply(PerVCProverModel m) {

            //We want to start removing at the end so we aren't adjusting 
            //indecis
            for (int i = myBindSiteIndecis.length - 1; i >= 0; i--) {
                m.removeConsequent(myBindSiteIndecis[i]);
            }

            for (PExp a : myAntecedents) {
                m.getConsequentList().size();
                m.addConsequent(a.substitute(myBindings));
            }

            m.addProofStep(new StrengthenConsequentStep(myBindSites,
                    myAntecedents.size(), StrengthenConsequent.this));
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            Set<Site> result = new HashSet<Site>();

            for (Site s : myBindSites) {
                if (s.section.equals(Site.Section.CONSEQUENTS)) {
                    result.add(s);
                }
            }

            return result;
        }
    }
}
