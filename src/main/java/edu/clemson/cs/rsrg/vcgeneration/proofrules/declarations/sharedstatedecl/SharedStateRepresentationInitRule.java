/*
 * SharedStateRepresentationInitRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.declarations.sharedstatedecl;

import edu.clemson.cs.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl.SharedStateRealizationDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.declarations.AbstractBlockDeclRule;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for establishing the {@code Shared Variable Representation}'s
 * {@code initialization} declaration rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class SharedStateRepresentationInitRule extends AbstractBlockDeclRule
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@code shared variable} representation we are applying the rule to.</p> */
    private final SharedStateRealizationDec mySharedStateRealizationDec;

    /**
     * <p>While walking a procedure, this stores all the local {@link VarDec VarDec's}
     * program type entry.</p>
     */
    private final Map<VarDec, SymbolTableEntry> myVariableTypeEntries;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application for the {@code initialize}
     * rule for a {@link SharedStateRealizationDec}.</p>
     *
     * @param dec A concept type realization.
     * @param blockVarTypeEntries This block's local variable declarations
     * @param symbolTableBuilder The current symbol table.
     * @param moduleScope The current module scope we are visiting.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param context The verification context that contains all
     *                the information we have collected so far.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public SharedStateRepresentationInitRule(SharedStateRealizationDec dec,
            Map<VarDec, SymbolTableEntry> blockVarTypeEntries,
            MathSymbolTableBuilder symbolTableBuilder, ModuleScope moduleScope,
            AssertiveCodeBlock block, VerificationContext context,
            STGroup stGroup, ST blockModel) {
        super(block, dec.getName().getName(), symbolTableBuilder, moduleScope,
                context, stGroup, blockModel);
        mySharedStateRealizationDec = dec;
        myVariableTypeEntries = blockVarTypeEntries;

        // Build a set of shared variables being affected
        // by the current finalization block
        AffectsClause affectsClause =
                mySharedStateRealizationDec.getInitItem().getAffectedVars();
        if (affectsClause != null) {
            for (Exp exp : affectsClause.getAffectedExps()) {
                if (!Utilities.containsEquivalentExp(myAffectedExps, exp)) {
                    myAffectedExps.add(exp.clone());
                }
            }
        }
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
        return "Initialization Rule (Concept Shared Variable Realization)";
    }

}