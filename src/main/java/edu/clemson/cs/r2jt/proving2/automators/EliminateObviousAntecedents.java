/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving2.model.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.proofsteps.RemoveAntecedentStep;
import edu.clemson.cs.r2jt.proving2.transformations.RemoveAntecedent;
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
