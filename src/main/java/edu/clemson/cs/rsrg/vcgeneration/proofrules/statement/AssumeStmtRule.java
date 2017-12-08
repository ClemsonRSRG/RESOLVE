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
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.EqualsExp.Operator;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.treewalk.TreeWalker;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import edu.clemson.cs.rsrg.vcgeneration.sequents.SequentReduction;
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductiontree.ReductionTreeDotExporter;
import edu.clemson.cs.rsrg.vcgeneration.sequents.reductiontree.ReductionTreeExporter;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationCondition;
import edu.clemson.cs.rsrg.vcgeneration.utilities.treewalkers.UniqueSymbolNameExtractor;
import java.util.*;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
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
        String ruleName = getRuleDescription();

        // Check to see if this assume can be simplified or not.
        if (VarExp.isLiteralTrue(myAssumeStmt.getAssertion())) {
            // Don't need to do anything here. We simply ignore this
            // assertion.
            ruleName = ruleName + " and Simplified";
        }
        else {
            // This is the stipulate assume rule.
            if (myAssumeStmt.getIsStipulate()) {
                ruleName = "Stipulate " + ruleName;
            }

            // Retrieve the expression inside the assume
            Exp assumeExp = myAssumeStmt.getAssertion();

            // YS: In order to apply the "Substitution Step",
            // we will need a list of replaceable expressions.
            // Once we have done the substitutions, if this
            // statement is a Stipulate Assume, then we keep
            // the remaining expressions no matter what, else
            // it will only add it to the sequent if there are
            // common symbols.
            List<Exp> assumeExps =
                    splitConjunctExp(assumeExp, new ArrayList<Exp>());

            // YS: We really want to record the split into conjuncts
            // as some kind of reduction, so we build a reduction tree
            // ourselves.
            if (assumeExps.size() != 1) {
                DirectedGraph<Sequent, DefaultEdge> reductionTree =
                        new DefaultDirectedGraph<>(DefaultEdge.class);

                // Create a root node using the original assumeExp and
                // a children node using the assumeExps list. Then create
                // and edge to indicate a reduction.
                Sequent rootSequent =
                        new Sequent(myAssumeStmt.getLocation(),
                                Collections.singletonList(assumeExp), new ArrayList<Exp>());
                Sequent updatedSequent =
                        new Sequent(myAssumeStmt.getLocation(),
                                assumeExps, new ArrayList<Exp>());
                reductionTree.addVertex(rootSequent);
                reductionTree.addVertex(updatedSequent);
                reductionTree.addEdge(rootSequent, updatedSequent);

                // Export the tree
                ReductionTreeExporter treeExporter = new ReductionTreeDotExporter();
                stepModel.add("reductionTrees", treeExporter.output(reductionTree));
            }

            // Build the new list of VCs
            List<VerificationCondition> newVCs = new ArrayList<>();
            for (VerificationCondition vc : myCurrentAssertiveCodeBlock.getVCs()) {
                List<Sequent> newSequents = new ArrayList<>();
                for (Sequent sequent : vc.getAssociatedSequents()) {
                    // YS: The substitutionStep will call applicationStep
                    Sequent resultSequent =
                            substitutionStep(sequent, assumeExps);

                    // Reduce the sequent and store this as
                    // our new list of associated sequents.
                    newSequents.addAll(reducedSequentForm(resultSequent, stepModel));
                }

                newVCs.add(new VerificationCondition(vc.getLocation(), vc.getName(),
                        newSequents, vc.getLocationDetailModel()));
            }

            // Set this as our new list of vcs
            myCurrentAssertiveCodeBlock.setVCs(newVCs);
        }

        // Add the different details to the various different output models
        stepModel.add("proofRuleName", ruleName).add(
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
        return "Assume Rule";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that checks to see if the given assume expression
     * can be used to prove anything in {@code seq}. This is done by finding the
     * intersection between the set of symbols in the assume expression and
     * the set of symbols in {@code seq}.</p>
     *
     * <p>For a stipulate assume statement, we keep the remaining
     * then we keep all the remaining assume expressions no matter what.</p>
     *
     * <p>For a regular assume statement, we loop though keep looping through
     * the remaining assume expressions until we stop adding more expression
     * to our antecedent.</p>
     *
     * @param seq The current {@link Sequent} we are processing.
     * @param remAssumeExpList The list of remaining assume expressions.
     *
     * @return The modified {@link Sequent}.
     */
    private Sequent applicationStep(Sequent seq, List<Exp> remAssumeExpList) {
        List<Exp> seqAntecedents = new ArrayList<>(seq.getAntecedents());
        List<Exp> seqConsequents = new ArrayList<>(seq.getConcequents());

        // If it is stipulate statement, keep it no matter what
        if (myAssumeStmt.getIsStipulate()) {
            seqAntecedents.addAll(remAssumeExpList);
        }
        else {
            // This boolean condition will store whether or
            // not we keep checking the remAssumeExpList for
            // more potential antecedents. To start of, if
            // the list is empty, the obvious answer is no.
            boolean checkForMoreAntecedents = !remAssumeExpList.isEmpty();

            // Loop until we no longer add more expressions or
            // we have added all expressions in the remaining
            // assume expression list.
            while (checkForMoreAntecedents) {
                List<Exp> tmpExpList = new ArrayList<>();

                // Check to see if we have added something to our
                // antecedent set.
                boolean addedToAntecendentSet = false;
                for (Exp assumeExp : remAssumeExpList) {
                    // Get the set of symbols in the antecedent
                    Set<String> symbolsInSeq = new LinkedHashSet<>();
                    for (Exp antecedentExp : seqAntecedents) {
                        // Use the symbol name extractor to retrieve
                        // unique symbol names.
                        UniqueSymbolNameExtractor symbolNameExtractor =
                                new UniqueSymbolNameExtractor();
                        TreeWalker.visit(symbolNameExtractor, antecedentExp);
                        symbolsInSeq.addAll(symbolNameExtractor.getSymbols());
                    }

                    // Get the set of symbols in the consequent
                    for (Exp consequentExp : seqConsequents) {
                        // Use the symbol name extractor to retrieve
                        // unique symbol names.
                        UniqueSymbolNameExtractor symbolNameExtractor =
                                new UniqueSymbolNameExtractor();
                        TreeWalker.visit(symbolNameExtractor, consequentExp);
                        symbolsInSeq.addAll(symbolNameExtractor.getSymbols());
                    }

                    // Add this as a new antecedent if there are common symbols
                    // in the assume expression and in the sequent. (Parsimonious step)
                    Set<String> intersection = new LinkedHashSet<>(symbolsInSeq);
                    UniqueSymbolNameExtractor symbolNameExtractor =
                            new UniqueSymbolNameExtractor();
                    TreeWalker.visit(symbolNameExtractor, assumeExp);
                    intersection.retainAll(symbolNameExtractor.getSymbols());

                    // There are common symbols!
                    if (!intersection.isEmpty()) {
                        // Don't add this as an antecedent if we have "Assume true"
                        if (!VarExp.isLiteralTrue(assumeExp)) {
                            seqAntecedents.add(assumeExp.clone());
                            addedToAntecendentSet = true;
                        }
                    }
                    // There are no common symbols!
                    else {
                        // Add this as a new antecedent if we have "Assume false"
                        if (VarExp.isLiteralFalse(assumeExp)) {
                            seqAntecedents.add(assumeExp.clone());
                            addedToAntecendentSet = true;
                        }
                        // We might need to check this again if in the future
                        // another expression in remAssumeExpList makes this
                        // assumeExp have common symbols with anything in our sequent.
                        else {
                            tmpExpList.add(assumeExp);
                        }
                    }
                }

                // Use whatever we have in tmpExpList as our new
                // remAssumeExpList
                remAssumeExpList = tmpExpList;
                if (remAssumeExpList.size() > 0) {
                    // Check to see if we added something to as
                    // a new antecedent. If we did, then we will
                    // need to loop again to see if we can add any
                    // more. If we didn't, then none of the remaining
                    // expressions will be helpful.
                    checkForMoreAntecedents = addedToAntecendentSet;
                } else {
                    // Since we are done with all assume expressions,
                    // we can quit out of the loop.
                    checkForMoreAntecedents = false;
                }
            }
        }

        return new Sequent(seq.getLocation(), seqAntecedents, seqConsequents);
    }

    /**
     * <p>This method checks to see if this the expression we passed
     * is either a variable expression or a dotted expression that
     * contains a variable expression in the last position.</p>
     *
     * @param exp The checking expression.
     *
     * @return {@code true} if is an expression we can replace, {@code false} otherwise.
     */
    private boolean containsReplaceableExp(Exp exp) {
        boolean retVal = false;

        // Case #1: VarExp
        if (exp instanceof VarExp) {
            retVal = true;
        }
        // Case #2: DotExp
        else if (exp instanceof DotExp) {
            DotExp dotExp = (DotExp) exp;
            List<Exp> dotExpList = dotExp.getSegments();
            retVal =
                    containsReplaceableExp(dotExpList
                            .get(dotExpList.size() - 1));
        }
        // Case #3: VCVarExp
        else if (exp instanceof VCVarExp) {
            retVal = containsReplaceableExp(((VCVarExp) exp).getExp());
        }

        return retVal;
    }

    /**
     * <p>Rather than using the strict {@code equals} method that is defined for
     * {@link Exp Exps}, this method checks to see if all {@link Exp Exps}
     * are {@code equivalent}.</p>
     *
     * @param originalExpList The original expression list.
     * @param newExpList The new expression list.
     *
     * @return {@code true} if the lists contain {@code equivalent} {@link Exp Exps},
     * {@code false} otherwise.
     */
    private boolean isEquivalentExpList(List<Exp> originalExpList,
            List<Exp> newExpList) {
        boolean isEquivalent = (originalExpList.size() == newExpList.size());

        Iterator<Exp> originalExpIt = originalExpList.iterator();
        Iterator<Exp> newExpIt = newExpList.iterator();
        while (originalExpIt.hasNext() && isEquivalent) {
            Exp originalExp = originalExpIt.next();
            Exp newExp = newExpIt.next();
            isEquivalent = originalExp.equivalent(newExp);
        }

        return isEquivalent;
    }

    /**
     * <p>This method uses {@code sequent} to produce
     * a list of reduced {@link Sequent Sequents}.</p>
     *
     * @param sequent Original {@link Sequent}.
     * @param stepModel The model associated with this step.
     *
     * @return A list of reduced {@link Sequent Sequents}.
     */
    private List<Sequent> reducedSequentForm(Sequent sequent, ST stepModel) {
        // Apply the various sequent reduction rules.
        SequentReduction reduction = new SequentReduction(sequent);
        List<Sequent> resultSequents = reduction.applyReduction();
        DirectedGraph<Sequent, DefaultEdge> reductionTree =
                reduction.getReductionTree();

        // Output the reduction tree as a dot file to the step model
        // only if we did some kind of reduction.
        if (!reductionTree.edgeSet().isEmpty()) {
            ReductionTreeExporter treeExporter = new ReductionTreeDotExporter();
            stepModel.add("reductionTrees", treeExporter.output(reductionTree));
        }

        return resultSequents;
    }

    /**
     * <p>Takes an expression and split it by the {@code and}
     * operator.</p>
     *
     * @param exp Expression to be split.
     * @param expList List of expressions that have been split already.
     *
     * @return A list containing {@code expList} and the split result
     * of {@code exp}.
     */
    private List<Exp> splitConjunctExp(Exp exp, List<Exp> expList) {
        // Attempt to split the expression if it contains a conjunct
        if (exp instanceof InfixExp) {
            InfixExp infixExp = (InfixExp) exp;

            // Split the expression if it is a conjunct
            if (infixExp.getOperatorAsString().equals("and")) {
                expList = splitConjunctExp(infixExp.getLeft(), expList);
                expList = splitConjunctExp(infixExp.getRight(), expList);
            }
            // Otherwise simply add it to our list
            else {
                expList.add(infixExp);
            }
        }
        else if (exp instanceof BetweenExp) {
            expList.addAll(((BetweenExp) exp).getJoiningExps());
        }
        // Otherwise it is an individual assume statement we need to deal with.
        else {
            expList.add(exp);
        }

        return expList;
    }

    /**
     * <p>This method performs the substitutions on each of the expressions in the
     * given list.</p>
     *
     * @param expressions List of {@link Exp Exps}.
     * @param replacements A map of substitutions.
     *
     * @return A modified list of {@link Exp Exps}.
     */
    private List<Exp> substituteExps(List<Exp> expressions, Map<Exp, Exp> replacements) {
        List<Exp> newExps = new ArrayList<>();
        for (Exp exp : expressions) {
            newExps.add(exp.substitute(replacements));
        }

        return newExps;
    }

    /**
     * <p>This is a helper method that checks to see if the any of the assume expressions
     * can be substituted. If it can, it will generate a new sequent. If it cannot, the original
     * sequent will be kept. Once this step is over, it will call {@link #applicationStep(Sequent, List)}
     * to generate the final {@link Sequent}.</p>
     *
     * @param seq The current {@link Sequent} we are processing.
     * @param assumeExpList A list of {@link Exp Exps} being assumed.
     *
     * @return The modified {@link Sequent}.
     */
    private Sequent substitutionStep(Sequent seq, List<Exp> assumeExpList) {
        // New antecedent and consequent expressions
        List<Exp> newAntecedents = new ArrayList<>(seq.getAntecedents());
        List<Exp> newConsequents = new ArrayList<>(seq.getConcequents());

        // Make a deep copy of the assume expression list
        List<Exp> assumeExpCopyList = new ArrayList<>();
        for (Exp assumeExp : assumeExpList) {
            assumeExpCopyList.add(assumeExp.clone());
        }

        // Stores the remaining assume expressions
        // we have not substituted. Note that if the expression
        // is part of a stipulate assume statement, we keep
        // the assume no matter what.
        List<Exp> remAssumeExpList = new ArrayList<>();

        // Loop through each assume expression
        for (int j = 0; j < assumeExpCopyList.size(); j++) {
            Exp currentAssumeExp = assumeExpCopyList.get(j);
            boolean hasVerificationVar = false;
            boolean isConceptualVar = false;

            // Substitution map
            Map<Exp, Exp> substitutions = new HashMap<>();

            // Attempts to simplify equality expressions
            if (currentAssumeExp instanceof EqualsExp
                    && ((EqualsExp) currentAssumeExp).getOperator() == Operator.EQUAL) {
                EqualsExp equalsExp = (EqualsExp) currentAssumeExp;
                boolean isLeftReplaceable = containsReplaceableExp(equalsExp.getLeft());
                boolean isRightReplaceable = containsReplaceableExp(equalsExp.getRight());

                // Check to see if we have P_Val or Cum_Dur
                if (equalsExp.getLeft().containsVar("P_Val", false) ||
                        equalsExp.getLeft().containsVar("Cum_Dur", false)) {
                    hasVerificationVar = true;
                }
                // Check to see if we have Conc.[expression]
                else if (equalsExp.getLeft() instanceof DotExp) {
                    DotExp tempLeft = (DotExp) equalsExp.getLeft();
                    isConceptualVar = tempLeft.containsVar("Conc", false);
                }

                // Check if both the left and right are replaceable
                if (isLeftReplaceable && isRightReplaceable) {
                    // Only check for verification variable on the left
                    // hand side. If that is the case, we know the
                    // right hand side is the only one that makes sense
                    // in the current context, therefore we do the
                    // substitution.
                    if (hasVerificationVar || isConceptualVar) {
                        substitutions.put(equalsExp.getLeft(), equalsExp.getRight());
                    }
                }
                // Check if left hand side is replaceable
                else if (isLeftReplaceable) {
                    // Add to substitutions where left is replaced with the right
                    substitutions.put(equalsExp.getLeft(), equalsExp.getRight());
                }
                // Only right hand side is replaceable
                else if (isRightReplaceable) {
                    // Add to substitutions where right is replaced with the left
                    substitutions.put(equalsExp.getRight(), equalsExp.getLeft());
                }
            }

            // Replace all instances of the left side in
            // the assume expressions we have already processed.
            for (int k = 0; k < remAssumeExpList.size(); k++) {
                Exp newAssumeExp = remAssumeExpList.get(k).substitute(substitutions);
                remAssumeExpList.set(k, newAssumeExp);
            }

            // Replace all instances of the left side in
            // the assume expressions we haven't processed.
            for (int k = j + 1; k < assumeExpCopyList
                    .size(); k++) {
                Exp newAssumeExp = assumeExpCopyList.get(k).substitute(substitutions);
                assumeExpCopyList.set(k, newAssumeExp);
            }

            List<Exp> antencedentsSubtituted = substituteExps(newAntecedents, substitutions);
            List<Exp> consequentsSubtituted = substituteExps(newConsequents, substitutions);

            // Check to see if this is a stipulate assume clause
            // If yes, then we will have to add it for further processing
            // regardless if we did a substitution or not.
            if (myAssumeStmt.getIsStipulate()) {
                remAssumeExpList.add(currentAssumeExp.clone());
            }
            else {
                // Check to see if there is no change to the new antecedents
                // and consequents. If not, then we might to keep this assumed
                // expression for further processing.
                if (isEquivalentExpList(antencedentsSubtituted, newAntecedents) &&
                        isEquivalentExpList(consequentsSubtituted, newConsequents)) {
                    // Check to see if this a verification
                    // variable. If yes, we don't keep this assume.
                    // Otherwise, we need to store this for the
                    // step that generates the parsimonious vcs.
                    if (!hasVerificationVar) {
                        remAssumeExpList.add(currentAssumeExp.clone());
                    }
                }
            }

            newAntecedents = antencedentsSubtituted;
            newConsequents = consequentsSubtituted;
        }

        // Perform the application step and return the new sequent
        return applicationStep(new Sequent(seq.getLocation(),
                newAntecedents, newConsequents), remAssumeExpList);
    }
}