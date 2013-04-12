/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.model;

import edu.clemson.cs.r2jt.proving.ChainingIterator;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel.AbstractBinder;
import java.util.Iterator;
import java.util.List;

public class AtLeastOneLocalTheoremBinder extends AbstractBinder {

    private int myTotalBindingCount;

    public AtLeastOneLocalTheoremBinder(PExp pattern, int totalBindings) {
        super(pattern);
        myTotalBindingCount = totalBindings;
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
                        (boundSitesSoFarIter.next().conjunct instanceof LocalTheorem);
            }
        }

        if (includeGlobal) {
            result =
                    new ChainingIterator<Site>(result, m
                            .topLevelGlobalTheoremsIterator());
        }

        return result;
    }
}