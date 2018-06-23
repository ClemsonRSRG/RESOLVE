/*
 * InitializeVarStmtRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.statement;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.entry.FacilityTypeRepresentationEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.TypeRepresentationEntry;
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
            // Case #2: A type from some concept.
            if (typeEntry instanceof ProgramTypeEntry) {
                // Extract the initialization ensures clause from the type.
                ProgramTypeEntry programTypeEntry =
                        typeEntry.toProgramTypeEntry(dec.getLocation());
                TypeFamilyDec type =
                        (TypeFamilyDec) programTypeEntry.getDefiningElement();
                AssertionClause initEnsuresClause =
                        type.getInitialization().getEnsures();

                // Create the modified initialization ensures clause with
                // the exemplar replaced with the variable declaration name.
                AssertionClause modifiedInitEnsures =
                        Utilities.getTypeInitEnsuresClause(initEnsuresClause,
                                dec.getLocation(), null, dec.getName(), type
                                        .getExemplar(), programTypeEntry
                                        .getModelType(), null);

                // Create the initialization ensures expression.
                Location initEnsuresLoc =
                        modifiedInitEnsures.getAssertionExp().getLocation();
                initializationEnsuresExp =
                        Utilities.formConjunct(initEnsuresLoc, null,
                                modifiedInitEnsures, new LocationDetailModel(
                                        initEnsuresLoc.clone(), dec
                                                .getLocation().clone(),
                                        "Initialization Ensures Clause of "
                                                + dec.getName()));

                // Replace any formal shared variables with the correct facility
                // instantiation (if possible).
                NameTy decTyAsNameTy = (NameTy) dec.getTy();
                PosSymbol facQualifier =
                        Utilities.getFacilityQualifier(decTyAsNameTy,
                                myCurrentVerificationContext);
                initializationEnsuresExp =
                        createEnsuresExpWithModifiedSharedVars(dec
                                .getLocation(), initializationEnsuresExp,
                                facQualifier, type.getInitialization()
                                        .getAffectedVars());
            }
            // Case #3: A type representation that implements a type from some concept.
            else if (typeEntry instanceof TypeRepresentationEntry) {
                TypeRepresentationEntry representationEntry =
                        typeEntry.toTypeRepresentationEntry(dec.getLocation());
                ProgramTypeEntry programTypeEntry =
                        representationEntry.getDefiningTypeEntry();

                // TODO: Logic for type representations associated with a concept.
                initializationEnsuresExp = null;
            }
            // Case #4: A local type representation.
            else {
                FacilityTypeRepresentationEntry representationEntry =
                        typeEntry.toFacilityTypeRepresentationEntry(dec
                                .getLocation());

                // TODO: Logic for local type representation.
                initializationEnsuresExp = null;
            }
        }

        // NY YS
        // TODO: Initialization duration for this variable

        // Assume that initialization has happened.
        AssumeStmt initAssumeStmt =
                new AssumeStmt(dec.getLocation(), initializationEnsuresExp,
                        false);
        myCurrentAssertiveCodeBlock.addStatement(initAssumeStmt);

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