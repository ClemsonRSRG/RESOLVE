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
package edu.clemson.cs.r2jt.proving2.utilities;

import edu.clemson.cs.r2jt.proving.DummyIterator;
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
