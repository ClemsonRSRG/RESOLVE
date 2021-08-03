/*
 * ChangeStmtRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.statements;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VCVarExp;
import edu.clemson.cs.rsrg.absyn.statements.ChangeStmt;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationCondition;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import java.util.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>
 * This class contains the logic for applying the {@code change} rule.
 * </p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ChangeStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The {@link ChangeStmt} we are applying the rule to.
     * </p>
     */
    private final ChangeStmt myChangeStmt;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a new application of the {@code change} rule.
     * </p>
     *
     * @param changeStmt The {@link ChangeStmt} we are applying the rule to.
     * @param block The assertive code block that the subclasses are applying
     *        the rule to.
     * @param context The verification context that contains all the information
     *        we have collected so
     *        far.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public ChangeStmtRule(ChangeStmt changeStmt, AssertiveCodeBlock block,
            VerificationContext context, STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myChangeStmt = changeStmt;
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
        // Create a map from each variable to a new NQV
        List<Exp> changeVars = myChangeStmt.getChangingVars();
        Map<Exp, Exp> replacementMap = new LinkedHashMap<>(changeVars.size());
        for (Exp exp : changeVars) {
            VCVarExp vcVarExp = Utilities
                    .createVCVarExp(myCurrentAssertiveCodeBlock, exp.clone());
            myCurrentAssertiveCodeBlock.addFreeVar(vcVarExp);
            replacementMap.put(exp.clone(), vcVarExp);
        }

        // Loop through each verification condition and replace the variable
        // expression wherever possible.
        List<VerificationCondition> newVCs = createReplacementVCs(
                myCurrentAssertiveCodeBlock.getVCs(), replacementMap);

        // Store the new list of vcs
        myCurrentAssertiveCodeBlock.setVCs(newVCs);

        // Add the different details to the various different output models
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
        stepModel.add("proofRuleName", getRuleDescription())
                .add("currentStateOfBlock", myCurrentAssertiveCodeBlock);
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
        return "Change Rule";
    }

}
