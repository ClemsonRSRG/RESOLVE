/*
 * IfStmtRule.java
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

import edu.clemson.cs.rsrg.absyn.declarations.typedecl.AbstractTypeRepresentationDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.MathExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramFunctionExp;
import edu.clemson.cs.rsrg.absyn.items.programitems.IfConditionItem;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.absyn.statements.ConfirmStmt;
import edu.clemson.cs.rsrg.absyn.statements.IfStmt;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.treewalk.TreeWalker;
import edu.clemson.cs.rsrg.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.formaltoactual.InstantiatedFacilityDecl;
import edu.clemson.cs.rsrg.vcgeneration.utilities.treewalkers.ProgramFunctionExpWalker;
import java.util.List;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for applying the {@code if-else}
 * rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class IfStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>This contains all the types declared by the {@code Concept}
     * associated with the current module. Note that if we are in a
     * {@code Facility}, this list will be empty.</p>
     */
    private final List<TypeFamilyDec> myCurrentConceptDeclaredTypes;

    /**
     * <p>The module scope for the file we are generating
     * {@code VCs} for.</p>
     */
    private final ModuleScope myCurrentModuleScope;

    /**
     * <p>If we are in a {@code Procedure} and it is an recursive
     * operation implementation, then this stores the decreasing clause
     * expression.</p>
     */
    private final Exp myCurrentProcedureDecreasingExp;

    /**
     * <p>The {@link OperationEntry} associated with this {@code If}
     * statement if we are inside a {@code ProcedureDec}.</p>
     */
    private final OperationEntry myCurrentProcedureOperationEntry;

    /** <p>The {@link IfStmt} we are applying the rule to.</p> */
    private final IfStmt myIfStmt;

    /**
     * <p>If our current module scope allows us to introduce new type implementations,
     * this will contain all the {@link AbstractTypeRepresentationDec}. Otherwise,
     * this list will be empty.</p>
     */
    private final List<AbstractTypeRepresentationDec> myLocalRepresentationTypeDecs;

    /** <p>The list of processed {@link InstantiatedFacilityDecl}. </p> */
    private final List<InstantiatedFacilityDecl> myProcessedInstFacilityDecls;

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application of the {@code if-else}
     * rule.</p>
     *
     * @param ifStmt The {@link IfStmt} we are applying
     *               the rule to.
     * @param typeFamilyDecs List of abstract types we are implementing or extending.
     * @param localRepresentationTypeDecs List of local representation types.
     * @param processedInstFacDecs The list of processed {@link InstantiatedFacilityDecl}.
     * @param symbolTableBuilder The current symbol table.
     * @param moduleScope The current module scope we are visiting.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public IfStmtRule(IfStmt ifStmt, List<TypeFamilyDec> typeFamilyDecs,
            List<AbstractTypeRepresentationDec> localRepresentationTypeDecs,
            List<InstantiatedFacilityDecl> processedInstFacDecs,
            MathSymbolTableBuilder symbolTableBuilder, ModuleScope moduleScope,
            AssertiveCodeBlock block, STGroup stGroup, ST blockModel) {
        super(block, stGroup, blockModel);
        myCurrentConceptDeclaredTypes = typeFamilyDecs;
        myCurrentModuleScope = moduleScope;
        myCurrentProcedureDecreasingExp =
                myCurrentAssertiveCodeBlock
                        .getCorrespondingOperationDecreasingExp();
        myCurrentProcedureOperationEntry =
                myCurrentAssertiveCodeBlock.getCorrespondingOperation();
        myIfStmt = ifStmt;
        myLocalRepresentationTypeDecs = localRepresentationTypeDecs;
        myProcessedInstFacilityDecls = processedInstFacDecs;
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
        // Convert the if-condition into mathematical expressions.
        IfConditionItem ifConditionItem = myIfStmt.getIfClause();
        ProgramExp ifCondition = ifConditionItem.getTest();
        if (ifCondition instanceof ProgramFunctionExp) {
            // Note: In the If-Else Rule, we will end up with a total of two assertive
            // code blocks that are almost identical. The first block will be the current
            // assertive block that we are currently processing and will contain the
            // logic for the if portion of the statement. Regardless if there is an else
            // portion or not, the other block will contain the else portion.
            AssertiveCodeBlock negIfAssertiveCodeBlock =
                    myCurrentAssertiveCodeBlock.clone();

            // Use the walker to convert to mathematical expression
            ProgramFunctionExp ifConditionAsProgramFunctionExp =
                    (ProgramFunctionExp) ifCondition;
            ProgramFunctionExpWalker walker;
            if (myCurrentProcedureOperationEntry == null) {
                walker =
                        new ProgramFunctionExpWalker(
                                myCurrentAssertiveCodeBlock,
                                myCurrentConceptDeclaredTypes,
                                myLocalRepresentationTypeDecs,
                                myProcessedInstFacilityDecls,
                                myCurrentModuleScope, myTypeGraph);
            }
            else {
                walker =
                        new ProgramFunctionExpWalker(
                                myCurrentProcedureOperationEntry,
                                myCurrentProcedureDecreasingExp,
                                myCurrentAssertiveCodeBlock,
                                myCurrentConceptDeclaredTypes,
                                myLocalRepresentationTypeDecs,
                                myProcessedInstFacilityDecls,
                                myCurrentModuleScope, myTypeGraph);
            }
            TreeWalker.visit(walker, ifConditionAsProgramFunctionExp);

            // Retrieve the various pieces of information from the walker
            Exp generatedRequires =
                    walker.getRequiresClause(ifConditionItem.getTest()
                            .getLocation());
            Exp generatedEnsures =
                    walker.getEnsuresClause(ifConditionAsProgramFunctionExp);
            List<Exp> restoresParamExps =
                    walker.getRestoresParamEnsuresClauses();
            List<ConfirmStmt> terminationConfirms =
                    walker.getTerminationConfirmStmts();

            // Form a conjunct using the restoresParamExps
            // YS: We also make a copy of this for the else part,
            //     while storing the associated location details.
            Exp restoresParamEnsuresIfPart =
                    VarExp.getTrueVarExp(ifConditionItem.getTest()
                            .getLocation(), myTypeGraph);
            Exp restoresParamEnsuresElsePart =
                    VarExp.getTrueVarExp(ifConditionItem.getTest()
                            .getLocation(), myTypeGraph);
            for (Exp exp : restoresParamExps) {
                // Make a copy of the expression for the else part
                // and add the new location detail.
                Exp expCopy = exp.clone();

                if (VarExp.isLiteralTrue(restoresParamEnsuresIfPart)) {
                    restoresParamEnsuresIfPart = exp;
                    restoresParamEnsuresElsePart = expCopy;
                }
                else {
                    restoresParamEnsuresIfPart =
                            MathExp.formConjunct(ifConditionItem.getTest()
                                    .getLocation().clone(),
                                    restoresParamEnsuresIfPart, exp);
                    restoresParamEnsuresElsePart =
                            MathExp.formConjunct(ifConditionItem.getTest()
                                    .getLocation().clone(),
                                    restoresParamEnsuresElsePart, expCopy);
                }
            }

            // If part of the rule
            // 1) If the testing condition contains recursive calls,
            //    we need to add all the termination confirm statements
            //    to the if assertive code block.
            for (ConfirmStmt confirmStmt : terminationConfirms) {
                myCurrentAssertiveCodeBlock.addStatement(confirmStmt);
            }

            // 2) If the testing condition has any requires clauses,
            //    we need to add it as a new confirm statement.
            //    ( Confirm Invk_Cond(BE) )
            if (!VarExp.isLiteralTrue(generatedRequires)) {
                myCurrentAssertiveCodeBlock.addStatement(new ConfirmStmt(
                        ifConditionItem.getTest().getLocation().clone(),
                        generatedRequires, false));
            }

            // 3) Add the testing condition as a new stipulate assume statement.
            //    ( Stipulate Math(BE) )
            Exp ifConditionBEExp = generatedEnsures.clone();
            ifConditionBEExp.setLocationDetailModel(new LocationDetailModel(
                    generatedEnsures.getLocation(), ifConditionBEExp
                            .getLocation(), "If Statement Condition"));
            myCurrentAssertiveCodeBlock.addStatement(new AssumeStmt(ifCondition
                    .getLocation().clone(), ifConditionBEExp, true));

            // 4) If the ProgramFunctionExp walker generated any restores
            //    parameter ensures clauses, we need to add it as a new
            //    assume statement.
            if (!VarExp.isLiteralTrue(restoresParamEnsuresIfPart)) {
                myCurrentAssertiveCodeBlock.addStatement(new AssumeStmt(
                        ifConditionItem.getTest().getLocation().clone(),
                        restoresParamEnsuresIfPart, false));
            }

            // 5) Add all the statements inside the if-part to the
            //    if-assertive code block.
            myCurrentAssertiveCodeBlock.addStatements(ifConditionItem
                    .getStatements());

            // NY YS
            // TODO: Duration for If Part

            // 6) Add a branching condition for this block
            myCurrentAssertiveCodeBlock.addBranchingCondition(ifCondition
                    .getLocation().clone(), ifConditionBEExp.toString(), true);

            // 7) Add the different details to the various different output models
            ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
            stepModel.add("proofRuleName", "If-Part Rule").add(
                    "currentStateOfBlock", myCurrentAssertiveCodeBlock);
            myBlockModel.add("vcGenSteps", stepModel.render());

            // Else part of the rule
            // 1) Add the testing condition as a new stipulate assume statement.
            //    ( Stipulate not(Math(BE)) )
            Exp elseConditionBEExp = generatedEnsures.clone();
            elseConditionBEExp =
                    Utilities
                            .negateExp(elseConditionBEExp, myTypeGraph.BOOLEAN);
            elseConditionBEExp.setLocationDetailModel(new LocationDetailModel(
                    generatedEnsures.getLocation(), elseConditionBEExp
                            .getLocation(),
                    "Negation of If Statement Condition"));
            negIfAssertiveCodeBlock.addStatement(new AssumeStmt(ifCondition
                    .getLocation().clone(), elseConditionBEExp, true));

            // 2) Add all the statements inside the else-part to the
            //    else-assertive code block.
            negIfAssertiveCodeBlock.addStatements(myIfStmt.getElseclause());

            // NY YS
            // TODO: Duration for Else Part

            // 3) Add a branching condition for this block
            negIfAssertiveCodeBlock.addBranchingCondition(ifCondition
                    .getLocation().clone(), elseConditionBEExp.toString(),
                    false);

            // 4) Store the new block and add a new block model that goes with it.
            myResultingAssertiveCodeBlocks.add(negIfAssertiveCodeBlock);

            ST negIfBlockModel =
                    mySTGroup.getInstanceOf("outputAssertiveCodeBlock");
            negIfBlockModel.add("blockName", negIfAssertiveCodeBlock.getName());
            ST negIfStepModel = mySTGroup.getInstanceOf("outputVCGenStep");
            negIfStepModel.add("proofRuleName", "Else-Part Rule").add(
                    "currentStateOfBlock", negIfAssertiveCodeBlock);
            negIfBlockModel.add("vcGenSteps", negIfStepModel.render());
            myNewAssertiveCodeBlockModels.put(negIfAssertiveCodeBlock,
                    negIfBlockModel);
        }
        else {
            Utilities.expNotHandled(ifCondition, myIfStmt.getLocation());
        }
    }

    /**
     * <p>This method returns a description associated with
     * the {@code Proof Rule}.</p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "If-Else Rule";
    }

}