/*
 * EliminateObviousAntecedents.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.automators;

import edu.clemson.cs.r2jt.rewriteprover.model.LocalTheorem;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.transformations.RemoveAntecedent;
import java.util.Deque;
import java.util.Iterator;

/**
 *
 * @author hamptos
 */
public class EliminateObviousAntecedents implements Automator {

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        Iterator<LocalTheorem> ts = model.getLocalTheoremList().iterator();
        LocalTheorem toRemove = null;

        LocalTheorem curTheorem;
        while (toRemove == null && ts.hasNext()) {
            curTheorem = ts.next();

            if (curTheorem.getAssertion().isObviouslyTrue()) {
                toRemove = curTheorem;
            }
        }

        if (toRemove == null) {
            stack.pop();
        }
        else {
            new RemoveAntecedent(model, toRemove).getApplications(model).next()
                    .apply(model);
        }
    }
}
