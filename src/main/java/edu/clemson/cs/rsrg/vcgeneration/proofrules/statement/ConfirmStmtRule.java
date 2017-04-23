/*
 * ConfirmStmtRule.java
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
import edu.clemson.cs.rsrg.absyn.statements.ConfirmStmt;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.cs.rsrg.vcgeneration.sequents.SequentReduction;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationCondition;
import java.io.StringWriter;
import java.util.*;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.graph.DefaultEdge;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for applying the {@code confirm}
 * rule to a {@link ConfirmStmt}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ConfirmStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@link ConfirmStmt} we are applying the rule to.</p> */
    private final ConfirmStmt myConfirmStmt;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application of the {@code confirm}
     * rule.</p>
     *
     * @param confirmStmt The {@link ConfirmStmt} we are applying
     *                    the rule to.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public ConfirmStmtRule(ConfirmStmt confirmStmt, AssertiveCodeBlock block,
            STGroup stGroup, ST blockModel) {
        super(block, stGroup, blockModel);
        myConfirmStmt = confirmStmt;
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

        // Check to see if this confirm can be simplified or not.
        if (myConfirmStmt.getSimplify()) {
            // Add the different details to the various different output models
            stepModel.add("proofRuleName", getRuleDescription() + " and Simplified")
                    .add("currentStateOfBlock", myCurrentAssertiveCodeBlock);
        }
        else {
            // Build the new list of VCs
            List<VerificationCondition> newVCs = new ArrayList<>();
            newVCs.addAll(myCurrentAssertiveCodeBlock.getVCs());
            newVCs.addAll(buildNewVCs(stepModel));

            // Set this as our new list of vcs
            myCurrentAssertiveCodeBlock.setVCs(newVCs);

            stepModel.add("proofRuleName", getRuleDescription())
                    .add("currentStateOfBlock", myCurrentAssertiveCodeBlock);
        }

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
        return "Confirm Rule";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method that builds a list of new {@code VCs}.</p>
     *
     * @param stepModel The model associated with this step.
     *
     * @return A list of {@link VerificationCondition VCs}.
     */
    private List<VerificationCondition> buildNewVCs(ST stepModel) {
        // Apply the various sequent reduction rules to the expressions
        // in the confirm statement.
        Sequent sequentToBeReduced =
                new Sequent(myConfirmStmt.getLocation(), new ArrayList<Exp>(),
                        Collections.singletonList(myConfirmStmt.getAssertion()));
        SequentReduction reduction =
                new SequentReduction(sequentToBeReduced);
        List<Sequent> resultSequents = reduction.applyReduction();
        DirectedGraph<Sequent, DefaultEdge> reductionTree = reduction.getReductionTree();

        // YS: The confirm statement always generate new VCs,
        // so we will need to use the reduction tree to determine
        // how many new VCs to create.
        List<VerificationCondition> vcs = new ArrayList<>();
        Map<Sequent, List<Sequent>> sequentListMap =
                buildAssociatedSequentsMap(reductionTree, resultSequents);
        for (Sequent s : sequentListMap.keySet()) {
            // Form a list with all associated sequents
            List<Sequent> associatedSequents = new ArrayList<>();
            associatedSequents.add(s);
            associatedSequents.addAll(sequentListMap.get(s));

            // Create a new VC
            vcs.add(new VerificationCondition(myConfirmStmt.getLocation(), associatedSequents));
        }

        // Output the reduction tree as a dot file to the step model
        StringWriter writer = new StringWriter();
        DOTExporter<Sequent, DefaultEdge> dotExporter = new DOTExporter<>();
        dotExporter.exportGraph(reductionTree, writer);
        stepModel.add("reductionTree", writer.toString());

        return vcs;
    }

    /**
     * <p>An helper method to build a map of {@code sequent} to its associated
     * {@code sequents}.</p>
     *
     * @param reductionTree A {@link DirectedGraph} representing a reduction tree.
     * @param sequents A list of {@link Sequent Sequents}.
     *
     * @return A map from {@link Sequent} to list of {@link Sequent Sequents}.
     */
    private Map<Sequent, List<Sequent>> buildAssociatedSequentsMap(
            DirectedGraph<Sequent, DefaultEdge> reductionTree, List<Sequent> sequents) {
        Map<Sequent, List<Sequent>> sequentListMap = new LinkedHashMap<>();

        // Create a boolean array to store whether or not
        // sequent corresponding to the index has been processed.
        boolean[] processed = new boolean[sequents.size()];
        Arrays.fill(processed, false);

        // Loop through all the sequents
        for (int i = 0; i < sequents.size(); i++) {
            // Only deal with the ones we haven't processed
            if (!processed[i]) {
                Sequent sequentAtI = sequents.get(i);

                // Form a list with all associated sequents
                List<Sequent> associatedSequents = new ArrayList<>();
                for (int j = i+1; j < sequents.size(); j++) {
                    Sequent sequentAtJ = sequents.get(j);
                    if (Utilities.pathExist(reductionTree, sequentAtI, sequentAtJ)) {
                        associatedSequents.add(sequentAtJ);
                        processed[j] = true;
                    }
                }

                // Put sequentAtI and its associated sequents in our map.
                sequentListMap.put(sequentAtI, associatedSequents);
                processed[i] = true;
            }
        }

        return sequentListMap;
    }
}