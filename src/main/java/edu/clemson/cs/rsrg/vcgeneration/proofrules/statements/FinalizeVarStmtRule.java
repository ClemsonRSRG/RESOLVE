/*
 * FinalizeVarStmtRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.statements;

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
import edu.clemson.cs.rsrg.vcgeneration.utilities.helperstmts.FinalizeVarStmt;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for applying variable finalization
 * to the variable declaration with a known program type stored
 * inside a {@link FinalizeVarStmt}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class FinalizeVarStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@link FinalizeVarStmt} we are applying the rule to.</p> */
    private final FinalizeVarStmt myFinalVarStmt;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an application rule that deals with
     * {@link FinalizeVarStmt}.</p>
     *
     * @param finalVarStmt The {@link FinalizeVarStmt} we are applying
     *                     the rule to.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param context The verification context that contains all
     *                the information we have collected so far.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public FinalizeVarStmtRule(FinalizeVarStmt finalVarStmt,
            AssertiveCodeBlock block, VerificationContext context,
            STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myFinalVarStmt = finalVarStmt;
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
        VarDec dec = myFinalVarStmt.getVarDec();
        SymbolTableEntry typeEntry = myFinalVarStmt.getVarProgramTypeEntry();

        // Case #1: A type from some concept.
        Exp finalizationEnsuresExp;
        if (typeEntry instanceof ProgramTypeEntry) {
            // Extract the finalization ensures clause from the type.
            ProgramTypeEntry programTypeEntry =
                    typeEntry.toProgramTypeEntry(dec.getLocation());
            TypeFamilyDec type =
                    (TypeFamilyDec) programTypeEntry.getDefiningElement();
            AssertionClause finalEnsuresClause =
                    type.getFinalization().getEnsures();

            // Create the modified finalization ensures clause with
            // the exemplar replaced with the variable declaration name.
            AssertionClause modifiedFinalEnsures =
                    Utilities.getTypeFinalEnsuresClause(finalEnsuresClause, dec
                            .getLocation(), null, dec.getName(), type
                            .getExemplar(), programTypeEntry.getModelType(),
                            null);

            // Create the finalization ensures expression.
            Location finalEnsuresLoc =
                    modifiedFinalEnsures.getAssertionExp().getLocation();
            finalizationEnsuresExp =
                    Utilities.formConjunct(finalEnsuresLoc, null,
                            modifiedFinalEnsures, new LocationDetailModel(
                                    finalEnsuresLoc.clone(), dec.getLocation()
                                            .clone(),
                                    "Finalization Ensures Clause of "
                                            + dec.getName()));

            // Replace any formal shared variables with the correct facility
            // instantiation (if possible).
            NameTy decTyAsNameTy = (NameTy) dec.getTy();
            PosSymbol facQualifier =
                    Utilities.getFacilityQualifier(decTyAsNameTy,
                            myCurrentVerificationContext);
            finalizationEnsuresExp =
                    createEnsuresExpWithModifiedSharedVars(dec.getLocation(),
                            finalizationEnsuresExp, facQualifier, type
                                    .getFinalization().getAffectedVars());
        }
        // Case #2: A type representation that implements a type from some concept.
        else if (typeEntry instanceof TypeRepresentationEntry) {
            TypeRepresentationEntry representationEntry =
                    typeEntry.toTypeRepresentationEntry(dec.getLocation());
            ProgramTypeEntry programTypeEntry =
                    representationEntry.getDefiningTypeEntry();

            // TODO: Logic for type representations associated with a concept.
            finalizationEnsuresExp = null;
        }
        // Case #3: A local type representation.
        else {
            FacilityTypeRepresentationEntry representationEntry =
                    typeEntry.toFacilityTypeRepresentationEntry(dec
                            .getLocation());

            // TODO: Logic for local type representation.
            finalizationEnsuresExp = null;
        }

        // Assume that finalization has happened.
        AssumeStmt finalAssumeStmt =
                new AssumeStmt(dec.getLocation(), finalizationEnsuresExp, false);
        myCurrentAssertiveCodeBlock.addStatement(finalAssumeStmt);

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
        return "Variable Finalization Rule (Known Program Type)";
    }

}