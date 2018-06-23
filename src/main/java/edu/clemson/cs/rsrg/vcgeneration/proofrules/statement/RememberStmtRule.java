/*
 * RememberStmtRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.statement;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.treewalk.TreeWalker;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationCondition;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import edu.clemson.cs.rsrg.vcgeneration.utilities.treewalkers.GenerateRememberRuleSubstitutionMap;
import java.util.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for applying the {@code remember}
 * rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class RememberStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application of the {@code remember}
     * rule.</p>
     *
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param context The verification context that contains all
     *                the information we have collected so far.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public RememberStmtRule(AssertiveCodeBlock block,
            VerificationContext context, STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        // Retrieve the list of VCs and use the sequent
        // substitution map to do replacements.
        List<VerificationCondition> vcs = myCurrentAssertiveCodeBlock.getVCs();
        List<VerificationCondition> newVCs = new ArrayList<>(vcs.size());
        for (VerificationCondition vc : vcs) {
            newVCs.add(new VerificationCondition(vc.getLocation(), vc.getName(),
                    createReplacementSequent(vc.getSequent()), vc.getHasImpactingReductionFlag(),
                    vc.getLocationDetailModel()));
        }

        // Store the new list of vcs
        myCurrentAssertiveCodeBlock.setVCs(newVCs);

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
        return "Remember Rule";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method that uses the {@link GenerateRememberRuleSubstitutionMap}
     * walker to generate {@link Exp Exps} that result from applying
     * the {@code Remember} rule for each of the {@link Exp} in
     * the {@link Sequent}.</p>
     *
     * @param s The original {@link Sequent}.
     *
     * @return A modified {@link Sequent}.
     */
    private Sequent createReplacementSequent(Sequent s) {
        List<Exp> newAntecedents = new ArrayList<>();
        List<Exp> newConsequents = new ArrayList<>();

        for (Exp antecedent : s.getAntecedents()) {
            // Use the helper walker to generate the "remember"
            // expression for the antecedent.
            GenerateRememberRuleSubstitutionMap expMapGenerator =
                    new GenerateRememberRuleSubstitutionMap(antecedent);
            TreeWalker.visit(expMapGenerator, antecedent);
            newAntecedents.add(antecedent.substitute(expMapGenerator.getSubstitutionMap()));
        }

        for (Exp consequent : s.getConcequents()) {
            // Use the helper walker to generate the "remember"
            // expression for the consequent.
            GenerateRememberRuleSubstitutionMap expMapGenerator =
                    new GenerateRememberRuleSubstitutionMap(consequent);
            TreeWalker.visit(expMapGenerator, consequent);
            newConsequents.add(consequent.substitute(expMapGenerator.getSubstitutionMap()));
        }

        return new Sequent(s.getLocation(), newAntecedents, newConsequents);
    }
}