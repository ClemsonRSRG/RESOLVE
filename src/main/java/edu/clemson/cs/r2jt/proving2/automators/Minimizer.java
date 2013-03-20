/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.automators;

import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.proving2.Theorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.transformations.NoOpLabel;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation.Equivalence;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * <p>A <code>Minimizer</code> is an {@link Automator Automator} that takes a
 * list of transformations during its initialization, restricts them to just
 * those transformations guaranteed to strictly reduce the number of theorem
 * applications in the consequent of the prover state, then repeatedly applies
 * transformations from that set until no further transformations can be 
 * applied.</p>
 */
public class Minimizer implements Automator {

    private static final ApplyAll DUMMY_APPLIER =
            new ApplyAll(new NoOpLabel("Dummy"));

    private Collection<Transformation> myReducingTranformations =
            new LinkedList<Transformation>();

    private Iterator<Transformation> myCurrentRound;
    private ApplyAll myCurrentApplier;
    private boolean myProductiveRoundFlag = false;

    public Minimizer(ImmutableList<Theorem> theoremLibrary) {
        for (Theorem theorem : theoremLibrary) {
            for (Transformation t : theorem.getTransformations()) {
                if (t.getEquivalence().equals(Equivalence.EQUIVALENT)
                        && !t.introducesQuantifiedVariables()
                        && !t.couldAffectAntecedent()
                        && t.functionApplicationCountDelta() < 0) {
                    myReducingTranformations.add(t);
                }
            }
        }

        myCurrentRound = myReducingTranformations.iterator();
        myCurrentApplier = DUMMY_APPLIER; //This will never be applied
    }

    @Override
    public void step(Deque<Automator> stack, PerVCProverModel model) {
        myProductiveRoundFlag =
                myProductiveRoundFlag
                        || (myCurrentApplier.getApplicationCount() > 0);

        if (myCurrentRound.hasNext()) {
            myCurrentApplier = new ApplyAll(myCurrentRound.next());
            stack.push(myCurrentApplier);
        }
        else {
            if (myProductiveRoundFlag) {
                myCurrentRound = myReducingTranformations.iterator();
                myProductiveRoundFlag = false;
                myCurrentApplier = DUMMY_APPLIER; //This will never be applied
            }
            else {
                stack.pop();
            }
        }
    }
}
