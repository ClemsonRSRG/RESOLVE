/*
 * IfStmtRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.statement;

import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.items.programitems.IfConditionItem;
import edu.clemson.cs.rsrg.absyn.statements.IfStmt;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for applying the {@code if-else}
 * rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class IfStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@link IfStmt} we are applying the rule to.</p> */
    private final IfStmt myIfStmt;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application of the {@code if-else}
     * rule.</p>
     *
     * @param ifStmt The {@link IfStmt} we are applying
     *               the rule to.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public IfStmtRule(IfStmt ifStmt, AssertiveCodeBlock block, STGroup stGroup,
            ST blockModel) {
        super(block, stGroup, blockModel);
        myIfStmt = ifStmt;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        // Note: In the If-Else Rule, we will end up with a total of two assertive
        // code blocks that are almost identical. The first block will be the current
        // assertive block that we are currently processing and will contain the
        // logic for the if portion of the statement. Regardless if there is an else
        // portion or not, the other block will contain the else portion.
        AssertiveCodeBlock negIfAssertiveCodeBlock =
                myCurrentAssertiveCodeBlock.clone();

        // TODO: Convert the if-condition into mathematical expressions.
        // TODO: Use the nested function call walker to deal with nested function calls.
        IfConditionItem ifConditionItem = myIfStmt.getIfClause();
        ProgramExp ifCondition = ifConditionItem.getTest();

        // If part of the rule

        // Else part of the rule

        // Add the different details to the various different output models
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
        stepModel.add("proofRuleName", getRuleDescription()).add(
                "currentStateOfBlock", myCurrentAssertiveCodeBlock);
        myBlockModel.add("vcGenSteps", stepModel.render());
    }

    /**
     * <p>This method returns a description associated with
     * the {@code Proof Rule}.</p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "If-Else Rule";
    }

}