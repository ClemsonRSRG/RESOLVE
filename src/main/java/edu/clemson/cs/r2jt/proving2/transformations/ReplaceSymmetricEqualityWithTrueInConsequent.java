/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.transformations;

import edu.clemson.cs.r2jt.proving.LazyMappingIterator;
import edu.clemson.cs.r2jt.proving.absyn.BindingException;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving.absyn.PSymbol;
import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.Binder;
import edu.clemson.cs.r2jt.proving2.model.Site;
import edu.clemson.cs.r2jt.proving2.proofsteps.ModifyConsequentStep;
import edu.clemson.cs.r2jt.utilities.Mapping;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

        public ReplaceSymmetricEqualityWithTrueInConsequentApplication(Site site) {
            mySite = site;
        }

        @Override
        public void apply(PerVCProverModel m) {
            m.alterSite(mySite, m.getTrue());

            m.addProofStep(new ModifyConsequentStep(mySite,
                    ReplaceSymmetricEqualityWithTrueInConsequent.this));
        }

        @Override
        public Set<Site> involvedSubExpressions() {
            return Collections.singleton(mySite);
        }

        @Override
        public String description() {
            return "To true";
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
