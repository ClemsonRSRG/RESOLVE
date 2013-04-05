/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.Conjunct;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.proofsteps.ModifyConsequentStep;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class ReplaceSymmetricEqualityWithTrueInConsequent
        implements
            Transformation {

    public static final ReplaceSymmetricEqualityWithTrueInConsequent INSTANCE =
            new ReplaceSymmetricEqualityWithTrueInConsequent();

    private final SiteToApplication SITE_TO_APPLICATION =
            new SiteToApplication();

    @Override
    public Iterator<Application> getApplications(PerVCProverModel m) {
        return new LazyMappingIterator<Site, Application>(
                new SymmetricEqualityIterator(m
                        .topLevelConsequentSiteIterator()), SITE_TO_APPLICATION);
    }

    @Override
    public String toString() {
        return "Symmetric equality is true";
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
        return -2;
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
        return Collections.singleton("true");
    }

    @Override
    public Equivalence getEquivalence() {
        return Equivalence.EQUIVALENT;
    }

    private class SiteToApplication implements Mapping<Site, Application> {

        @Override
        public Application map(Site input) {
            return new ReplaceSymmetricEqualityWithTrueInConsequentApplication(
                    input);
        }
    }

    private class ReplaceSymmetricEqualityWithTrueInConsequentApplication
            implements
                Application {

        private final Site mySite;
        private Site myFinalSite;

        public ReplaceSymmetricEqualityWithTrueInConsequentApplication(Site site) {
            mySite = site;
        }

        @Override
        public void apply(PerVCProverModel m) {
            m.alterSite(mySite, m.getTrue());

            myFinalSite =
                    new Site(m, mySite.conjunct, mySite.path, m.getTrue());

            m.addProofStep(new ModifyConsequentStep(mySite, myFinalSite,
                    ReplaceSymmetricEqualityWithTrueInConsequent.this, this));
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            return Collections.singleton(mySite);
        }

        @Override
        public String description() {
            return "To true";
        }

        @Override
        public Set<Conjunct> getPrerequisiteConjuncts() {
            return Collections.singleton(mySite.conjunct);
        }

        @Override
        public Set<Conjunct> getAffectedConjuncts() {
            return Collections.singleton(mySite.conjunct);
        }

        @Override
        public Set<Site> getAffectedSites() {
            return Collections.<Site> singleton(myFinalSite);
        }
    }

    private class SymmetricEqualityIterator implements Iterator<Site> {

        private Iterator<Site> myBaseSites;

        private Site myNextReturn;

        public SymmetricEqualityIterator(Iterator<Site> baseSites) {
            myBaseSites = baseSites;
            setUpNext();
        }

        private void setUpNext() {
            myNextReturn = null;

            Site candidate;
            PExp candidateExp;
            while (myBaseSites.hasNext() && myNextReturn == null) {
                candidate = myBaseSites.next();
                candidateExp = candidate.exp;

                if (candidateExp instanceof PSymbol) {
                    PSymbol candidateSymbol = (PSymbol) candidateExp;

                    if (candidateSymbol.name.equals("=")) {
                        PExp left = candidateSymbol.arguments.get(0);
                        PExp right = candidateSymbol.arguments.get(1);

                        if (left.equals(right)) {
                            myNextReturn = candidate;
                        }
                    }
                }
            }
        }

        @Override
        public boolean hasNext() {
            return (myNextReturn != null);
        }

        @Override
        public Site next() {
            if (myNextReturn == null) {
                throw new NoSuchElementException();
            }

            Site result = myNextReturn;

            setUpNext();

            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }
}
