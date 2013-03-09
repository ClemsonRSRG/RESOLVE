/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.proving2.utilities;

import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.proving2.LocalTheorem;
import edu.clemson.cs.r2jt.proving2.Theorem;
import edu.clemson.cs.r2jt.proving2.model.PerVCProverModel;
import edu.clemson.cs.r2jt.proving2.proofsteps.IntroduceLocalTheoremStep;
import edu.clemson.cs.r2jt.proving2.proofsteps.ProofStep;
import edu.clemson.cs.r2jt.proving2.transformations.ExpandAntecedentByImplication;
import edu.clemson.cs.r2jt.proving2.transformations.ExpandAntecedentBySubstitution;
import edu.clemson.cs.r2jt.proving2.transformations.Transformation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>This predicate is used during antecedent development to ensure that 1)
 * duplicate antecedents are not added, and 2) that we don't endlessly add "+ 0"
 * at the end of integer valued things, or "o Empty_String" at the end of string
 * valued things.</p>
 * 
 * <p>The exact criteria are this: returns true if 1) the introduced expression
 * appears only once in the antecedents and one of 2a) the transformer is
 * an ExpandAntecedentByImplication or 2b) the transformed expression has the 
 * same or fewer number of function calls as the original or 2c) the transformed
 * expression introduces some new constant value or function.</p>
 */
public class AddsSomethingNewPredicate implements Predicate<ProofStep> {

    private final PerVCProverModel myModel;

    public AddsSomethingNewPredicate(PerVCProverModel model) {
        myModel = model;
    }

    @Override
    public boolean test(ProofStep t) {
        boolean result = (t instanceof IntroduceLocalTheoremStep);

        if (result) {
            IntroduceLocalTheoremStep tLT = (IntroduceLocalTheoremStep) t;

            result =
                    appearsOnce(tLT.getIntroducedTheorem().getAssertion(),
                            myModel);

            if (result) {
                Transformation transformation = tLT.getTransformation();

                if (transformation instanceof ExpandAntecedentBySubstitution
                        || transformation instanceof ExpandAntecedentByImplication) {
                    result =
                            transformation.functionApplicationCountDelta() <= 0;

                    if (!result) {
                        Set<Theorem> origialTheorems =
                                tLT.getPrerequisiteTheorems();
                        Set<String> originalSymbolNames = new HashSet<String>();
                        for (Theorem ot : origialTheorems) {
                            originalSymbolNames.addAll(ot.getAssertion()
                                    .getSymbolNames());
                        }

                        Set<String> introduced = new HashSet<String>(
                                transformation.getReplacementSymbolNames());

                        introduced.removeAll(originalSymbolNames);

                        result = !introduced.isEmpty();
                    }
                }
                else {
                    //Not prepared to deal with other kinds of transformations
                    throw new RuntimeException();
                }
            }
        }
        else {
            throw new RuntimeException(
                    "Expecting a local theorem introduction?");
        }

        return result;
    }

    private static boolean appearsOnce(PExp exp, PerVCProverModel m) {
        boolean found = false;
        boolean result = true;

        Iterator<LocalTheorem> localTheorems =
                m.getLocalTheoremList().iterator();

        PExp localTheorem;
        boolean equal;
        while (result && localTheorems.hasNext()) {
            localTheorem = localTheorems.next().getAssertion();
            equal = localTheorem.equals(exp);

            if (found) {
                result = !equal;
            }
            else {
                found = equal;
            }
        }

        if (!found) {
            throw new RuntimeException("Snuh?");
        }

        return result;
    }

}
