/*
 * FuncAssignStmtRule.java
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

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.rsrg.absyn.expressions.programexpr.ProgramFunctionExp;
import edu.clemson.rsrg.absyn.statements.ConfirmStmt;
import edu.clemson.rsrg.absyn.statements.FuncAssignStmt;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.typeandpopulate.entry.OperationEntry;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationCondition;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationContext;
import edu.clemson.rsrg.vcgeneration.utilities.treewalkers.ProgramFunctionExpWalker;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>
 * This class contains the logic for applying the {@code function assignment} rule.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class FuncAssignStmtRule extends AbstractProofRuleApplication implements ProofRuleApplication {

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
     * If we are in a {@code Procedure} and it is an recursive operation implementation, then this stores the decreasing
     * clause expression.
     * </p>
     */
    private final Exp myCurrentProcedureDecreasingExp;

    /**
     * <p>
     * The {@link OperationEntry} associated with this {@code If} statement if we are inside a {@code ProcedureDec}.
     * </p>
     */
    private final OperationEntry myCurrentProcedureOperationEntry;

    /**
     * <p>
     * The {@link FuncAssignStmt} we are applying the rule to.
     * </p>
     */
    private final FuncAssignStmt myFuncAssignStmt;

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
     * This creates a new application of the {@code function assignment} rule.
     * </p>
     *
     * @param funcAssignStmt
     *            The {@link FuncAssignStmt} we are applying the rule to.
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
    public FuncAssignStmtRule(FuncAssignStmt funcAssignStmt, MathSymbolTableBuilder symbolTableBuilder,
            ModuleScope moduleScope, AssertiveCodeBlock block, VerificationContext context, STGroup stGroup,
            ST blockModel) {
        super(block, context, stGroup, blockModel);
        myCurrentModuleScope = moduleScope;
        myCurrentProcedureDecreasingExp = myCurrentAssertiveCodeBlock.getCorrespondingOperationDecreasingExp();
        myCurrentProcedureOperationEntry = myCurrentAssertiveCodeBlock.getCorrespondingOperation();
        myFuncAssignStmt = funcAssignStmt;
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
        // Convert the right hand side into something we can substitute.
        ProgramExp assignProgramExp = myFuncAssignStmt.getAssignExp();
        Exp assignExp;
        if (assignProgramExp instanceof ProgramFunctionExp) {
            // Use the walker to convert to mathematical expression
            ProgramFunctionExp assignProgramFunctionExp = (ProgramFunctionExp) assignProgramExp;
            ProgramFunctionExpWalker walker;
            if (myCurrentProcedureOperationEntry == null) {
                walker = new ProgramFunctionExpWalker(myCurrentVerificationContext, myCurrentModuleScope, myTypeGraph);
            } else {
                walker = new ProgramFunctionExpWalker(myCurrentProcedureOperationEntry, myCurrentProcedureDecreasingExp,
                        myCurrentVerificationContext, myCurrentModuleScope, myTypeGraph);
            }
            TreeWalker.visit(walker, assignProgramFunctionExp);

            // Retrieve the various pieces of information from the walker
            Exp generatedRequires = walker.getRequiresClause(assignProgramFunctionExp.getLocation());
            Exp generatedEnsures = walker.getEnsuresClause(assignProgramFunctionExp);
            List<ConfirmStmt> terminationConfirms = walker.getTerminationConfirmStmts();

            // If the program function contains recursive calls,
            // we need to add all the termination confirm statements
            // to the if assertive code block.
            for (ConfirmStmt confirmStmt : terminationConfirms) {
                myCurrentAssertiveCodeBlock.addStatement(confirmStmt);
            }

            // If the program function has any requires clauses,
            // we need to add it as a new confirm statement.
            // ( Confirm Invk_Cond( F(exp, b, c) ) )
            if (!VarExp.isLiteralTrue(generatedRequires)) {
                myCurrentAssertiveCodeBlock.addStatement(
                        new ConfirmStmt(assignProgramFunctionExp.getLocation().clone(), generatedRequires, false));
            }

            // Set the generated ensures as our new assign expression
            assignExp = generatedEnsures.clone();
        } else {
            // Simply convert to the math equivalent expression
            assignExp = Utilities.convertExp(assignProgramExp, myCurrentModuleScope);
        }

        // Convert the left hand side into the expression we are going to substitute
        // with assignExp.
        Exp varExp = Utilities.convertExp(myFuncAssignStmt.getVariableExp(), myCurrentModuleScope);

        // Create a map of replacements
        Map<Exp, Exp> replacementMap = new HashMap<>();
        replacementMap.put(varExp, assignExp);

        // Loop through each verification condition and replace the variable
        // expression with the assign expression wherever possible.
        List<VerificationCondition> newVCs = createReplacementVCs(myCurrentAssertiveCodeBlock.getVCs(), replacementMap);

        // Store the new list of vcs
        myCurrentAssertiveCodeBlock.setVCs(newVCs);

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
        return "Function Assignment Rule";
    }

}
