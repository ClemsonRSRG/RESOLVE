/*
 * InitializeVarStmtRule.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.vcgeneration.proofrules.statements;

import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.rsrg.absyn.declarations.typedecl.TypeRepresentationDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.DotExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.MathExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.rsrg.absyn.rawtypes.RecordTy;
import edu.clemson.rsrg.absyn.rawtypes.Ty;
import edu.clemson.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.typeandpopulate.entry.FacilityTypeRepresentationEntry;
import edu.clemson.rsrg.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.entry.TypeRepresentationEntry;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationContext;
import edu.clemson.rsrg.vcgeneration.utilities.helperstmts.InitializeVarStmt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>
 * This class contains the logic for applying variable declaration and initialization logic for the {@link VarDec}
 * stored inside a {@link InitializeVarStmt}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class InitializeVarStmtRule extends AbstractProofRuleApplication implements ProofRuleApplication {

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
     * The {@link InitializeVarStmt} we are applying the rule to.
     * </p>
     */
    private final InitializeVarStmt myInitVarStmt;

    /**
     * <p>
     * This is the math type graph that indicates relationship between different math types.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an application rule that deals with {@link InitializeVarStmt}.
     * </p>
     *
     * @param initVarStmt
     *            The {@link InitializeVarStmt} we are applying the rule to.
     * @param symbolTableBuilder
     *            The current symbol table.
     * @param moduleScope
     *            The current module scope we are visiting.
     * @param block
     *            The assertive code block that the subclasses are applying the rule to.
     * @param context
     *            The verification context that contains all the information we have collected so far.
     * @param stGroup
     *            The string template group we will be using.
     * @param blockModel
     *            The model associated with {@code block}.
     */
    public InitializeVarStmtRule(InitializeVarStmt initVarStmt, MathSymbolTableBuilder symbolTableBuilder,
            ModuleScope moduleScope, AssertiveCodeBlock block, VerificationContext context, STGroup stGroup,
            ST blockModel) {
        super(block, context, stGroup, blockModel);
        myCurrentModuleScope = moduleScope;
        myInitVarStmt = initVarStmt;
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
        VarDec dec = myInitVarStmt.getVarDec();
        SymbolTableEntry typeEntry = myInitVarStmt.getVarProgramTypeEntry();
        boolean isGenericVar = myInitVarStmt.isGenericVar();

        // Case #1: Generic type
        Exp initializationEnsuresExp;
        if (isGenericVar) {
            // Create an "Is_Initial" predicate using the generic variable declaration.
            initializationEnsuresExp = Utilities.createInitExp(dec, myCurrentAssertiveCodeBlock.getTypeGraph().BOOLEAN);
        } else {
            // Case #2: A type from some concept.
            if (typeEntry instanceof ProgramTypeEntry) {
                ProgramTypeEntry programTypeEntry = typeEntry.toProgramTypeEntry(dec.getLocation());
                initializationEnsuresExp = getConceptTypeInitEnsures(dec, programTypeEntry);
            }
            // Case #3: A type representation that implements a type from some concept.
            else if (typeEntry instanceof TypeRepresentationEntry) {
                TypeRepresentationEntry representationEntry = typeEntry.toTypeRepresentationEntry(dec.getLocation());
                initializationEnsuresExp = getTypeRepresentationInitEnsures(dec,
                        (TypeRepresentationDec) representationEntry.getDefiningElement());
            }
            // Case #4: A local type representation.
            else {
                FacilityTypeRepresentationEntry representationEntry = typeEntry
                        .toFacilityTypeRepresentationEntry(dec.getLocation());

                // TODO: Logic for local type representation.
                throw new RuntimeException();
            }
        }

        // NY YS
        // TODO: Initialization duration for this variable

        // Assume that initialization has happened.
        AssumeStmt initAssumeStmt = new AssumeStmt(dec.getLocation(), initializationEnsuresExp, false);
        myCurrentAssertiveCodeBlock.addStatement(initAssumeStmt);

        // Add the different details to the various different output models
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
        stepModel.add("proofRuleName", getRuleDescription()).add("currentStateOfBlock", myCurrentAssertiveCodeBlock);
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
        StringBuilder builder = new StringBuilder();
        builder.append("Variable Declaration/Initialization Rule ");

        if (myInitVarStmt.isGenericVar()) {
            builder.append("(Generic Program Type)");
        } else {
            builder.append("(Known Program Type)");
        }

        return builder.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method for extracting the {@code initialization ensures} clause for a {@link ProgramTypeEntry}.
     * </p>
     *
     * @param varDec
     *            The variable declaration we are instantiating.
     * @param programTypeEntry
     *            A program type entry.
     *
     * @return An initialization ensures clause.
     */
    private Exp getConceptTypeInitEnsures(VarDec varDec, ProgramTypeEntry programTypeEntry) {
        // Extract the initialization ensures clause from the type.
        Exp initializationEnsuresExp;
        TypeFamilyDec type = (TypeFamilyDec) programTypeEntry.getDefiningElement();
        AssertionClause initEnsuresClause = type.getInitialization().getEnsures();

        // Create the modified initialization ensures clause with
        // the exemplar replaced with the variable declaration name.
        AssertionClause modifiedInitEnsures = Utilities.getTypeInitEnsuresClause(initEnsuresClause,
                varDec.getLocation(), null, varDec.getName(), type.getExemplar(), programTypeEntry.getModelType(),
                null);

        // Create the initialization ensures expression.
        Location initEnsuresLoc = modifiedInitEnsures.getAssertionExp().getLocation();
        initializationEnsuresExp = Utilities.formConjunct(initEnsuresLoc, null, modifiedInitEnsures,
                new LocationDetailModel(initEnsuresLoc.clone(), varDec.getLocation().clone(),
                        "Initialization Ensures Clause of " + varDec.getName()));

        // Replace any formal shared variables with the correct facility
        // instantiation (if possible).
        NameTy decTyAsNameTy = (NameTy) varDec.getTy();
        PosSymbol facQualifier = Utilities.getFacilityQualifier(decTyAsNameTy, myCurrentVerificationContext);
        initializationEnsuresExp = createEnsuresExpWithModifiedSharedVars(varDec.getLocation(),
                initializationEnsuresExp, facQualifier, type.getInitialization().getAffectedVars());

        return initializationEnsuresExp;
    }

    /**
     * <p>
     * An helper method for extracting the {@code initialization ensures} clause for a {@link TypeRepresentationDec}.
     * </p>
     *
     * @param varDec
     *            The variable declaration we are instantiating.
     * @param typeRepresentationDec
     *            A type representation.
     *
     * @return An initialization ensures clause.
     */
    private Exp getTypeRepresentationInitEnsures(VarDec varDec, TypeRepresentationDec typeRepresentationDec) {
        Exp initializationEnsuresExp;

        // Case #1: Type representation implemented using a named type
        Ty implementingType = typeRepresentationDec.getRepresentation();
        if (implementingType instanceof NameTy) {
            NameTy implementingTypeAsNameTy = (NameTy) implementingType;

            // Query for the type entry in the symbol table
            ProgramTypeEntry programTypeEntry = Utilities
                    .searchProgramType(implementingTypeAsNameTy.getLocation(), implementingTypeAsNameTy.getQualifier(),
                            implementingTypeAsNameTy.getName(), myCurrentModuleScope)
                    .toProgramTypeEntry(implementingTypeAsNameTy.getLocation());
            initializationEnsuresExp = getConceptTypeInitEnsures(varDec, programTypeEntry);
        }
        // Case #2: Type representation implemented using a record type
        else {
            RecordTy implementingTypeAsRecordTy = (RecordTy) implementingType;
            initializationEnsuresExp = VarExp.getTrueVarExp(implementingType.getLocation(), myTypeGraph);

            // varDec as VarExp
            VarExp varDecAsVarExp = Utilities.createVarExp(varDec.getLocation(), null, varDec.getName(),
                    implementingTypeAsRecordTy.getMathType(), implementingTypeAsRecordTy.getMathTypeValue());

            // For each variable inside the record type
            for (VarDec innerDec : implementingTypeAsRecordTy.getFields()) {
                // YS: For now, let's assume that innerDecTy always refer to
                // some named program type and not to another record type.
                NameTy innerDecTy = (NameTy) innerDec.getTy();

                // Query for the type entry in the symbol table
                ProgramTypeEntry programTypeEntry = Utilities.searchProgramType(innerDecTy.getLocation(),
                        innerDecTy.getQualifier(), innerDecTy.getName(), myCurrentModuleScope)
                        .toProgramTypeEntry(innerDecTy.getLocation());
                Exp innerInitEnsuresExp = getConceptTypeInitEnsures(innerDec, programTypeEntry);

                // Create the DotExp
                VarExp innerDecAsVarExp = Utilities.createVarExp(innerDec.getLocation(), null, innerDec.getName(),
                        innerDecTy.getMathType(), innerDecTy.getMathTypeValue());
                List<Exp> segments = new ArrayList<>();
                segments.add(varDecAsVarExp.clone());
                segments.add(innerDecAsVarExp.clone());

                DotExp dotExp = new DotExp(varDec.getLocation(), segments);
                dotExp.setMathType(innerDecTy.getMathType());

                // Replace the inner declaration name with a dotted expression
                // with the outer declaration
                Map<Exp, Exp> substitutionMap = new HashMap<>();
                substitutionMap.put(innerDecAsVarExp.clone(), dotExp);
                innerInitEnsuresExp = innerInitEnsuresExp.substitute(substitutionMap);

                if (VarExp.isLiteralTrue(initializationEnsuresExp)) {
                    initializationEnsuresExp = innerInitEnsuresExp;
                } else {
                    initializationEnsuresExp = MathExp.formConjunct(typeRepresentationDec.getLocation(),
                            initializationEnsuresExp, innerInitEnsuresExp);
                }
            }
        }

        return initializationEnsuresExp;
    }
}
