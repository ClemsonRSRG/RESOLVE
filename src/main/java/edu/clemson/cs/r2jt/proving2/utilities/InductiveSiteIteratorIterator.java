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
import edu.clemson.cs.r2jt.proving2.model.Site;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>Nope, that's not a mistype.  This class takes an iterator over top level
 * sites and decorates it to inductively visit each top-level site's subsites
 * as well.</p>
 */
public class InductiveSiteIteratorIterator implements Iterator<Site> {

    private final Iterator<Site> myTopLevelSites;
    private Iterator<Site> myCurInductiveIterator;

    private Site myNextReturn;

    public InductiveSiteIteratorIterator(Iterator<Site> topLevelSites) {
        myTopLevelSites = topLevelSites;
        myCurInductiveIterator =
                DummyIterator.getInstance(myCurInductiveIterator);

        setUpNext();
    }

    public void setUpNext() {
        if (myCurInductiveIterator.hasNext()) {
            myNextReturn = myCurInductiveIterator.next();
        }
        else {
            if (myTopLevelSites.hasNext()) {
                myCurInductiveIterator =
                        new InductiveSiteIterator(myTopLevelSites.next());
                myNextReturn = myCurInductiveIterator.next();
            }
            else {
                myNextReturn = null;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
