/*
 * WhileStmtRule.java
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

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramVariableExp;
import edu.clemson.cs.rsrg.absyn.items.mathitems.LoopVerificationItem;
import edu.clemson.cs.rsrg.absyn.items.programitems.IfConditionItem;
import edu.clemson.cs.rsrg.absyn.statements.*;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VCConfirmStmt;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationCondition;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for applying the {@code while}
 * rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class WhileStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>The module scope for the file we are generating
     * {@code VCs} for.</p>
     */
    private final ModuleScope myCurrentModuleScope;

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    /** <p>The {@link WhileStmt} we are applying the rule to.</p> */
    private final WhileStmt myWhileStmt;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application of the {@code while}
     * rule.</p>
     *
     * @param whileStmt The {@link WhileStmt} we are applying
     *                  the rule to.
     * @param moduleScope The current module scope we are visiting.
     * @param g The current type graph.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public WhileStmtRule(WhileStmt whileStmt, ModuleScope moduleScope,
            TypeGraph g, AssertiveCodeBlock block, STGroup stGroup,
            ST blockModel) {
        super(block, stGroup, blockModel);
        myCurrentModuleScope = moduleScope;
        myTypeGraph = g;
        myWhileStmt = whileStmt;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        LoopVerificationItem whileLoopItem = myWhileStmt.getLoopVerificationBlock();

        // Create a statement confirming the loop invariant and
        // add it to the assertive code block.
        // TODO: Check to see what happens when the loop invariant contains a which_entails. - YS
        AssertionClause loopInvariantClause = whileLoopItem.getMaintainingClause();
        ConfirmStmt confirmStmt = new ConfirmStmt(loopInvariantClause.getLocation().clone(),
                loopInvariantClause.getAssertionExp().clone(), false);
        myCurrentAssertiveCodeBlock.addStatement(confirmStmt);
        myLocationDetails.put(confirmStmt.getLocation(), "Base Case of the Invariant of While Statement");

        // NY YS
        // TODO: Obtain the elapsed time duration of loop

        // NY YS
        // TODO: Confirm that elapsed time is 0.0

        // Create the change statement with the variables that are changing.
        List<ProgramVariableExp> changingVars = whileLoopItem.getChangingVars();
        List<Exp> changingVarsAsMathExp = new ArrayList<>(changingVars.size());
        for (ProgramVariableExp exp : changingVars) {
            changingVarsAsMathExp.add(Utilities.convertExp(exp, myCurrentModuleScope));
        }
        myCurrentAssertiveCodeBlock.addStatement(
                new ChangeStmt(whileLoopItem.getLocation().clone(), changingVarsAsMathExp));

        // NQV for P_Val
        AssertionClause decreasingClause = whileLoopItem.getDecreasingClause();
        VCVarExp nqvPValExp = Utilities.createVCVarExp(myCurrentAssertiveCodeBlock,
                Utilities.createPValExp(decreasingClause.getLocation().clone(), myCurrentModuleScope));

        // Create a statement assuming the invariant and NQV(RS, P_Val) = P_Exp
        EqualsExp equalsPExp = new EqualsExp(decreasingClause.getLocation().clone(),
                nqvPValExp.clone(), null, EqualsExp.Operator.EQUAL,
                decreasingClause.getAssertionExp().clone());
        equalsPExp.setMathType(myTypeGraph.BOOLEAN);
        myCurrentAssertiveCodeBlock.addStatement(
                new AssumeStmt(myWhileStmt.getLocation().clone(),
                        InfixExp.formConjunct(myWhileStmt.getLocation().clone(),
                                loopInvariantClause.getAssertionExp().clone(), equalsPExp), false));

        // NY YS
        // TODO: Also assume NQV(RP, Cum_Dur) = El_Dur_Exp

        // YS: We deep copy the statements just in case we run into aliasing problems.
        List<Statement> ifStmts = new ArrayList<>(myWhileStmt.getStatements().size() + 1);
        for (Statement s : myWhileStmt.getStatements()) {
            ifStmts.add(s.clone());
        }

        // Confirm the inductive case of invariant and P_Exp <= 1 + NQV(RS, P_Val) (termination)
        IntegerExp oneExp = new IntegerExp(decreasingClause.getLocation().clone(), null, 1);
        oneExp.setMathType(decreasingClause.getAssertionExp().getMathType());

        InfixExp sumExp = new InfixExp(decreasingClause.getLocation().clone(), oneExp, null,
                new PosSymbol(decreasingClause.getLocation().clone(), "+"), nqvPValExp.clone());
        sumExp.setMathType(decreasingClause.getAssertionExp().getMathType());

        InfixExp terminationExp = new InfixExp(decreasingClause.getLocation().clone(),
                decreasingClause.getAssertionExp().clone(), null,
                new PosSymbol(decreasingClause.getLocation().clone(), "<="), sumExp);
        terminationExp.setMathType(myTypeGraph.BOOLEAN);
        myLocationDetails.put(terminationExp.getLocation(), "Termination of While Statement");

        Exp invariantAsExp = loopInvariantClause.getAssertionExp().clone();
        myLocationDetails.put(invariantAsExp.getLocation(), "Inductive Case of Invariant of While Statement");

        // YS: Simplify if we have "maintaining true"
        ifStmts.add(new ConfirmStmt(loopInvariantClause.getLocation().clone(),
                InfixExp.formConjunct(myWhileStmt.getLocation().clone(), invariantAsExp, terminationExp),
                MathExp.isLiteralTrue(invariantAsExp)));

        // If part should contain the original statements plus a ConfirmStmt that ensures
        // the invariant and the decreasing clause holds.
        IfConditionItem ifPart = new IfConditionItem(myWhileStmt.getLocation().clone(),
                myWhileStmt.getTest(), ifStmts);

        // Else part should only contain the original sequents in a special VCConfirmStmt
        List<Statement> elsePart = new ArrayList<>(1);
        elsePart.add(new VCConfirmStmt(myWhileStmt.getLocation().clone(),
                myCurrentAssertiveCodeBlock.getVCs()));

        // Create the replacing If-Else statement.
        myCurrentAssertiveCodeBlock.addStatement(
                new IfStmt(myWhileStmt.getLocation().clone(), ifPart,
                        new ArrayList<IfConditionItem>(), elsePart));

        // Store an empty list of vcs.
        myCurrentAssertiveCodeBlock.setVCs(new LinkedList<VerificationCondition>());

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
        return "While Rule";
    }

}