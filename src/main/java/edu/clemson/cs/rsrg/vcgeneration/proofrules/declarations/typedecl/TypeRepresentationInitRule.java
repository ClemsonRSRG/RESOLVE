/*
 * TypeRepresentationInitRule.java
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

import edu.clemson.cs.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeRepresentationDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
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
import java.util.List;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for establishing the {@code Type Representation}'s
 * {@code initialization} declaration rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class TypeRepresentationInitRule extends AbstractBlockDeclRule
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@code type} representation we are applying the rule to.</p> */
    private final TypeRepresentationDec myTypeRepresentationDec;

    /**
     * <p>While walking a procedure, this stores all the local {@link VarDec VarDec's}
     * program type entry.</p>
     */
    private final Map<VarDec, SymbolTableEntry> myVariableTypeEntries;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application for the {@code initialization}
     * rule for a {@link TypeRepresentationDec}.</p>
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
    public TypeRepresentationInitRule(TypeRepresentationDec dec,
            Map<VarDec, SymbolTableEntry> blockVarTypeEntries,
            MathSymbolTableBuilder symbolTableBuilder, ModuleScope moduleScope,
            AssertiveCodeBlock block, VerificationContext context,
            STGroup stGroup, ST blockModel) {
        super(block, dec.getName().getName(), symbolTableBuilder, moduleScope,
                context, stGroup, blockModel);
        myTypeRepresentationDec = dec;
        myVariableTypeEntries = blockVarTypeEntries;

        // Build a set of shared variables being affected
        // by the current initialization block
        AffectsClause affectsClause =
                myTypeRepresentationDec.getTypeInitItem().getAffectedVars();
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
        // Initialization block
        RealizInitFinalItem initItem =
                myTypeRepresentationDec.getTypeInitItem();

        // Obtain the associated type family declaration
        TypeFamilyDec typeFamilyDec =
                Utilities.getAssociatedTypeFamilyDec(myTypeRepresentationDec,
                        myCurrentVerificationContext);

        // Add all the statements
        myCurrentAssertiveCodeBlock.addStatements(initItem.getStatements());

        // YS: Simply create a finalization statement for each variable that
        //     allow us to deal with generating question mark variables
        //     and duration logic when we backtrack through the code.
        List<VarDec> varDecs = initItem.getVariables();
        for (VarDec dec : varDecs) {
            // Only need to finalize non-generic type variables.
            if (myVariableTypeEntries.containsKey(dec)) {
                myCurrentAssertiveCodeBlock.addStatement(new FinalizeVarStmt(
                        dec, myVariableTypeEntries.remove(dec)));
            }

            // TODO: Add the finalization duration ensures (if any)
        }

        // Confirm the shared variable's and our type convention
        // ( Confirm SS_RC and RC; )
        Exp confirmConventionExp =
                myCurrentVerificationContext
                        .createSharedStateRealizConventionExp(initItem
                                .getLocation().clone());
        AssertionClause typeConventionClause =
                myTypeRepresentationDec.getConvention().clone();
        confirmConventionExp =
                Utilities.formConjunct(initItem.getLocation().clone(),
                        confirmConventionExp, typeConventionClause,
                        new LocationDetailModel(typeConventionClause
                                .getLocation().clone(), initItem.getLocation()
                                .clone(), "Type: "
                                + myTypeRepresentationDec.getName().getName()
                                + "'s Convention is Satisfied."));
        ConfirmStmt conventionConfirmStmt =
                new ConfirmStmt(initItem.getLocation().clone(),
                        confirmConventionExp, VarExp
                                .isLiteralTrue(confirmConventionExp));
        myCurrentAssertiveCodeBlock.addStatement(conventionConfirmStmt);

        // Assume the shared variable's and our type correspondence
        // ( Assume SS_Corr_Exp and Cor_Exp; )
        Exp assumeCorrespondenceExp =
                myCurrentVerificationContext
                        .createSharedStateRealizCorrespondenceExp(initItem
                                .getLocation().clone());
        AssertionClause typeCorrespondenceClause =
                myTypeRepresentationDec.getCorrespondence().clone();
        assumeCorrespondenceExp =
                Utilities.formConjunct(initItem.getLocation().clone(),
                        assumeCorrespondenceExp, typeCorrespondenceClause,
                        new LocationDetailModel(typeCorrespondenceClause
                                .getLocation().clone(), initItem.getLocation()
                                .clone(), "Type: "
                                + myTypeRepresentationDec.getName().getName()
                                + "'s Correspondence."));
        AssumeStmt correspondenceAssumeStmt =
                new AssumeStmt(initItem.getLocation().clone(),
                        assumeCorrespondenceExp, false);
        myCurrentAssertiveCodeBlock.addStatement(correspondenceAssumeStmt);

        // Create the final confirm expression
        Exp finalConfirmExp = createFinalConfirmExp(typeFamilyDec);

        // Replace any facility declaration instantiation arguments
        // in the ensures clause.
        finalConfirmExp =
                Utilities.replaceFacilityFormalWithActual(finalConfirmExp,
                        new ArrayList<ParameterVarDec>(), myCurrentModuleScope
                                .getDefiningElement().getName(),
                        myCurrentVerificationContext);

        // Confirm the type initialization ensures clause is satisfied.
        // YS: Also need to make sure that all shared variables that are not affected
        //     are being "restored".
        ConfirmStmt finalConfirmStmt =
                new ConfirmStmt(initItem.getLocation().clone(),
                        finalConfirmExp, VarExp.isLiteralTrue(finalConfirmExp));
        myCurrentAssertiveCodeBlock.addStatement(finalConfirmStmt);

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
        return "Initialization Rule (Concept Type Realization)";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method that uses the {@code ensures} clause from the associated
     * {@link TypeFamilyDec} and builds the appropriate {@code ensures} clause that will be an
     * {@link AssertiveCodeBlock AssertiveCodeBlock's} final {@code confirm} statement.</p>
     *
     * @param typeFamilyDec The associated type family declaration.
     *
     * @return The final confirm expression.
     */
    private Exp createFinalConfirmExp(TypeFamilyDec typeFamilyDec) {
        // Add the type family's initialization ensures clause
        AssertionClause ensuresClause =
                typeFamilyDec.getInitialization().getEnsures();
        Location initEnsuresLoc = ensuresClause.getLocation();
        Exp ensuresExp = ensuresClause.getAssertionExp().clone();
        ensuresExp.setLocationDetailModel(new LocationDetailModel(ensuresExp
                .getLocation().clone(), initEnsuresLoc.clone(),
                "Initialization Ensures Clause of " + typeFamilyDec.getName()));

        // Add any non-affected shared variable/def var's restores ensures
        // and return the modified expression.
        return processNonAffectedVarsEnsures(initEnsuresLoc, ensuresExp,
                typeFamilyDec);
    }
}