/*
 * FacilityInitStmtRule.java
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
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl.SharedStateDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import edu.clemson.cs.rsrg.vcgeneration.utilities.formaltoactual.FormalActualLists;
import edu.clemson.cs.rsrg.vcgeneration.utilities.formaltoactual.InstantiatedFacilityDecl;
import edu.clemson.cs.rsrg.vcgeneration.utilities.helperstmts.FacilityInitStmt;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>
 * This class contains the logic for applying facility initialization logic for
 * the
 * {@link FacilityDec} stored inside a {@link FacilityInitStmt}.
 * </p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class FacilityInitStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The {@link FacilityInitStmt} we are applying the rule to.
     * </p>
     */
    private final FacilityInitStmt myFacilityInitStmt;

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
     * This creates an application rule that deals with
     * {@link FacilityInitStmt}.
     * </p>
     *
     * @param facilityInitStmt The {@link FacilityInitStmt} we are applying the
     *        rule to.
     * @param symbolTableBuilder The current symbol table.
     * @param block The assertive code block that the subclasses are applying
     *        the rule to.
     * @param context The verification context that contains all the information
     *        we have collected so
     *        far.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public FacilityInitStmtRule(FacilityInitStmt facilityInitStmt,
            MathSymbolTableBuilder symbolTableBuilder, AssertiveCodeBlock block,
            VerificationContext context, STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myFacilityInitStmt = facilityInitStmt;
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
        // Obtain the corresponding instantiated facility declaration
        InstantiatedFacilityDecl instantiatedFacilityDecl =
                myCurrentVerificationContext.getProcessedInstFacilityDecl(
                        myFacilityInitStmt.getInstantiatedFacilityDec());

        // Extract the shared variable initialization for each block
        Exp initializationEnsuresExp = VarExp
                .getTrueVarExp(myFacilityInitStmt.getLocation(), myTypeGraph);
        PosSymbol facilityName =
                instantiatedFacilityDecl.getInstantiatedFacilityName();
        for (SharedStateDec dec : instantiatedFacilityDecl
                .getConceptSharedStates()) {
            Exp decInitEnsures = getSharedVariableInitEnsures(facilityName, dec,
                    instantiatedFacilityDecl);
            if (VarExp.isLiteralTrue(initializationEnsuresExp)) {
                initializationEnsuresExp = decInitEnsures;
            }
            else {
                if (!VarExp.isLiteralTrue(decInitEnsures)) {
                    initializationEnsuresExp = MathExp.formConjunct(
                            myFacilityInitStmt.getLocation().clone(),
                            initializationEnsuresExp, decInitEnsures);
                }
            }
        }

        // Add any definition variables and <Type>.Receptacles = {}
        for (TypeFamilyDec typeFamilyDec : instantiatedFacilityDecl
                .getConceptDeclaredTypes()) {
            // Extract the definition variables (if any)
            Exp defVarExp = generateDefVarExps(facilityName, typeFamilyDec,
                    instantiatedFacilityDecl);
            if (VarExp.isLiteralTrue(initializationEnsuresExp)) {
                initializationEnsuresExp = defVarExp;
            }
            else {
                if (!VarExp.isLiteralTrue(defVarExp)) {
                    initializationEnsuresExp = MathExp.formConjunct(
                            myFacilityInitStmt.getLocation().clone(),
                            initializationEnsuresExp, defVarExp);
                }
            }

            // Create an equality expression indicating the type's receptacles
            // is the empty set.
            EqualsExp typeRecepEqualsExp = createTypeReceptaclesEmptySetExp(
                    facilityName, typeFamilyDec);
            if (VarExp.isLiteralTrue(initializationEnsuresExp)) {
                initializationEnsuresExp = typeRecepEqualsExp;
            }
            else {
                initializationEnsuresExp = MathExp.formConjunct(
                        myFacilityInitStmt.getLocation().clone(),
                        initializationEnsuresExp, typeRecepEqualsExp);
            }
        }

        // Assume that initialization has happened.
        if (!VarExp.isLiteralTrue(initializationEnsuresExp)) {
            AssumeStmt initAssumeStmt =
                    new AssumeStmt(myFacilityInitStmt.getLocation().clone(),
                            initializationEnsuresExp, false);
            myCurrentAssertiveCodeBlock.addStatement(initAssumeStmt);
        }

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
        return "Facility Initialization Rule";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method for creating an expression that indicate the type's
     * receptacles is equal to
     * the empty set.
     * </p>
     *
     * @param facilityName Name of the facility we are processing.
     * @param typeFamilyDec A type family declaration.
     *
     * @return An expression that indicates that the instantiated type's
     *         {@code Receptacles} is equal
     *         to the empty set.
     */
    private EqualsExp createTypeReceptaclesEmptySetExp(PosSymbol facilityName,
            TypeFamilyDec typeFamilyDec) {
        // Create a new TypeReceptaclesExp
        TypeReceptaclesExp typeReceptaclesExp = new TypeReceptaclesExp(
                typeFamilyDec.getLocation().clone(),
                Utilities.createVarExp(myFacilityInitStmt.getLocation().clone(),
                        facilityName, typeFamilyDec.getName().clone(),
                        typeFamilyDec.getModel().getMathType(),
                        typeFamilyDec.getModel().getMathTypeValue()));
        typeReceptaclesExp.setMathType(myTypeGraph.RECEPTACLES);

        // Create a new empty set expression
        SetCollectionExp emptySetExp =
                new SetCollectionExp(myFacilityInitStmt.getLocation().clone(),
                        new HashSet<MathExp>());
        emptySetExp.setMathType(myTypeGraph.SSET);
        emptySetExp.setMathTypeValue(myTypeGraph.EMPTY_SET);

        // Create the new equality expression
        EqualsExp equalsExp = new EqualsExp(
                myFacilityInitStmt.getLocation().clone(), typeReceptaclesExp,
                null, EqualsExp.Operator.EQUAL, emptySetExp);
        equalsExp.setMathType(myTypeGraph.BOOLEAN);
        equalsExp.setLocationDetailModel(new LocationDetailModel(
                myFacilityInitStmt.getLocation().clone(),
                myFacilityInitStmt.getLocation().clone(),
                "Receptacles of type: " + typeFamilyDec.getName().getName()));

        return equalsExp;
    }

    /**
     * <p>
     * An helper method for extracting the {@code initialization ensures} clause
     * from a
     * {@link SharedStateDec}.
     * </p>
     *
     * @param facilityName Name of the facility we are processing.
     * @param stateDec A shared variables block.
     * @param instantiatedFacilityDecl The instantiating facility declaration.
     *
     * @return An initialization ensures clause.
     */
    private Exp getSharedVariableInitEnsures(PosSymbol facilityName,
            SharedStateDec stateDec,
            InstantiatedFacilityDecl instantiatedFacilityDecl) {
        // Extract the initialization ensures clause from the type.
        Exp initializationEnsuresExp;
        AssertionClause initEnsuresClause =
                stateDec.getInitialization().getEnsures();

        // Create the initialization ensures expression.
        Location initEnsuresLoc =
                initEnsuresClause.getAssertionExp().getLocation();
        initializationEnsuresExp =
                Utilities.formConjunct(initEnsuresLoc, null, initEnsuresClause,
                        new LocationDetailModel(initEnsuresLoc.clone(),
                                myFacilityInitStmt.getLocation().clone(),
                                "Facility Initialization Ensures Clause of "
                                        + facilityName.getName()));

        // Create a replacement map for substituting shared variables
        // with qualified ones.
        Map<Exp, Exp> substitutionMap = new LinkedHashMap<>();
        for (MathVarDec mathVarDec : stateDec.getAbstractStateVars()) {
            VarExp stateVarExp = Utilities.createVarExp(
                    facilityName.getLocation().clone(), null,
                    mathVarDec.getName(), mathVarDec.getMathType(), null);
            VarExp qualifiedVarExp = Utilities.createVarExp(
                    facilityName.getLocation().clone(), facilityName,
                    mathVarDec.getName(), mathVarDec.getMathType(), null);
            substitutionMap.put(stateVarExp, qualifiedVarExp);
        }

        // Substitute any formal concept arguments with its actual
        FormalActualLists conceptParamArgs =
                instantiatedFacilityDecl.getConceptParamArgLists();
        Iterator<VarExp> formalArgsIt =
                conceptParamArgs.getFormalParamList().iterator();
        Iterator<Exp> actualArgsIt =
                conceptParamArgs.getActualArgList().iterator();
        while (formalArgsIt.hasNext() && actualArgsIt.hasNext()) {
            substitutionMap.put(formalArgsIt.next(), actualArgsIt.next());
        }

        return initializationEnsuresExp.substitute(substitutionMap);
    }
}
