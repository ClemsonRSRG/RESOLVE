/*
 * LeftAndRule.java
 * ---------------------------------
 * Copyright (c) 2018
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
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.BetweenExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.InfixExp;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules.AbstractReductionRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules.ReductionRuleApplication;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class contains the logic for applying the {@code left and}
 * rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class LeftAndRule extends AbstractReductionRuleApplication
        implements
            ReductionRuleApplication {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application of the {@code left and}
     * rule.</p>
     *
     * @param originalSequent The original {@link Sequent} that contains
     *                        the expression to be reduced.
     * @param originalExp The {@link Exp} to be reduced.
     */
    public LeftAndRule(Sequent originalSequent, Exp originalExp) {
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
        if (myOriginalExp instanceof BetweenExp) {
            BetweenExp originalExpAsBetweenExp = (BetweenExp) myOriginalExp;
            List<Exp> newAntecedents = new ArrayList<>();
            for (Exp exp : myOriginalSequent.getAntecedents()) {
                // A between expression is a collection of InfixExp joined
                // by the "and" operator. So we simply replace the original
                // expression with the inner expressions as new antecedents.
                if (exp.equals(originalExpAsBetweenExp)) {
                    newAntecedents.addAll(copyExpList(originalExpAsBetweenExp.getJoiningExps(),
                            myOriginalExp.getLocationDetailModel()));
                }
                // Don't do anything to the other expressions.
                else {
                    newAntecedents.add(exp.clone());
                }
            }

            // Construct a new sequent
            // YS: Don't pass a location detail model
            //     so we don't accidentally change a goal's
            //     location detail model
            Sequent resultingSequent = new Sequent(myOriginalSequent.getLocation(),
                    newAntecedents, copyExpList(myOriginalSequent.getConcequents(), null));
            myResultingSequents.add(resultingSequent);
        }
        else if (myOriginalExp instanceof InfixExp) {
            InfixExp originalExpAsInfixExp = (InfixExp) myOriginalExp;
            List<Exp> newAntecedents = new ArrayList<>();
            for (Exp exp : myOriginalSequent.getAntecedents()) {
                if (exp.equals(originalExpAsInfixExp)) {
                    // Replace the original "and" expression with its associated
                    // left and right expressions.
                    if (originalExpAsInfixExp.getOperatorAsString().equals("and")) {
                        newAntecedents.add(copyExp(originalExpAsInfixExp.getLeft(),
                                myOriginalExp.getLocationDetailModel()));
                        newAntecedents.add(copyExp(originalExpAsInfixExp.getRight(),
                                myOriginalExp.getLocationDetailModel()));
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
            // YS: Don't pass a location detail model
            //     so we don't accidentally change a goal's
            //     location detail model
            Sequent resultingSequent = new Sequent(myOriginalSequent.getLocation(),
                    newAntecedents, copyExpList(myOriginalSequent.getConcequents(), null));
            myResultingSequents.add(resultingSequent);
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
        return "Left And Rule";
    }

}