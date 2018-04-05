/*
 * InitializeVarStmtRule.java
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

import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import edu.clemson.cs.rsrg.vcgeneration.utilities.helperstmts.InitializeVarStmt;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for applying variable declaration and
 * initialization logic for the {@link VarDec} stored inside a {@link InitializeVarStmt}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class InitializeVarStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@link InitializeVarStmt} we are applying the rule to.</p> */
    private final InitializeVarStmt myInitVarStmt;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an application rule that deals with
     * {@link InitializeVarStmt}.</p>
     *
     * @param initVarStmt The {@link InitializeVarStmt} we are applying
     *                    the rule to.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param context The verification context that contains all
     *                the information we have collected so far.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public InitializeVarStmtRule(InitializeVarStmt initVarStmt,
            AssertiveCodeBlock block, VerificationContext context,
            STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myInitVarStmt = initVarStmt;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        // Obtain the program variable and type from the statement
        VarDec dec = myInitVarStmt.getVarDec();
        SymbolTableEntry typeEntry = myInitVarStmt.getVarProgramTypeEntry();
        boolean isGenericVar = myInitVarStmt.isGenericVar();

        // Case #1: Generic type
        Exp initializationEnsuresExp;
        if (isGenericVar) {
            // Create an "Is_Initial" predicate using the generic variable declaration.
            initializationEnsuresExp =
                    Utilities.createInitExp(dec, myCurrentAssertiveCodeBlock
                            .getTypeGraph().BOOLEAN);
        }
        else {
            // TODO: Change this!
            initializationEnsuresExp = null;
        }

        // Case #1: A type from some concept.
        /*
        if (typeEntry.getDefiningElement() instanceof TypeFamilyDec) {


            // Variable declaration rule for known types
            /*TypeFamilyDec type =
                    (TypeFamilyDec) typeEntry.getDefiningElement();
            AssertionClause initEnsures =
                    type.getInitialization().getEnsures();
            AssertionClause modifiedInitEnsures =
                    Utilities.getTypeInitEnsuresClause(initEnsures, dec
                            .getLocation(), null, dec.getName(), type
                            .getExemplar(), typeEntry.getModelType(), null);

            // TODO: Logic for types in concept realizations

            declRule =
                    new KnownTypeVariableDeclRule(dec, modifiedInitEnsures,
                            myCurrentAssertiveCodeBlock, mySTGroup,
                            myAssertiveCodeBlockModels
                                    .remove(myCurrentAssertiveCodeBlock));

            // Store the variable's finalization item for
            // future use.
            AffectsClause finalAffects =
                    type.getFinalization().getAffectedVars();
            AssertionClause finalEnsures =
                    type.getFinalization().getEnsures();
            if (!VarExp.isLiteralTrue(finalEnsures.getAssertionExp())) {
                myVariableSpecFinalItems.put(dec, new SpecInitFinalItem(
                        type.getFinalization().getLocation(), type
                                .getFinalization().getClauseType(),
                        finalAffects, Utilities.getTypeFinalEnsuresClause(
                                finalEnsures, dec.getLocation(), null, dec
                                        .getName(), type.getExemplar(),
                                typeEntry.getModelType(), null)));
            }

            // NY YS
            // TODO: Initialization duration for this variable
        }*/

        AssumeStmt initAssumeStmt =
                new AssumeStmt(dec.getLocation(), initializationEnsuresExp,
                        false);
        myCurrentAssertiveCodeBlock.addStatement(initAssumeStmt);

        // Add this as a free variable
        myCurrentAssertiveCodeBlock.addFreeVar(Utilities.createVarExp(dec
                .getLocation(), null, dec.getName(), dec.getMathType(), null));

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
        StringBuilder builder = new StringBuilder();
        builder.append("Variable Declaration/Initialization Rule ");

        if (myInitVarStmt.isGenericVar()) {
            builder.append("(Generic Program Type)");
        }
        else {
            builder.append("(Known Program Type)");
        }

        return builder.toString();
    }

}