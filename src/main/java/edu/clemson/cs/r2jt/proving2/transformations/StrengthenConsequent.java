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
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.Consequent;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.TopLevelConsequentBinder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.model.Theorem;
import edu.clemson.cs.r2jt.proving2.proofsteps.StrengthenConsequentStep;
import edu.clemson.cs.r2jt.utilities.Mapping;
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
    private final Theorem myTheorem;

    public StrengthenConsequent(Theorem t, List<PExp> tTheoremAntecedents,
            List<PExp> tTheoremConsequent) {

        myTheorem = t;
        myAntecedents = tTheoremAntecedents;
        myConsequents = tTheoremConsequent;
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

    @Override
    public String getKey() {
        return myTheorem.getAssertion() + " " + this.getClass().getName();
    }

    private class StrengthenConsequentApplication implements Application {

        private final Map<PExp, PExp> myBindings;
        private final Collection<Site> myBindSites;

        private final Set<Conjunct> myNewConsequents = new HashSet<Conjunct>();
        private final Set<Site> myNewSites = new HashSet<Site>();

        private final Set<Conjunct> myOldConsequents = new HashSet<Conjunct>();

        public StrengthenConsequentApplication(Map<PExp, PExp> bindings,
                Collection<Site> bindSites) {
            myBindings = bindings;
            myBindSites = bindSites;

            for (Site s : bindSites) {
                myOldConsequents.add(s.conjunct);
            }
        }

        @Override
        public String description() {
            return "Strengthen to "
                    + Utilities.conjunctListToString(myAntecedents);
        }

        @Override
        public void apply(PerVCProverModel m) {

            List<Conjunct> removedConjuncts = new LinkedList<Conjunct>();
            List<Integer> removedConjunctsIndex = new LinkedList<Integer>();
            Set<Conjunct> alreadyRemoved = new HashSet<Conjunct>();
            for (Site s : myBindSites) {
                if (!alreadyRemoved.contains(s.conjunct)) {
                    alreadyRemoved.add(s.conjunct);

                    removedConjuncts.add(s.conjunct);
                    removedConjunctsIndex.add(m.getConjunctIndex(s.conjunct));

                    m.removeConjunct(s.conjunct);
                }
            }

            Conjunct c;
            for (PExp a : myAntecedents) {
                c = m.addConsequent(a.substitute(myBindings).flipQuantifiers());
                myNewConsequents.add(c);
                myNewSites.add(c.toSite(m));
            }

            m.addProofStep(new StrengthenConsequentStep(removedConjuncts,
                    removedConjunctsIndex, myNewConsequents,
                    StrengthenConsequent.this, this, myBindSites));
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            Set<Site> result = new HashSet<Site>();

            for (Site s : myBindSites) {
                if (s.conjunct instanceof Consequent) {
                    result.add(s);
                }
            }

            return result;
        }

        @Override
        public Set<Conjunct> getPrerequisiteConjuncts() {
            Set<Conjunct> result = new HashSet<Conjunct>(myOldConsequents);
            result.add(myTheorem);

            return result;
        }

        @Override
        public Set<Conjunct> getAffectedConjuncts() {
            return myNewConsequents;
        }

        @Override
        public Set<Site> getAffectedSites() {
            return myNewSites;
        }
    }
}
