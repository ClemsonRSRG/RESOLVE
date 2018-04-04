/*
 * FinalizeVarStmtRule.java
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

import edu.clemson.cs.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.OldExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VCVarExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationCondition;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import edu.clemson.cs.rsrg.vcgeneration.utilities.formaltoactual.InstantiatedFacilityDecl;
import edu.clemson.cs.rsrg.vcgeneration.utilities.helperstmts.FinalizeVarStmt;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
            // Extract the finalization ensure clause from the type.
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
            PosSymbol facQualifier = getFacilityQualifier(decTyAsNameTy);
            finalizationEnsuresExp =
                    createModifiedTypeFinalEnsExp(dec.getLocation(),
                            finalizationEnsuresExp, facQualifier, type
                                    .getFinalization().getAffectedVars());
        }
        // Case #2: A local type representation.
        else {
            // TODO: Change this!
            finalizationEnsuresExp = null;
        }

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

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method for creating the modified {@code ensures} clause
     * that modifies the shared variables appropriately.</p>
     *
     * <p>Note that this helper method also does all the appropriate substitutions to
     * the {@code VCs} in the assertive code block.</p>
     *
     * @param loc Location where we are creating a replacement for.
     * @param originalExp The original expression.
     * @param facQualifier A facility qualifier (if any).
     * @param affectsClause The {@code affects} clause associated with the ensures clause.
     *
     * @return The modified {@code ensures} clause expression.
     */
    private Exp createModifiedTypeFinalEnsExp(Location loc, Exp originalExp,
            PosSymbol facQualifier, AffectsClause affectsClause) {
        // Create a replacement maps
        // 1) substitutions: Contains all the replacements for the originalExp
        // 2) substitutionsForSeq: Contains all the replacements for the VC's sequents.
        Map<Exp, Exp> substitutions = new LinkedHashMap<>();
        Map<Exp, Exp> substitutionsForSeq = new LinkedHashMap<>();

        // Create replacements for any affected variables (if needed)
        if (affectsClause != null) {
            for (Exp affectedExp : affectsClause.getAffectedExps()) {
                // Replace any #originalAffectsExp with the facility qualified modifiedAffectsExp
                VarExp originalAffectsExp = (VarExp) affectedExp;
                VarExp modifiedAffectsExp =
                        Utilities.createVarExp(loc.clone(), facQualifier, originalAffectsExp.getName(),
                                affectedExp.getMathType(), affectedExp.getMathTypeValue());
                substitutions.put(new OldExp(originalAffectsExp.getLocation(), originalAffectsExp),
                        modifiedAffectsExp);

                // Replace any originalAffectsExp with NQV(modifiedAffectsExp)
                VCVarExp vcVarExp = Utilities.createVCVarExp(myCurrentAssertiveCodeBlock, modifiedAffectsExp);
                myCurrentAssertiveCodeBlock.addFreeVar(vcVarExp);
                substitutions.put(originalAffectsExp, vcVarExp);

                // Add modifiedAffectsExp with NQV(modifiedAffectsExp) as a substitution for VC's sequents
                substitutionsForSeq.put(modifiedAffectsExp.clone(), vcVarExp.clone());
            }

            // Retrieve the list of VCs and use the sequent
            // substitution map to do replacements.
            List<VerificationCondition> newVCs =
                    createReplacementVCs(myCurrentAssertiveCodeBlock.getVCs(), substitutionsForSeq);

            // Store the new list of vcs
            myCurrentAssertiveCodeBlock.setVCs(newVCs);
        }

        return originalExp.substitute(substitutions);
    }

    /**
     * <p>An helper method for locating a facility qualifier (if any) from
     * a raw program type.</p>
     *
     * @param ty A raw program type.
     *
     * @return A facility qualifier if the program type came from a facility
     * instantiation, {@code null} otherwise.
     */
    private PosSymbol getFacilityQualifier(NameTy ty) {
        PosSymbol facQualifier = ty.getQualifier();

        // Check to see if there is a facility that instantiated a type
        // that matches "ty".
        if (facQualifier == null) {
            Iterator<InstantiatedFacilityDecl> it =
                    myCurrentVerificationContext
                            .getProcessedInstFacilityDecls().iterator();
            while (it.hasNext() && facQualifier == null) {
                InstantiatedFacilityDecl decl = it.next();

                // One we find one, we are done!
                for (TypeFamilyDec dec : decl.getConceptDeclaredTypes()) {
                    if (dec.getName().getName().equals(ty.getName().getName())) {
                        facQualifier = decl.getInstantiatedFacilityName();
                    }
                }
            }
        }

        return facQualifier;
    }

}