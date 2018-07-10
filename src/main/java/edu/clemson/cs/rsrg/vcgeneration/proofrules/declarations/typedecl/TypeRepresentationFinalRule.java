/*
 * TypeRepresentationFinalRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.declarations.typedecl;

import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeRepresentationDec;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for establishing the {@code Type Representation}'s
 * {@code finalization} declaration rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class TypeRepresentationFinalRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@code type} representation we are applying the rule to.</p> */
    private final TypeRepresentationDec myTypeRepresentationDec;

    /**
     * <p>The symbol table entry representing program type associated
     * with the variable we are trying to finalize.</p>
     */
    private final SymbolTableEntry myVarTypeEntry;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application for the {@code finalize}
     * rule for a {@link TypeRepresentationDec}.</p>
     *
     * @param dec A concept type realization.
     * @param symbolTableEntry The program type entry associated with {@code dec}.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param context The verification context that contains all
     *                the information we have collected so far.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public TypeRepresentationFinalRule(TypeRepresentationDec dec,
            SymbolTableEntry symbolTableEntry, AssertiveCodeBlock block,
            VerificationContext context, STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myTypeRepresentationDec = dec;
        myVarTypeEntry = symbolTableEntry;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
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
        return "Finalization Rule (Concept Type Realization)";
    }

}