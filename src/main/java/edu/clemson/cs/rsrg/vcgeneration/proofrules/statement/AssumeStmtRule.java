/*
 * AssumeStmtRule.java
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

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.cs.rsrg.vcgeneration.sequents.SequentReduction;
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductiontree.ReductionTreeDotExporter;
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductiontree.ReductionTreeExporter;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationCondition;
import java.util.*;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for applying the {@code assume}
 * rule to an {@link AssumeStmt}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class AssumeStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@link AssumeStmt} we are applying the rule to.</p> */
    private final AssumeStmt myAssumeStmt;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application of the {@code assume}
     * rule.</p>
     *
     * @param assumeStmt The {@link AssumeStmt} we are applying
     *                   the rule to.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public AssumeStmtRule(AssumeStmt assumeStmt, AssertiveCodeBlock block,
            STGroup stGroup, ST blockModel) {
        super(block, stGroup, blockModel);
        myAssumeStmt = assumeStmt;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");

        // Check to see if this assume can be simplified or not.
        if (VarExp.isLiteralTrue(myAssumeStmt.getAssertion())) {
            // Add the different details to the various different output models
            stepModel.add("proofRuleName",
                    getRuleDescription() + " and Simplified").add(
                    "currentStateOfBlock", myCurrentAssertiveCodeBlock);
        }
        // Check to see if this is a "Stipulate Assume"
        else if (myAssumeStmt.getIsStipulate()) {
            // Retrieve the expression inside the assume
            Exp assumeExp = myAssumeStmt.getAssertion();

            // Build the new list of VCs
            List<VerificationCondition> newVCs = new ArrayList<>();
            for (VerificationCondition vc : myCurrentAssertiveCodeBlock.getVCs()) {
                List<Sequent> newSequents = new ArrayList<>();
                for (Sequent sequent : vc.getAssociatedSequents()) {
                    // YS: The reducedSequentForm combines both the Substitution and
                    //     Stipulate Application Steps.
                    newSequents.addAll(reducedSequentForm(sequent, assumeExp.clone(), stepModel));
                }

                newVCs.add(new VerificationCondition(vc.getLocation(), vc.getName(), newSequents));
            }

            // Set this as our new list of vcs
            myCurrentAssertiveCodeBlock.setVCs(newVCs);

            stepModel.add("proofRuleName", getRuleDescription()).add(
                    "currentStateOfBlock", myCurrentAssertiveCodeBlock);
        }
        /*else {
            // Retrieve the expression inside the assume
            Exp assumeExp = myAssumeStmt.getAssertion();

            // TODO: Apply the various sequent reduction rules.
            List<Exp> assumeExps =
                    splitConjunctExp(assumeExp, new ArrayList<Exp>());

            // Set this as our new list of sequents
            List<Sequent> newSequents = formParsimoniousVC(assumeExps);
            myCurrentAssertiveCodeBlock.setSequents(newSequents);

            stepModel.add("proofRuleName", getRuleDescription()).add(
                    "currentStateOfBlock", myCurrentAssertiveCodeBlock);
        }*/

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
        return "Assume Rule";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This method adds the {@code assumeExp} to {@code sequent} and
     * and produces list of reduced {@code sequents}.</p>
     *
     * @param sequent Original {@link Sequent}.
     * @param assumeExp The new {@code assume} {@link Exp}.
     * @param stepModel The model associated with this step.
     *
     * @return A list of reduced {@link Sequent Sequents}.
     */
    private List<Sequent> reducedSequentForm(Sequent sequent, Exp assumeExp,
            ST stepModel) {
        // Create the sequent to be reduced.
        List<Exp> newAntecedents = sequent.getAntecedents();
        newAntecedents.add(assumeExp);
        List<Exp> consequents = sequent.getConcequents();
        Sequent sequentToBeReduced =
                new Sequent(sequent.getLocation(), newAntecedents, consequents);

        // Apply the various sequent reduction rules.
        SequentReduction reduction = new SequentReduction(sequentToBeReduced);
        List<Sequent> resultSequents = reduction.applyReduction();
        DirectedGraph<Sequent, DefaultEdge> reductionTree =
                reduction.getReductionTree();

        // Output the reduction tree as a dot file to the step model
        ReductionTreeExporter treeExporter = new ReductionTreeDotExporter();
        stepModel.add("reductionTrees", treeExporter.output(reductionTree));

        return resultSequents;
    }
}