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

    @Override
    public String getKey() {
        return this.getClass().getName();
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
                    ReplaceSymmetricEqualityWithTrueInConsequent.this, this,
                    Collections.singleton(mySite)));
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
