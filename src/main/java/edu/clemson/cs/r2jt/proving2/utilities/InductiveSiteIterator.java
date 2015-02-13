/**
 * InductiveSiteIterator.java
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
package edu.clemson.cs.r2jt.proving2.utilities;

import edu.clemson.cs.r2jt.proving2.iterators.DummyIterator;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.model.Site;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Wraps a Site, and iterates over every sub-site, including the site itself.
 * The order in which it does this is undefined.</p>
 */
public class InductiveSiteIterator implements Iterator<Site> {

    private final Site myRootSite;
    private final Iterator<Site> myRootSubExpressions;
    private Site myCurrentSubExpression;

    private Iterator<Site> myCurrentInductiveSubExpressionIterator;

    private Site myNextReturn;

    private boolean myNoMoreFlag = false;

    public InductiveSiteIterator(Site s) {
        myRootSite = s;
        myRootSubExpressions =
                new SiteConstructor(s, s.exp.getSubExpressions().iterator());

        if (myRootSubExpressions.hasNext()) {
            myCurrentSubExpression = myRootSubExpressions.next();
            myCurrentInductiveSubExpressionIterator =
                    new InductiveSiteIterator(myCurrentSubExpression);
        }
        else {
            myCurrentInductiveSubExpressionIterator =
                    DummyIterator
                            .getInstance(myCurrentInductiveSubExpressionIterator);
        }

        setUpNext();
    }

    private void setUpNext() {
        if (myNoMoreFlag) {
            myNextReturn = null;
        }
        else if (myCurrentInductiveSubExpressionIterator.hasNext()) {
            myNextReturn = myCurrentInductiveSubExpressionIterator.next();
        }
        else {
            if (myRootSubExpressions.hasNext()) {
                myCurrentSubExpression = myRootSubExpressions.next();
                myCurrentInductiveSubExpressionIterator =
                        new InductiveSiteIterator(myCurrentSubExpression);
                myNextReturn = myCurrentInductiveSubExpressionIterator.next();
            }
            else {
                myNoMoreFlag = true;
                myNextReturn = myRootSite;
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

    private class SiteConstructor implements Iterator<Site> {

        private final Site myBasis;
        private final Iterator<PExp> myPExps;
        private int mySiblingIndex;

        public SiteConstructor(Site basis, Iterator<PExp> p) {
            myBasis = basis;
            myPExps = p;
            mySiblingIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return myPExps.hasNext();
        }

        @Override
        public Site next() {
            Site result =
                    new Site(myBasis.getModel(), myBasis.conjunct, myBasis.path
                            .appended(mySiblingIndex), myPExps.next());

            mySiblingIndex++;

            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
