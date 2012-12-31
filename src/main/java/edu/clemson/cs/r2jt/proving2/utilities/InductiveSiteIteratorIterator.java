/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
