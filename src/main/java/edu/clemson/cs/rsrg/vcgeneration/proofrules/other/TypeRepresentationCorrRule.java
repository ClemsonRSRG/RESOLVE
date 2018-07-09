/*
 * TypeRepresentationCorrRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.other;

import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeRepresentationDec;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for establishing the {@code Type Representation}'s
 * {@code correspondence} is well defined.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class TypeRepresentationCorrRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@code type} representation we are applying the rule to.</p> */
    private final TypeRepresentationDec myTypeRepresentationDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application for a well defined
     * {@code correspondence} rule for a {@link TypeRepresentationDec}.</p>
     *
     * @param dec A concept type realization.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param context The verification context that contains all
     *                the information we have collected so far.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public TypeRepresentationCorrRule(TypeRepresentationDec dec,
            AssertiveCodeBlock block, VerificationContext context,
            STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myTypeRepresentationDec = dec;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        // Create the top most level assume statement and
        // add it to the assertive code block as the first statement
        // ( Assume CPC and RPC and DC and RDC and SS_RC and RC )
        /*AssumeStmt topLevelAssumeStmt =
                new AssumeStmt(myTypeRepresentationDec.getLocation().clone(),
                        myCurrentVerificationContext
                                .createTopLevelAssumeExpFromContext(myTypeRepresentationDec
                                        .getLocation(), true, false), false);
        myCurrentAssertiveCodeBlock.addStatement(topLevelAssumeStmt);*/

        // Assume Cor_Exp

        // Confirm the type's constraint

        // Add the different details to the various different output models
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
        stepModel.add("proofRuleName", getRuleDescription()).add(
                "currentStateOfBlock", myCurrentAssertiveCodeBlock);

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
        return "Well Defined Correspondence Rule (Concept Type Realization)";
    }

}