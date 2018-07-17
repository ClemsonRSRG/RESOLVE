/*
 * SharedStateCorrRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.declarations.sharedstatedecl;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl.SharedStateDec;
import edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl.SharedStateRealizationDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.DotExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.absyn.statements.ConfirmStmt;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import java.util.LinkedHashMap;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for establishing the {@code Shared Variable}'s
 * {@code correspondence} is well defined.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class SharedStateCorrRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@code shared state} we are implementing.</p> */
    private final SharedStateDec myCorrespondingSharedStateDec;

    /** <p>The {@code shared state} realization we are applying the rule to.</p> */
    private final SharedStateRealizationDec mySharedStateRealizationDec;

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application for a well defined
     * {@code correspondence} rule for a {@link SharedStateRealizationDec}.</p>
     *
     * @param dec A shared state realization.
     * @param correspondingSharedStateDec The corresponding shared state we are realizing.
     * @param symbolTableBuilder The current symbol table.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param context The verification context that contains all
     *                the information we have collected so far.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public SharedStateCorrRule(SharedStateRealizationDec dec,
            SharedStateDec correspondingSharedStateDec,
            MathSymbolTableBuilder symbolTableBuilder,
            AssertiveCodeBlock block, VerificationContext context,
            STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myCorrespondingSharedStateDec = correspondingSharedStateDec;
        mySharedStateRealizationDec = dec;
        myTypeGraph = symbolTableBuilder.getTypeGraph();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        // Create the top most level assume statement and
        // add it to the assertive code block as the first statement
        Exp topLevelAssumeExp =
                myCurrentVerificationContext
                        .createTopLevelAssumeExpFromContext(
                                mySharedStateRealizationDec.getLocation(), true,
                                false);

        // ( Assume CPC and RPC and DC and RDC and SS_RC; )
        AssumeStmt topLevelAssumeStmt =
                new AssumeStmt(mySharedStateRealizationDec.getLocation().clone(),
                        topLevelAssumeExp, false);
        myCurrentAssertiveCodeBlock.addStatement(topLevelAssumeStmt);

        // ( Assume SS_Cor_Exp; )
        AssertionClause stateCorrespondenceClause =
                mySharedStateRealizationDec.getCorrespondence();
        Exp sharedStateCorrExp =
                Utilities.formConjunct(mySharedStateRealizationDec.getLocation().clone(), null,
                        stateCorrespondenceClause, new LocationDetailModel(
                                stateCorrespondenceClause.getAssertionExp()
                                        .getLocation().clone(),
                                stateCorrespondenceClause.getAssertionExp()
                                        .getLocation().clone(),
                                "Shared Variable Correspondence"));
        AssumeStmt correspondenceAssumeStmt =
                new AssumeStmt(mySharedStateRealizationDec.getLocation().clone(),
                        sharedStateCorrExp, false);
        myCurrentAssertiveCodeBlock.addStatement(correspondenceAssumeStmt);

        // Create a replacement map for substituting shared
        // variables with ones that indicates they are conceptual.
        Map<Exp, Exp> substitutionExemplarToConc = new LinkedHashMap<>();
        for (MathVarDec sharedVarDec : myCorrespondingSharedStateDec.getAbstractStateVars()) {
            VarExp exemplarExp =
                    Utilities.createVarExp(mySharedStateRealizationDec.getLocation().clone(),
                            null, sharedVarDec.getName().clone(),
                            sharedVarDec.getMathType(), null);
            DotExp concExemplarExp =
                    Utilities.createConcVarExp(
                            new VarDec(sharedVarDec.getName(),
                                    sharedVarDec.getTy()),
                            sharedVarDec.getMathType(), myTypeGraph.BOOLEAN);
            substitutionExemplarToConc.put(exemplarExp, concExemplarExp);
        }

        // Confirm the shared variable's constraint
        // ( Confirm VC; )
        Exp sharedVariableConstraint =
                myCorrespondingSharedStateDec.getConstraint().getAssertionExp().clone();
        sharedVariableConstraint = sharedVariableConstraint.substitute(substitutionExemplarToConc);
        sharedVariableConstraint.setLocationDetailModel(new LocationDetailModel(
                myCorrespondingSharedStateDec.getLocation().clone(), mySharedStateRealizationDec
                .getLocation().clone(),
                "Well Defined Correspondence for Shared Variables"));
        ConfirmStmt finalConfirmStmt =
                new ConfirmStmt(mySharedStateRealizationDec.getLocation().clone(),
                        sharedVariableConstraint, VarExp
                        .isLiteralTrue(sharedVariableConstraint));
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
        return "Well Defined Correspondence Rule (Shared State)";
    }

}