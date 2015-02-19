/**
 * AtLeastOneLocalTheoremBinder.java
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
package edu.clemson.cs.r2jt.rewriteprover.model;

import edu.clemson.cs.r2jt.rewriteprover.iterators.ChainingIterator;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel.AbstractBinder;
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