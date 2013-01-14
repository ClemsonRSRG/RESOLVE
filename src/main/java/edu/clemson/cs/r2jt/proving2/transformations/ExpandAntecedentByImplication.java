/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.ChainingIterator;
import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.Utilities;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.justifications.TheoremApplication;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.BindResult;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.proofsteps.IntroduceLocalTheoremStep;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>A transformation that applies "A and B and C implies D" by first seeing
 * if all antecedents (A, B, and C) can be directly matched against givens or
 * global theorems, and if so, adding a new given matching the form of D, with 
 * variables appropriately replaced based on the matching of the antecedents.
 * </p>
 * 
 * <p><strong>Random Quirk:</strong> Because the intention of this class is to
 * extend available assumptions based on local contextual data, at least one
 * variable binding when matching the theorem antecedent against known facts
 * must come from the prover state's antecedent.  That is, the prover state will
 * not be extended with applications of this conditional theorem entirely to 
 * global facts--those "extensions" should themselves be listed as global 
 * theorems.</p>
 * 
 * <p><strong>Example:</strong>  Given the theorem 
 * <code>|S| &gt; 0 implies S /= Empty_String</code>, consider the following
 * VC:</p>
 * 
 * <pre>
 * |T| > 0
 * --->
 * |S o T| > |S|
 * </pre>
 * 
 * One (the only) application of this transformation would be:
 * 
 * <pre>
 * |T| > 0 and
 * T /= Empty_String
 * --->
 * |S o T| > |S|
 * </pre>
 */
public class ExpandAntecedentByImplication implements Transformation {

    private final BindResultToApplication BIND_RESULT_TO_APPLICATION =
            new BindResultToApplication();

    private final List<PExp> myAntecedents;
    private final int myAntecedentsSize;
    private final PExp myConsequent;

    public ExpandAntecedentByImplication(List<PExp> localTheorems,
            PExp consequent) {
        myAntecedents = localTheorems;
        myAntecedentsSize = myAntecedents.size();
        myConsequent = consequent;
    }

    @Override
    public String toString() {
        return Utilities.conjunctListToString(myAntecedents) + " implies "
                + myConsequent;
    }

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        Set<Binder> binders = new HashSet<Binder>();
        for (PExp a : myAntecedents) {
            binders.add(new QuirkyBinder(a, myAntecedentsSize));
        }

        return new LazyMappingIterator(m.bind(binders),
                BIND_RESULT_TO_APPLICATION);
    }

    @Override
    public boolean couldAffectAntecedent() {
        return true;
    }

    @Override
    public boolean couldAffectConsequent() {
        return false;
    }

    @Override
    public int functionApplicationCountDelta() {
        int antecedentFunctionCount = 0;
        for (PExp a : myAntecedents) {
            antecedentFunctionCount += a.getFunctionApplications().size();
        }

        int consequentFunctionCount =
                myConsequent.getFunctionApplications().size();

        return consequentFunctionCount - antecedentFunctionCount;
    }

    @Override
    public boolean introducesQuantifiedVariables() {
        Set<PSymbol> antecedentQuantifiedVariables = new HashSet<PSymbol>();
        for (PExp a : myAntecedents) {
            antecedentQuantifiedVariables.addAll(a.getQuantifiedVariables());
        }

        Set<PSymbol> consequentQuantifiedVariables =
                myConsequent.getQuantifiedVariables();

        consequentQuantifiedVariables.removeAll(antecedentQuantifiedVariables);

        return !consequentQuantifiedVariables.isEmpty();
    }

    @Override
    public Set<String> getPatternSymbolNames() {
        Set<String> antecedentSymbolNames = new HashSet<String>();

        for (PExp a : myAntecedents) {
            antecedentSymbolNames.addAll(a.getSymbolNames());
        }

        return antecedentSymbolNames;
    }

    @Override
    public Set<String> getReplacementSymbolNames() {
        return myConsequent.getSymbolNames();
    }

    @Override
    public Equivalence getEquivalence() {
        return Equivalence.WEAKER;
    }

    public class QuirkyBinder implements PerVCProverModel.Binder {

        private int myTotalBindingCount;
        private PExp myPattern;

        public QuirkyBinder(PExp pattern, int totalBindings) {
            myTotalBindingCount = totalBindings;
            myPattern = pattern;
        }

        @Override
        public Iterator<Site> getInterestingSiteVisitor(PerVCProverModel m,
                List<Site> boundSitesSoFar) {
            Iterator<Site> result = m.topLevelAntecedentSiteIterator();

            boolean includeGlobal = true;
            if (boundSitesSoFar.size() == (myTotalBindingCount - 1)) {
                //We are the last binding.  If all other bindings are to global
                //theorems, then we must bind to something local
                includeGlobal = false;
                Iterator<Site> boundSitesSoFarIter = boundSitesSoFar.iterator();
                while (!includeGlobal && boundSitesSoFarIter.hasNext()) {
                    includeGlobal =
                            (boundSitesSoFarIter.next().section
                                    .equals(Site.Section.ANTECEDENTS));
                }
            }

            if (includeGlobal) {
                result =
                        new ChainingIterator<Site>(result, m
                                .topLevelGlobalTheoremsIterator());
            }

            return result;
        }

        @Override
        public Map<PExp, PExp> considerSite(Site s,
                Map<PExp, PExp> assumedBindings) throws BindingException {
            return myPattern.substitute(assumedBindings).bindTo(s.exp);
        }

        @Override
        public String toString() {
            return "" + myPattern;
        }
    }

    public class BindResultToApplication
            implements
                Mapping<BindResult, Application> {

        @Override
        public Application map(BindResult input) {
            return new ExpandAntecedentByImplicationApplication(
                    input.freeVariableBindings, input.bindSites.values());
        }
    }

    private class ExpandAntecedentByImplicationApplication
            implements
                Application {

        private Map<PExp, PExp> myBindings;
        private Collection<Site> myBindSites;

        public ExpandAntecedentByImplicationApplication(
                Map<PExp, PExp> bindings, Collection<Site> bindSites) {
            myBindings = bindings;
            myBindSites = bindSites;
        }

        @Override
        public String description() {
            return "Add " + myConsequent.substitute(myBindings);
        }

        @Override
        public void apply(PerVCProverModel m) {
            List<PExp> newAntecedents =
                    myConsequent.substitute(myBindings).splitIntoConjuncts();

            for (PExp a : newAntecedents) {
                LocalTheorem t =
                        m.addLocalTheorem(a, new TheoremApplication(
                                ExpandAntecedentByImplication.this), false);
                m.addProofStep(new IntroduceLocalTheoremStep(t,
                        ExpandAntecedentByImplication.this));
            }
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            Set<Site> result = new HashSet<Site>();

            for (Site s : myBindSites) {
                if (s.section.equals(Site.Section.ANTECEDENTS)) {
                    result.add(s);
                }
            }

            return result;
        }
    }
}