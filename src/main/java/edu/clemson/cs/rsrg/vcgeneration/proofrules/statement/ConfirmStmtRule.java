/*
 * ConfirmStmtRule.java
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

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.InfixExp;
import edu.clemson.cs.rsrg.absyn.statements.ConfirmStmt;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import java.util.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for applying the {@code confirm}
 * rule to a {@link ConfirmStmt}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ConfirmStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@link ConfirmStmt} we are applying the rule to.</p> */
    private final ConfirmStmt myConfirmStmt;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application of the {@code confirm}
     * rule.</p>
     *
     * @param confirmStmt The {@link ConfirmStmt} we are applying
     *                    the rule to.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public ConfirmStmtRule(ConfirmStmt confirmStmt, AssertiveCodeBlock block,
            STGroup stGroup, ST blockModel) {
        super(block, stGroup, blockModel);
        myConfirmStmt = confirmStmt;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");

        // Check to see if this confirm can be simplified or not.
        if (myConfirmStmt.getSimplify()) {
            // Add the different details to the various different output models
            stepModel.add("proofRuleName", getRuleDescription() + " and Simplified")
                    .add("currentStateOfBlock", myCurrentAssertiveCodeBlock);
        }
        else {
            // Retrieve the expression inside the confirm
            Exp confirmExp = myConfirmStmt.getAssertion();

            // TODO: Apply the various sequent reduction rules.
            List<Sequent> newSequents = new ArrayList<>();
            newSequents.addAll(myCurrentAssertiveCodeBlock.getSequents());
            if (confirmExp instanceof InfixExp) {
                List<InfixExp> consequentExps =
                        ((InfixExp) confirmExp).split(null, false);

                for (InfixExp consequentExp : consequentExps) {
                    List<Exp> antecedent = new ArrayList<>();
                    List<Exp> consequent = new ArrayList<>();
                    consequent.add(consequentExp);

                    // Add this new sequent to our sequent list.
                    newSequents.add(new Sequent(myConfirmStmt.getLocation(),
                            antecedent, consequent));
                }
            }
            else {
                List<Exp> antecedent = new ArrayList<>();
                List<Exp> consequent = new ArrayList<>();
                consequent.add(confirmExp);

                // Add this new sequent to our sequent list.
                newSequents.add(new Sequent(myConfirmStmt.getLocation(),
                        antecedent, consequent));
            }

            // Set this as our new list of sequents
            myCurrentAssertiveCodeBlock.setSequents(newSequents);

            stepModel.add("proofRuleName", getRuleDescription())
                    .add("currentStateOfBlock", myCurrentAssertiveCodeBlock);
        }

        // Add the different details to the various different output models
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
        return "Confirm Rule";
    }

}