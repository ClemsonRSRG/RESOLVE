/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving2.applications.Application;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.EliminateTrueConjunctInConsequent;
import edu.clemson.cs.r2jt.proving2.transformations.ReplaceSymmetricEqualityWithTrueInConsequent;
import java.util.Deque;
import java.util.Iterator;

/**
 * <p>The <code>Simplify</code> automator turns symmetric equalities into
 * "true" and eliminates true conjuncts until no more work can be done.</p>
 */
public class Simplify implements Automator {

    public static final Simplify INSTANCE = new Simplify();

    private Simplify() {

    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        //Turn symmetric equalities into true
        Iterator<Application> symmetricEqualities =
                ReplaceSymmetricEqualityWithTrueInConsequent.INSTANCE
                        .getApplications(model);

        if (symmetricEqualities.hasNext()) {
            symmetricEqualities.next().apply(model);
        }
        else {
            Iterator<Application> trueConjuncts =
                    EliminateTrueConjunctInConsequent.INSTANCE
                            .getApplications(model);

            if (trueConjuncts.hasNext()) {
                trueConjuncts.next().apply(model);
            }
            else {
                stack.pop();
            }
        }
    }
}
