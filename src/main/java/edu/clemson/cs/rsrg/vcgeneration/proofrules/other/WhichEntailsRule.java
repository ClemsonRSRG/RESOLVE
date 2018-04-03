/*
 * WhichEntailsRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.other;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.absyn.statements.ConfirmStmt;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for the {@code Which_Entails}
 * rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class WhichEntailsRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The assertion clause where we found the {@code Which_Entails}.</p> */
    private final AssertionClause myClause;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application for a {@code Which_Entails}
     * inside some {@link AssertionClause}.</p>
     *
     * @param clause The assertion clause where we found
     *               the {@code Which_Entails}.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param context The verification context that contains all
     *                the information we have collected so far.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public WhichEntailsRule(AssertionClause clause, AssertiveCodeBlock block,
            VerificationContext context, STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myClause = clause;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        // Check to see if we have a which_entails expression
        if (myClause.getWhichEntailsExp() != null) {
            // Use the first part of the assertion clause as what we can
            // assume to be true and add the location detail associated with it.
            Exp assertionExp = myClause.getAssertionExp().clone();
            assertionExp.setLocationDetailModel(new LocationDetailModel(
                    assertionExp.getLocation().clone(), assertionExp
                            .getLocation().clone(), myClause.getClauseType()
                            .toString()
                            + " Clause"));
            myCurrentAssertiveCodeBlock.addStatement(new AssumeStmt(myClause
                    .getAssertionExp().getLocation().clone(), assertionExp,
                    false));

            // Confirm the which_entails expression and add the location detail associated with it.
            Exp whichEntailsExp = myClause.getWhichEntailsExp().clone();
            whichEntailsExp.setLocationDetailModel(new LocationDetailModel(
                    whichEntailsExp.getLocation().clone(), whichEntailsExp
                            .getLocation().clone(),
                    "Which_Entails Expression Located at "
                            + myClause.getLocation()));
            myCurrentAssertiveCodeBlock.addStatement(new ConfirmStmt(myClause
                    .getWhichEntailsExp().getLocation().clone(),
                    whichEntailsExp, false));

            // Add the different details to the various different output models
            ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
            stepModel.add("proofRuleName", getRuleDescription()).add(
                    "currentStateOfBlock", myCurrentAssertiveCodeBlock);
            myBlockModel.add("vcGenSteps", stepModel.render());
        }
    }

    /**
     * <p>This method returns a description associated with
     * the {@code Proof Rule}.</p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "Which_Entails Rule";
    }

}