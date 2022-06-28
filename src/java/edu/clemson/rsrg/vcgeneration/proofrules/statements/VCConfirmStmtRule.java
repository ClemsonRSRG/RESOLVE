/*
 * VCConfirmStmtRule.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.vcgeneration.proofrules.statements;

import edu.clemson.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationCondition;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationContext;
import edu.clemson.rsrg.vcgeneration.utilities.helperstmts.VCConfirmStmt;
import java.util.List;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>
 * This class contains the logic for replacing the {@link VerificationCondition} in the current
 * {@link AssertiveCodeBlock} with those stored inside a {@link VCConfirmStmt}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class VCConfirmStmtRule extends AbstractProofRuleApplication implements ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The {@link VCConfirmStmt} we are applying the rule to.
     * </p>
     */
    private final VCConfirmStmt myVCConfirmStmt;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an application rule that deals with {@link VCConfirmStmt}.
     * </p>
     *
     * @param vcConfirmStmt
     *            The {@link VCConfirmStmt} we are applying the rule to.
     * @param block
     *            The assertive code block that the subclasses are applying the rule to.
     * @param context
     *            The verification context that contains all the information we have collected so far.
     * @param stGroup
     *            The string template group we will be using.
     * @param blockModel
     *            The model associated with {@code block}.
     */
    public VCConfirmStmtRule(VCConfirmStmt vcConfirmStmt, AssertiveCodeBlock block, VerificationContext context,
            STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myVCConfirmStmt = vcConfirmStmt;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method applies the {@code Proof Rule}.
     * </p>
     */
    @Override
    public final void applyRule() {
        // Combine the verification conditions with those stored inside the VCConfirmStmt.
        // Note: We really shouldn't have any VerificationCondition inside the current
        // assertive code block. The while statement that generated the VCConfirmStmt
        // should have made sure of that. However, it doesn't hurt to combine them rather
        // than simply replacing it directly. - YS
        List<VerificationCondition> newVCs = myCurrentAssertiveCodeBlock.getVCs();
        newVCs.addAll(myVCConfirmStmt.getVCs());

        // Store the new list of vcs
        myCurrentAssertiveCodeBlock.setVCs(newVCs);

        // Add the different details to the various different output models
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
        stepModel.add("proofRuleName", getRuleDescription()).add("currentStateOfBlock", myCurrentAssertiveCodeBlock);
        myBlockModel.add("vcGenSteps", stepModel.render());
    }

    /**
     * <p>
     * This method returns a description associated with the {@code Proof Rule}.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "VCConfirm Rule";
    }

}
