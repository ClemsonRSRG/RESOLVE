/*
 * ProcedureDeclRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.declaration;

import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.items.mathitems.SpecInitFinalItem;
import edu.clemson.cs.rsrg.absyn.statements.ConfirmStmt;
import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.other.KnownTypeVariableFinalizationRule;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for the {@code procedure}
 * declaration rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ProcedureDeclRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>The {@code procedure} declaration's {@code ensures}
     * expression.</p>
     */
    private final Exp myEnsuresExp;

    /** <p>Location that created this rule application</p> */
    private final Location myLocation;

    /**
     * <p>List of all the {@link Statement Statements} in this
     * {@code procedure} declaration.</p>
     */
    private final List<Statement> myStatements;

    /** <p>The list of {@link VarDec VarDecs} in this
     * {@code procedure} declaration.</p>
     */
    private final List<VarDec> myVariableDecs;

    /**
     * <p>This stores all the local {@link VarDec VarDec's}
     * {@code finalization} specification item if we were able to generate one.</p>
     */
    private final Map<VarDec, SpecInitFinalItem> myVariableSpecFinalItems;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application for the {@code procedure}
     * declaration rule.</p>
     *
     * @param loc The location that generated this rule.
     * @param procVarDecs The {@link VarDec VarDecs} inside the
     *                    {@code procedure}.
     * @param procStmts The {@link Statement Statements} inside the
     *                  {@code procedure}.
     * @param procVarFinalItems The local variable declaration's
     *                          {@code finalization} specification items.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public ProcedureDeclRule(Location loc, List<VarDec> procVarDecs,
            Map<VarDec, SpecInitFinalItem> procVarFinalItems,
            List<Statement> procStmts, Exp procEnsuresExp,
            AssertiveCodeBlock block, STGroup stGroup, ST blockModel) {
        super(block, stGroup, blockModel);
        myEnsuresExp = procEnsuresExp;
        myLocation = loc;
        myStatements = procStmts;
        myVariableDecs = procVarDecs;
        myVariableSpecFinalItems = procVarFinalItems;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        // TODO: Assume decreasing expression if it is recursive

        // Add all the statements
        myCurrentAssertiveCodeBlock.addStatements(myStatements);

        // TODO: Add the finalization duration ensures (if any)

        // TODO: Correct_Op_Hyp rule (Shared Variables and Type)
        // Correct_Op_Hyp rule: Only applies to non-local operations
        // in concept realizations.

        // TODO: Well_Def_Corr_Hyp rule (Shared Variables and Type)
        // Well_Def_Corr_Hyp rule: Rather than doing direct replacement,
        // we leave that logic to the parsimonious vc step. A replacement
        // will occur if this is a correspondence function or an implies
        // will be formed if this is a correspondence relation.

        // Add the variable finalization expressions
        Map<Exp, Exp> newFreeVarSubstitutions = new LinkedHashMap<>();
        for (VarDec dec : myVariableDecs) {
            if (myVariableSpecFinalItems.containsKey(dec)) {
                KnownTypeVariableFinalizationRule finalizationRule =
                        new KnownTypeVariableFinalizationRule(dec,
                                myVariableSpecFinalItems.get(dec),
                                myCurrentAssertiveCodeBlock, mySTGroup,
                                myBlockModel);
                finalizationRule.applyRule();

                // Add all the free variables.
                newFreeVarSubstitutions.putAll(finalizationRule.getNewFreeVarSubstitutions());
            }
        }

        // Replace any new free variables expressions generated by the
        // variable finalization rule.
        Exp newEnsuresExp = myEnsuresExp.substitute(newFreeVarSubstitutions);

        // Use the ensures clause to create a final confirm statement
        ConfirmStmt finalConfirmStmt =
                new ConfirmStmt(myLocation.clone(), newEnsuresExp, false);
        myCurrentAssertiveCodeBlock.addStatement(finalConfirmStmt);

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
        return "Procedure Declaration Rule (Part 2)";
    }

}