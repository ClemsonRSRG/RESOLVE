/*
 * LeftNotRule.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules.leftrules;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.PrefixExp;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules.AbstractReductionRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules.ReductionRuleApplication;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class contains the logic for applying the {@code left not}
 * rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class LeftNotRule extends AbstractReductionRuleApplication
        implements
            ReductionRuleApplication {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application of the {@code left not}
     * rule.</p>
     *
     * @param originalSequent The original {@link Sequent} that contains
     *                        the expression to be reduced.
     * @param originalExp The {@link Exp} to be reduced.
     */
    public LeftNotRule(Sequent originalSequent, Exp originalExp) {
        super(originalSequent, originalExp);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Sequent Reduction Rule}.</p>
     *
     * @return A list of {@link Sequent Sequents} that resulted
     * from applying the rule.
     */
    @Override
    public final List<Sequent> applyRule() {
        if (myOriginalExp instanceof PrefixExp) {
            PrefixExp originalExpAsPrefixExp = (PrefixExp) myOriginalExp;
            List<Exp> newAntecedents = new ArrayList<>();
            List<Exp> newConsequents = copyExpList(myOriginalSequent.getConcequents(), null);
            for (Exp exp : myOriginalSequent.getAntecedents()) {
                if (exp.equals(originalExpAsPrefixExp)) {
                    // Add the expression inside the "not" to the consequent.
                    if (originalExpAsPrefixExp.getOperatorAsString().equals("not")) {
                        // YS: We probably already have a location detail. If not, this should
                        //     really be an alternative goal we are trying to prove, so it is
                        //     OK(?) if it doesn't have an location detail.
                        newConsequents.add(copyExp(originalExpAsPrefixExp.getArgument(), null));
                    }
                    // This must be an error!
                    else {
                        unexpectedExp();
                    }
                }
                // Don't do anything to the other expressions.
                else {
                    newAntecedents.add(exp.clone());
                }
            }

            // Construct a new sequent
            Sequent resultingSequent = new Sequent(myOriginalSequent.getLocation(),
                    newAntecedents, newConsequents);
            myResultingSequents.add(resultingSequent);

            // Indicate that this is an impacting reduction
            myIsImpactingReductionFlag = true;
        }
        // This must be an error!
        else {
            unexpectedExp();
        }

        return myResultingSequents;
    }

    /**
     * <p>This method returns a description associated with
     * the {@code Sequent Reduction Rule}.</p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "Left Not Rule";
    }

}