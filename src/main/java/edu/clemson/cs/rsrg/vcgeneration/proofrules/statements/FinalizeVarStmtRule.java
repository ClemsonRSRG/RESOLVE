/*
 * FinalizeVarStmtRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.statements;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeRepresentationDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.DotExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.MathExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.absyn.rawtypes.RecordTy;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.entry.FacilityTypeRepresentationEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.TypeRepresentationEntry;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import edu.clemson.cs.rsrg.vcgeneration.utilities.helperstmts.FinalizeVarStmt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>
 * This class contains the logic for applying variable finalization to the
 * variable declaration with
 * a known program type stored inside a {@link FinalizeVarStmt}.
 * </p>
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

    /**
     * <p>
     * The module scope for the file we are generating {@code VCs} for.
     * </p>
     */
    private final ModuleScope myCurrentModuleScope;

    /**
     * <p>
     * The {@link FinalizeVarStmt} we are applying the rule to.
     * </p>
     */
    private final FinalizeVarStmt myFinalVarStmt;

    /**
     * <p>
     * This is the math type graph that indicates relationship between different
     * math types.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an application rule that deals with {@link FinalizeVarStmt}.
     * </p>
     *
     * @param finalVarStmt The {@link FinalizeVarStmt} we are applying the rule
     *        to.
     * @param block The assertive code block that the subclasses are applying
     *        the rule to.
     * @param context The verification context that contains all the information
     *        we have collected so
     *        far.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public FinalizeVarStmtRule(FinalizeVarStmt finalVarStmt,
            MathSymbolTableBuilder symbolTableBuilder, ModuleScope moduleScope,
            AssertiveCodeBlock block, VerificationContext context,
            STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myCurrentModuleScope = moduleScope;
        myFinalVarStmt = finalVarStmt;
        myTypeGraph = symbolTableBuilder.getTypeGraph();
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
        // Obtain the program variable and type from the statement
        VarDec dec = myFinalVarStmt.getVarDec();
        SymbolTableEntry typeEntry = myFinalVarStmt.getVarProgramTypeEntry();

        // Case #1: A type from some concept.
        Exp finalizationEnsuresExp;
        if (typeEntry instanceof ProgramTypeEntry) {
            ProgramTypeEntry programTypeEntry =
                    typeEntry.toProgramTypeEntry(dec.getLocation());
            finalizationEnsuresExp =
                    getConceptTypeFinalEnsures(dec, programTypeEntry);
        }
        // Case #2: A type representation that implements a type from some concept.
        else if (typeEntry instanceof TypeRepresentationEntry) {
            TypeRepresentationEntry representationEntry =
                    typeEntry.toTypeRepresentationEntry(dec.getLocation());
            finalizationEnsuresExp = getTypeRepresentationFinalEnsures(dec,
                    (TypeRepresentationDec) representationEntry
                            .getDefiningElement());
        }
        // Case #3: A local type representation.
        else {
            FacilityTypeRepresentationEntry representationEntry = typeEntry
                    .toFacilityTypeRepresentationEntry(dec.getLocation());

            // TODO: Logic for local type representation.
            throw new RuntimeException();
        }

        // Assume that finalization has happened.
        AssumeStmt finalAssumeStmt = new AssumeStmt(dec.getLocation(),
                finalizationEnsuresExp, false);
        myCurrentAssertiveCodeBlock.addStatement(finalAssumeStmt);

        // Add the different details to the various different output models
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
        stepModel.add("proofRuleName", getRuleDescription())
                .add("currentStateOfBlock", myCurrentAssertiveCodeBlock);
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
        return "Variable Finalization Rule (Known Program Type)";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method for extracting the {@code finalization ensures} clause
     * for a
     * {@link ProgramTypeEntry}.
     * </p>
     *
     * @param varDec The variable declaration we are finalizing.
     * @param programTypeEntry A program type entry.
     *
     * @return A finalization ensures clause.
     */
    private Exp getConceptTypeFinalEnsures(VarDec varDec,
            ProgramTypeEntry programTypeEntry) {
        // Extract the finalization ensures clause from the type.
        Exp finalizationEnsuresExp;
        TypeFamilyDec type =
                (TypeFamilyDec) programTypeEntry.getDefiningElement();
        AssertionClause finalEnsuresClause =
                type.getFinalization().getEnsures();

        // Create the modified finalization ensures clause with
        // the exemplar replaced with the variable declaration name.
        AssertionClause modifiedFinalEnsures =
                Utilities.getTypeFinalEnsuresClause(finalEnsuresClause,
                        varDec.getLocation(), null, varDec.getName(),
                        type.getExemplar(), programTypeEntry.getModelType(),
                        null);

        // Create the finalization ensures expression.
        Location finalEnsuresLoc =
                modifiedFinalEnsures.getAssertionExp().getLocation();
        finalizationEnsuresExp = Utilities.formConjunct(finalEnsuresLoc, null,
                modifiedFinalEnsures,
                new LocationDetailModel(finalEnsuresLoc.clone(),
                        varDec.getLocation().clone(),
                        "Finalization Ensures Clause of " + varDec.getName()));

        // Replace any formal shared variables with the correct facility
        // instantiation (if possible).
        NameTy decTyAsNameTy = (NameTy) varDec.getTy();
        PosSymbol facQualifier = Utilities.getFacilityQualifier(decTyAsNameTy,
                myCurrentVerificationContext);
        finalizationEnsuresExp = createEnsuresExpWithModifiedSharedVars(
                varDec.getLocation(), finalizationEnsuresExp, facQualifier,
                type.getFinalization().getAffectedVars());

        return finalizationEnsuresExp;
    }

    /**
     * <p>
     * An helper method for extracting the {@code finalization ensures} clause
     * for a
     * {@link TypeRepresentationDec}.
     * </p>
     *
     * @param varDec The variable declaration we are finalizing.
     * @param typeRepresentationDec A type representation.
     *
     * @return A finalization ensures clause.
     */
    private Exp getTypeRepresentationFinalEnsures(VarDec varDec,
            TypeRepresentationDec typeRepresentationDec) {
        Exp finalizationEnsuresExp;

        // Case #1: Type representation implemented using a named type
        Ty implementingType = typeRepresentationDec.getRepresentation();
        if (implementingType instanceof NameTy) {
            NameTy implementingTypeAsNameTy = (NameTy) implementingType;

            // Query for the type entry in the symbol table
            ProgramTypeEntry programTypeEntry = Utilities.searchProgramType(
                    implementingTypeAsNameTy.getLocation(),
                    implementingTypeAsNameTy.getQualifier(),
                    implementingTypeAsNameTy.getName(), myCurrentModuleScope)
                    .toProgramTypeEntry(implementingTypeAsNameTy.getLocation());
            finalizationEnsuresExp =
                    getConceptTypeFinalEnsures(varDec, programTypeEntry);
        }
        // Case #2: Type representation implemented using a record type
        else {
            RecordTy implementingTypeAsRecordTy = (RecordTy) implementingType;
            finalizationEnsuresExp = VarExp
                    .getTrueVarExp(implementingType.getLocation(), myTypeGraph);

            // varDec as VarExp
            VarExp varDecAsVarExp = Utilities.createVarExp(varDec.getLocation(),
                    null, varDec.getName(),
                    implementingTypeAsRecordTy.getMathType(),
                    implementingTypeAsRecordTy.getMathTypeValue());

            // For each variable inside the record type
            for (VarDec innerDec : implementingTypeAsRecordTy.getFields()) {
                // YS: For now, let's assume that innerDecTy always refer to
                // some named program type and not to another record type.
                NameTy innerDecTy = (NameTy) innerDec.getTy();

                // Query for the type entry in the symbol table
                ProgramTypeEntry programTypeEntry = Utilities
                        .searchProgramType(innerDecTy.getLocation(),
                                innerDecTy.getQualifier(), innerDecTy.getName(),
                                myCurrentModuleScope)
                        .toProgramTypeEntry(innerDecTy.getLocation());
                Exp innerFinalEnsuresExp =
                        getConceptTypeFinalEnsures(innerDec, programTypeEntry);

                // Create the DotExp
                VarExp innerDecAsVarExp =
                        Utilities.createVarExp(innerDec.getLocation(), null,
                                innerDec.getName(), innerDecTy.getMathType(),
                                innerDecTy.getMathTypeValue());
                List<Exp> segments = new ArrayList<>();
                segments.add(varDecAsVarExp.clone());
                segments.add(innerDecAsVarExp.clone());

                DotExp dotExp = new DotExp(varDec.getLocation(), segments);
                dotExp.setMathType(innerDecTy.getMathType());

                // Replace the inner declaration name with a dotted expression
                // with the outer declaration
                Map<Exp, Exp> substitutionMap = new HashMap<>();
                substitutionMap.put(innerDecAsVarExp.clone(), dotExp);
                innerFinalEnsuresExp =
                        innerFinalEnsuresExp.substitute(substitutionMap);

                if (VarExp.isLiteralTrue(finalizationEnsuresExp)) {
                    finalizationEnsuresExp = innerFinalEnsuresExp;
                }
                else {
                    finalizationEnsuresExp = MathExp.formConjunct(
                            typeRepresentationDec.getLocation(),
                            finalizationEnsuresExp, innerFinalEnsuresExp);
                }
            }
        }

        return finalizationEnsuresExp;
    }
}
