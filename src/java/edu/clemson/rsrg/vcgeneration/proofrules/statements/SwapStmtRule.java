/*
 * SwapStmtRule.java
 * ---------------------------------
 * Copyright (c) 2024
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
import edu.clemson.rsrg.absyn.expressions.programexpr.ProgramVariableExp;
import edu.clemson.rsrg.absyn.statements.SwapStmt;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationCondition;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>
 * This class contains the logic for applying the {@code swap} rule.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class SwapStmtRule extends AbstractProofRuleApplication implements ProofRuleApplication {

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
     * The {@link SwapStmt} we are applying the rule to.
     * </p>
     */
    private final SwapStmt mySwapStmt;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a new application of the {@code swap} rule.
     * </p>
     *
     * @param swapStmt
     *            The {@link SwapStmt} we are applying the rule to.
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
    public SwapStmtRule(SwapStmt swapStmt, ModuleScope moduleScope, AssertiveCodeBlock block,
            VerificationContext context, STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myCurrentModuleScope = moduleScope;
        mySwapStmt = swapStmt;
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
        // Retrieve the list of VCs and use the
        // substitution map to do replacements.
        List<VerificationCondition> vcs = myCurrentAssertiveCodeBlock.getVCs();
        List<VerificationCondition> newVCs = new ArrayList<>(vcs.size());
        for (VerificationCondition vc : vcs) {
            newVCs.add(new VerificationCondition(vc.getLocation(), vc.getName(), performSwap(vc.getSequent()),
                    vc.getHasImpactingReductionFlag(), vc.getLocationDetailModel()));
        }

        // NY YS
        // TODO: Duration for swap statements

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
        return "Swap Rule";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method that {@code swap} the expression on the left of a {@link SwapStmt} with the right to all
     * antecedents and consequents of {@code s} and generate a replacement sequent.
     * </p>
     *
     * @param s
     *            The original {@link Sequent}.
     *
     * @return A modified {@link Sequent}.
     */
    private Sequent performSwap(Sequent s) {
        List<Exp> newAntecedents = new ArrayList<>();
        List<Exp> newConsequents = new ArrayList<>();

        // Substitution maps
        // YS: Need 3 substitution maps, because we don't
        // want to replace everything from the left expression
        // to the right expression and subsequently replace all
        // of them back to left expression.
        Map<Exp, Exp> substitutionMap1 = new HashMap<>();
        Map<Exp, Exp> substitutionMap2 = new HashMap<>();
        Map<Exp, Exp> substitutionMap3 = new HashMap<>();

        // Convert these to their MathExp counterparts
        ProgramVariableExp stmtLeft = mySwapStmt.getLeft();
        Exp expLeft = Utilities.convertExp(stmtLeft, myCurrentModuleScope);
        ProgramVariableExp stmtRight = mySwapStmt.getRight();
        Exp expRight = Utilities.convertExp(stmtRight, myCurrentModuleScope);

        // Temp variable
        PosSymbol tmpLeftName = Utilities.getVarName(stmtLeft);
        VarExp tmp = Utilities.createVarExp(stmtLeft.getLocation(), null,
                new PosSymbol(stmtLeft.getLocation(), "_" + tmpLeftName.getName()), stmtLeft.getMathType(),
                stmtRight.getMathTypeValue());

        // Map 1: expLeft ~> tmp
        // Map 2: expRight ~> expLeft
        // Map 3: tmp ~> expRight
        substitutionMap1.put(expLeft, tmp);
        substitutionMap2.put(expRight, expLeft);
        substitutionMap3.put(tmp, expRight);

        // Replace in antecedents.
        for (Exp antecedent : s.getAntecedents()) {
            Exp newAntencedent = antecedent.substitute(substitutionMap1);
            newAntencedent = newAntencedent.substitute(substitutionMap2);
            newAntencedent = newAntencedent.substitute(substitutionMap3);

            newAntecedents.add(newAntencedent);
        }

        // Replace in consequents.
        for (Exp consequent : s.getConcequents()) {
            Exp newConsequent = consequent.substitute(substitutionMap1);
            newConsequent = newConsequent.substitute(substitutionMap2);
            newConsequent = newConsequent.substitute(substitutionMap3);

            newConsequents.add(newConsequent);
        }

        return new Sequent(s.getLocation(), newAntecedents, newConsequents);
    }
}
