/*
 * LeftOrRule.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.vcgeneration.sequents.reductionrules.leftrules;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.InfixExp;
import edu.clemson.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.rsrg.vcgeneration.sequents.reductionrules.AbstractReductionRuleApplication;
import edu.clemson.rsrg.vcgeneration.sequents.reductionrules.ReductionRuleApplication;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class contains the logic for applying the {@code left or} rule.
 * </p>
 *
 * @author Yu-Shan Sun
 * 
 * @version 1.0
 */
public class LeftOrRule extends AbstractReductionRuleApplication implements ReductionRuleApplication {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a new application of the {@code left or} rule.
     * </p>
     *
     * @param originalSequent
     *            The original {@link Sequent} that contains the expression to be reduced.
     * @param originalExp
     *            The {@link Exp} to be reduced.
     */
    public LeftOrRule(Sequent originalSequent, Exp originalExp) {
        super(originalSequent, originalExp);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method applies the {@code Sequent Reduction Rule}.
     * </p>
     *
     * @return A list of {@link Sequent Sequents} that resulted from applying the rule.
     */
    @Override
    public final List<Sequent> applyRule() {
        if (myOriginalExp instanceof InfixExp) {
            InfixExp originalExpAsInfixExp = (InfixExp) myOriginalExp;
            List<Exp> newAntecedents1 = new ArrayList<>();
            List<Exp> newAntecedents2 = new ArrayList<>();
            for (Exp exp : myOriginalSequent.getAntecedents()) {
                if (exp.equals(originalExpAsInfixExp)) {
                    // Add the left and right into the different antecedent lists
                    if (originalExpAsInfixExp.getOperatorAsString().equals("or")) {
                        newAntecedents1
                                .add(copyExp(originalExpAsInfixExp.getLeft(), myOriginalExp.getLocationDetailModel()));
                        newAntecedents2
                                .add(copyExp(originalExpAsInfixExp.getRight(), myOriginalExp.getLocationDetailModel()));
                    }
                    // This must be an error!
                    else {
                        unexpectedExp();
                    }
                }
                // Don't do anything to the other expressions.
                else {
                    newAntecedents1.add(exp.clone());
                    newAntecedents2.add(exp.clone());
                }
            }

            // Construct new sequents
            // YS: We just need to prove either resultingSequent1 or resultingSequent2,
            // therefore it is just the same VC. Don't pass a location detail model
            // so we don't accidentally change a goal's location detail model
            Sequent resultingSequent1 = new Sequent(myOriginalSequent.getLocation(), newAntecedents1,
                    copyExpList(myOriginalSequent.getConcequents(), null));
            myResultingSequents.add(resultingSequent1);
            Sequent resultingSequent2 = new Sequent(myOriginalSequent.getLocation(), newAntecedents2,
                    copyExpList(myOriginalSequent.getConcequents(), null));
            myResultingSequents.add(resultingSequent2);

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
     * <p>
     * This method returns a description associated with the {@code Sequent Reduction Rule}.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "Left Or Rule";
    }

}
