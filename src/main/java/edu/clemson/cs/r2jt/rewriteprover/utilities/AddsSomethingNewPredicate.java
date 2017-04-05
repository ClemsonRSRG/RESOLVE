/*
 * AddsSomethingNewPredicate.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.utilities;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.AutomatedProver;
import edu.clemson.cs.r2jt.rewriteprover.Utilities;
import edu.clemson.cs.r2jt.rewriteprover.model.Conjunct;
import edu.clemson.cs.r2jt.rewriteprover.model.LocalTheorem;
import edu.clemson.cs.r2jt.rewriteprover.model.PerVCProverModel;
import edu.clemson.cs.r2jt.rewriteprover.proofsteps.GeneralStep;
import edu.clemson.cs.r2jt.rewriteprover.proofsteps.IntroduceLocalTheoremStep;
import edu.clemson.cs.r2jt.rewriteprover.proofsteps.ProofStep;
import edu.clemson.cs.r2jt.rewriteprover.transformations.Transformation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// TODO : this comment is entirely out of date and the class should be renamed
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
    private final Set<String> myVariableSymbols;

    public AddsSomethingNewPredicate(PerVCProverModel model,
            Set<String> variableSymbols) {
        myModel = model;
        myVariableSymbols = variableSymbols;
    }

    @Override
    public boolean test(ProofStep t) {
        boolean result =
                (t instanceof IntroduceLocalTheoremStep)
                        || (t instanceof GeneralStep);

        if (result) {
            //Any development that doesn't tell us something about at least 
            //one of the variable symbols in the consequent of the VC or
            //doesn't introduce at least one new theorem should be rolled 
            //back
            Set<String> finalSymbolNames = new HashSet<String>();

            boolean somethingNew = false;
            for (Conjunct c : t.getAffectedConjuncts()) {
                finalSymbolNames.addAll(c.getExpression().getSymbolNames());

                somethingNew =
                        somethingNew || appearsOnce(c.getExpression(), myModel);
            }

            result =
                    somethingNew
                            && (!AutomatedProver.H_ONLY_DEVELOP_RELEVANT_TERMS || Utilities
                                    .containsAny(finalSymbolNames,
                                            myVariableSymbols));

            if (result) {
                Transformation tTransformation = t.getTransformation();

                //Any development that reduces the function count should be
                //accepted
                result = tTransformation.functionApplicationCountDelta() < 0;

                if (!result && AutomatedProver.H_ENCOURAGE_ANTECEDENT_DIVERSITY) {
                    //Any substitution that doesn't eliminate at least
                    //one symbol should be rolled back
                    Set<Conjunct> originalTheorems = t.getBoundConjuncts();
                    Set<String> originalSymbolNames = new HashSet<String>();
                    for (Conjunct ot : originalTheorems) {
                        originalSymbolNames.addAll(ot.getExpression()
                                .getSymbolNames());
                    }

                    originalSymbolNames.removeAll(finalSymbolNames);

                    result = !originalSymbolNames.isEmpty();
                }
            }
        }
        else {
            throw new RuntimeException(
                    "Expecting a local theorem introduction?  Got: "
                            + t.getClass());
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
