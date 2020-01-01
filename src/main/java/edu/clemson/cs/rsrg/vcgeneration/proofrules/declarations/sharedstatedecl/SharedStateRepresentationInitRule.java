/*
 * SharedStateRepresentationInitRule.java
 * ---------------------------------
 * Copyright (c) 2020
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
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl.SharedStateDec;
import edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl.SharedStateRealizationDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.DotExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.items.programitems.RealizInitFinalItem;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.absyn.statements.ConfirmStmt;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.declarations.AbstractBlockDeclRule;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import edu.clemson.cs.rsrg.vcgeneration.utilities.helperstmts.FinalizeVarStmt;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>
 * This class contains the logic for establishing the {@code Shared Variable}'s
 * {@code initialization} declaration rule.
 * </p>
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

    /**
     * <p>
     * The {@code shared state} we are implementing.
     * </p>
     */
    private final SharedStateDec myCorrespondingSharedStateDec;

    /**
     * <p>
     * The {@code shared variable} representation we are applying the rule to.
     * </p>
     */
    private final SharedStateRealizationDec mySharedStateRealizationDec;

    /**
     * <p>
     * While walking a procedure, this stores all the local {@link VarDec
     * VarDec's} program type
     * entry.
     * </p>
     */
    private final Map<VarDec, SymbolTableEntry> myVariableTypeEntries;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a new application for the {@code initialize} rule for a
     * {@link SharedStateRealizationDec}.
     * </p>
     *
     * @param dec A shared state realization.
     * @param correspondingSharedStateDec The corresponding shared state we are
     *        realizing.
     * @param blockVarTypeEntries This block's local variable declarations
     * @param symbolTableBuilder The current symbol table.
     * @param moduleScope The current module scope we are visiting.
     * @param block The assertive code block that the subclasses are applying
     *        the rule to.
     * @param context The verification context that contains all the information
     *        we have collected so
     *        far.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public SharedStateRepresentationInitRule(SharedStateRealizationDec dec,
            SharedStateDec correspondingSharedStateDec,
            Map<VarDec, SymbolTableEntry> blockVarTypeEntries,
            MathSymbolTableBuilder symbolTableBuilder, ModuleScope moduleScope,
            AssertiveCodeBlock block, VerificationContext context,
            STGroup stGroup, ST blockModel) {
        super(block, dec.getName().getName(), symbolTableBuilder, moduleScope,
                context, stGroup, blockModel);
        myCorrespondingSharedStateDec = correspondingSharedStateDec;
        mySharedStateRealizationDec = dec;
        myVariableTypeEntries = blockVarTypeEntries;

        // Build a set of shared variables being affected
        // by the shared variable's initialization affects clause
        AffectsClause affectsClauseSharedState = myCorrespondingSharedStateDec
                .getInitialization().getAffectedVars();
        if (affectsClauseSharedState != null) {
            for (Exp exp : affectsClauseSharedState.getAffectedExps()) {
                if (!Utilities.containsEquivalentExp(myAffectedExps, exp)) {
                    myAffectedExps.add(exp.clone());
                }
            }
        }

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
     * <p>
     * This method applies the {@code Proof Rule}.
     * </p>
     */
    @Override
    public final void applyRule() {
        // Initialization block
        RealizInitFinalItem initItem =
                mySharedStateRealizationDec.getInitItem();

        // Add all the statements
        myCurrentAssertiveCodeBlock.addStatements(initItem.getStatements());

        // YS: Simply create a finalization statement for each variable that
        // allow us to deal with generating question mark variables
        // and duration logic when we backtrack through the code.
        List<VarDec> varDecs = initItem.getVariables();
        for (VarDec dec : varDecs) {
            // Only need to finalize non-generic type variables.
            if (myVariableTypeEntries.containsKey(dec)) {
                myCurrentAssertiveCodeBlock.addStatement(new FinalizeVarStmt(
                        dec, myVariableTypeEntries.remove(dec)));
            }

            // TODO: Add the finalization duration ensures (if any)
        }

        // Confirm the shared variable's convention
        // ( Confirm SS_RC; )
        Exp confirmConventionExp = myCurrentVerificationContext
                .createSharedStateRealizConventionExp(
                        initItem.getLocation().clone());
        ConfirmStmt conventionConfirmStmt = new ConfirmStmt(
                initItem.getLocation().clone(), confirmConventionExp,
                VarExp.isLiteralTrue(confirmConventionExp));
        myCurrentAssertiveCodeBlock.addStatement(conventionConfirmStmt);

        // ( Assume SS_Cor_Exp; )
        AssertionClause stateCorrespondenceClause =
                mySharedStateRealizationDec.getCorrespondence();
        Exp sharedStateCorrExp = Utilities.formConjunct(
                mySharedStateRealizationDec.getLocation().clone(), null,
                stateCorrespondenceClause,
                new LocationDetailModel(
                        stateCorrespondenceClause.getAssertionExp()
                                .getLocation().clone(),
                        stateCorrespondenceClause.getAssertionExp()
                                .getLocation().clone(),
                        "Shared Variable Correspondence"));
        AssumeStmt correspondenceAssumeStmt = new AssumeStmt(
                mySharedStateRealizationDec.getLocation().clone(),
                sharedStateCorrExp, false);
        myCurrentAssertiveCodeBlock.addStatement(correspondenceAssumeStmt);

        // Create the final confirm expression
        Exp finalConfirmExp = createFinalConfirmExp();

        // Replace any facility declaration instantiation arguments
        // in the ensures clause.
        finalConfirmExp = Utilities.replaceFacilityFormalWithActual(
                finalConfirmExp, new ArrayList<ParameterVarDec>(),
                myCurrentModuleScope.getDefiningElement().getName(),
                myCurrentVerificationContext);

        // Confirm the type initialization ensures clause is satisfied.
        // YS: Also need to make sure that all shared variables that are not affected
        // are being "restored".
        ConfirmStmt finalConfirmStmt =
                new ConfirmStmt(initItem.getLocation().clone(), finalConfirmExp,
                        VarExp.isLiteralTrue(finalConfirmExp));
        myCurrentAssertiveCodeBlock.addStatement(finalConfirmStmt);

        // Add the different details to the various different output models
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
        stepModel.add("proofRuleName", getRuleDescription())
                .add("currentStateOfBlock", myCurrentAssertiveCodeBlock);

        // Add the different details to the various different output models
        myBlockModel.add("vcGenSteps", stepModel.render());
    }

    /**
     * <p>
     * This method returns a description associated with the {@code Proof Rule}.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "Initialization Rule (Concept Shared Variable Realization)";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method that uses the {@code ensures} clause from the associated
     * {@link SharedStateDec} and builds the appropriate {@code ensures} clause
     * that will be an
     * {@link AssertiveCodeBlock AssertiveCodeBlock's} final {@code confirm}
     * statement.
     * </p>
     *
     * @return The final confirm expression.
     */
    private Exp createFinalConfirmExp() {
        // Add the type family's initialization ensures clause
        AssertionClause ensuresClause =
                myCorrespondingSharedStateDec.getInitialization().getEnsures();
        Location initEnsuresLoc = ensuresClause.getLocation();
        Exp ensuresExp = ensuresClause.getAssertionExp().clone();
        ensuresExp.setLocationDetailModel(new LocationDetailModel(
                ensuresExp.getLocation().clone(), initEnsuresLoc.clone(),
                "Initialization Ensures Clause of Shared Variables"));

        // Create a replacement map for substituting shared
        // variables with ones that indicates they are conceptual.
        Map<Exp, Exp> substitutionExemplarToConc = new LinkedHashMap<>();
        for (MathVarDec sharedVarDec : myCorrespondingSharedStateDec
                .getAbstractStateVars()) {
            VarExp exemplarExp = Utilities.createVarExp(
                    mySharedStateRealizationDec.getLocation().clone(), null,
                    sharedVarDec.getName().clone(), sharedVarDec.getMathType(),
                    null);
            DotExp concExemplarExp = Utilities.createConcVarExp(
                    new VarDec(sharedVarDec.getName(), sharedVarDec.getTy()),
                    sharedVarDec.getMathType(), myTypeGraph.BOOLEAN);
            substitutionExemplarToConc.put(exemplarExp, concExemplarExp);
        }

        // Create a replacement map for substituting affected shared
        // variables with ones that indicates they are conceptual.
        substitutionExemplarToConc = addAffectedConceptualSharedVars(
                myCorrespondingSharedStateDec.getInitialization()
                        .getAffectedVars(),
                substitutionExemplarToConc, myTypeGraph.BOOLEAN);

        // Perform substitution
        ensuresExp = ensuresExp.substitute(substitutionExemplarToConc);

        // Loop through all instantiated facility's and generate a "restores" ensures clause
        // for non-affected shared variables/math definition variables.
        return createFacilitySharedVarRestoresEnsuresExp(initEnsuresLoc.clone(),
                ensuresExp);
    }
}
