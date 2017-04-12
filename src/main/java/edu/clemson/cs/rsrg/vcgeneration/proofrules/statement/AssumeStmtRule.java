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

import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.EqualsExp.Operator;
import edu.clemson.cs.rsrg.vcgeneration.absyn.mathexpr.VCVarExp;
import edu.clemson.cs.rsrg.vcgeneration.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.vcs.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.vcs.Sequent;
import java.util.*;
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
        else {
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
        return "Assume Rule";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that checks to see if the given assume expression
     * can be used to prove our confirm expression. This is done by finding the
     * intersection between the set of symbols in the assume expression and
     * the set of symbols in the confirm expression.</p>
     *
     * <p>If the assume expressions are part of a stipulate assume clause,
     * then we keep all the assume expressions no matter what.</p>
     *
     * <p>If it is not a stipulate assume clause, we loop though keep looping through
     * all the assume expressions until we stop adding more expression to our antecedent.</p>
     *
     * @param seq The current {@link Sequent} we are processing.
     * @param remAssumeExpList The list of remaining assume expressions.
     *
     * @return The modified {@link Sequent}.
     */
    private Sequent assumeApplicationStep(Sequent seq, List<Exp> remAssumeExpList) {
        Set<Exp> seqAntecedents = new LinkedHashSet<>(seq.getAntecedents());
        Set<Exp> seqConsequents = new LinkedHashSet<>(seq.getConcequents());

        // If it is stipulate clause, keep it no matter what
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
                        symbolsInSeq.addAll(getSymbols(antecedentExp));
                    }

                    // Get the set of symbols in the consequent
                    for (Exp consequentExp : seqConsequents) {
                        symbolsInSeq.addAll(getSymbols(consequentExp));
                    }

                    // Add this as a new antecedent if there are common symbols
                    // in the assume expression and in the sequent. (Parsimonious step)
                    Set<String> intersection = new LinkedHashSet<>(symbolsInSeq);
                    intersection.retainAll(getSymbols(assumeExp));

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
        else if (exp instanceof VCVarExp) {
            retVal = containsReplaceableExp(((VCVarExp) exp).getExp());
        }

        return retVal;
    }

    /**
     * <p>This method iterates through each of the assumed expressions.
     * If the expression is a replaceable equals expression, it will substitute
     * all instances of the expression in the rest of the assume expression
     * list and in the confirm expression list.</p>
     *
     * <p>When it is not a replaceable expression, we apply a step to generate
     * parsimonious VCs.</p>
     *
     * @param assumeExpList The list of conjunct assume expressions.
     *
     * @return The new list of {@link Sequent Sequents}.
     */
    private List<Sequent> formParsimoniousVC(List<Exp> assumeExpList) {
        List<Sequent> currentSequents = myCurrentAssertiveCodeBlock.getSequents();
        List<Sequent> newSequents = new ArrayList<>();

        // Loop through each sequent and apply one of the assume steps
        for (Sequent seq : currentSequents) {
            newSequents.add(substitutionStep(seq, assumeExpList));
        }

        return newSequents;
    }

    /**
     * <p>Gets all the unique symbols in an expression.</p>
     *
     * @param exp The searching {@link Exp}.
     *
     * @return The set of symbols.
     */
    private Set<String> getSymbols(Exp exp) {
        // Return value
        Set<String> symbolsSet = new HashSet<>();

        // Not CharExp, DoubleExp, IntegerExp or StringExp
        if (!(exp instanceof CharExp) && !(exp instanceof DoubleExp)
                && !(exp instanceof IntegerExp) && !(exp instanceof StringExp)) {
            // AlternativeExp
            if (exp instanceof AlternativeExp) {
                List<AltItemExp> alternativesList =
                        ((AlternativeExp) exp).getAlternatives();

                // Iterate through each of the alternatives
                for (AltItemExp altExp : alternativesList) {
                    Exp test = altExp.getTest();
                    Exp assignment = altExp.getAssignment();

                    // Don't loop if they are null
                    if (test != null) {
                        symbolsSet.addAll(getSymbols(altExp.getTest()));
                    }

                    if (assignment != null) {
                        symbolsSet.addAll(getSymbols(altExp.getAssignment()));
                    }
                }
            }
            // DotExp
            else if (exp instanceof DotExp) {
                List<Exp> segExpList = ((DotExp) exp).getSegments();
                StringBuffer currentStr = new StringBuffer();

                // Iterate through each of the segment expressions
                for (Exp e : segExpList) {
                    // For each expression, obtain the set of symbols
                    // and form a candidate expression.
                    Set<String> retSet = getSymbols(e);
                    for (String s : retSet) {
                        if (currentStr.length() != 0) {
                            currentStr.append(".");
                        }
                        currentStr.append(s);
                    }
                    symbolsSet.add(currentStr.toString());
                }
            }
            // EqualsExp
            else if (exp instanceof EqualsExp) {
                symbolsSet.addAll(getSymbols(((EqualsExp) exp).getLeft()));
                symbolsSet.addAll(getSymbols(((EqualsExp) exp).getRight()));
            }
            // FunctionExp
            else if (exp instanceof FunctionExp) {
                FunctionExp funcExp = (FunctionExp) exp;
                symbolsSet.addAll(getSymbols(funcExp.getName()));

                // Add the carat expression if it is not null
                if (funcExp.getCaratExp() != null) {
                    symbolsSet.addAll(getSymbols(funcExp.getCaratExp()));
                }

                // Add all the symbols in the argument list
                List<Exp> funcArgExpList = funcExp.getArguments();
                for (Exp e : funcArgExpList) {
                    symbolsSet.addAll(getSymbols(e));
                }
            }
            // If Exp
            else if (exp instanceof IfExp) {
                symbolsSet.addAll(getSymbols(((IfExp) exp).getTest()));
                symbolsSet.addAll(getSymbols(((IfExp) exp).getThen()));
                symbolsSet.addAll(getSymbols(((IfExp) exp).getElse()));
            }
            // InfixExp
            else if (exp instanceof InfixExp) {
                symbolsSet.addAll(getSymbols(((InfixExp) exp).getLeft()));
                symbolsSet.addAll(getSymbols(((InfixExp) exp).getRight()));
            }
            // LambdaExp
            else if (exp instanceof LambdaExp) {
                LambdaExp lambdaExp = (LambdaExp) exp;

                // Add all the parameter variables
                List<MathVarDec> paramList = lambdaExp.getParameters();
                for (MathVarDec v : paramList) {
                    symbolsSet.add(v.getName().getName());
                }

                // Add all the symbols in the body
                symbolsSet.addAll(getSymbols(lambdaExp.getBody()));
            }
            // OldExp
            else if (exp instanceof OldExp) {
                symbolsSet.add(exp.toString());
            }
            // OutfixExp
            else if (exp instanceof OutfixExp) {
                symbolsSet.addAll(getSymbols(((OutfixExp) exp).getArgument()));
            }
            // PrefixExp
            else if (exp instanceof PrefixExp) {
                symbolsSet.addAll(getSymbols(((PrefixExp) exp).getArgument()));
            }
            // SetExp
            else if (exp instanceof SetExp) {
                SetExp setExp = (SetExp) exp;

                // Add all the parts that form the set expression
                symbolsSet.add(setExp.getVar().getName().getName());
                symbolsSet.addAll(getSymbols(setExp.getWhere()));
                symbolsSet.addAll(getSymbols(setExp.getBody()));
            }
            // SetCollectionExp
            else if (exp instanceof SetCollectionExp) {
                SetCollectionExp setExp = (SetCollectionExp) exp;

                // Add all the parts that form the set expression
                for (MathExp e : setExp.getVars()) {
                    symbolsSet.addAll(getSymbols(e));
                }
            }
            // TupleExp
            else if (exp instanceof TupleExp) {
                TupleExp tupleExp = (TupleExp) exp;

                // Add all the expressions in the fields
                List<Exp> fieldList = tupleExp.getFields();
                for (Exp e : fieldList) {
                    symbolsSet.addAll(getSymbols(e));
                }
            }
            // VarExp
            else if (exp instanceof VarExp) {
                VarExp varExp = (VarExp) exp;
                StringBuffer varName = new StringBuffer();

                // Add the name of the variable (including any qualifiers)
                if (varExp.getQualifier() != null) {
                    varName.append(varExp.getQualifier().getName());
                    varName.append(".");
                }
                varName.append(varExp.getName());
                symbolsSet.add(varName.toString());
            }
            // VCVarExp
            else if (exp instanceof VCVarExp) {
                symbolsSet.addAll(getSymbols(((VCVarExp) exp).getExp()));
            }
            // Not Handled!
            else {
                Utilities.expNotHandled(exp, exp.getLocation());
            }
        }

        return symbolsSet;
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
     * given set.</p>
     *
     * @param expressions Set of {@link Exp Exps}.
     * @param replacements A map of substitutions.
     *
     * @return A modified set of {@link Exp Exps}.
     */
    private Set<Exp> substituteExps(Set<Exp> expressions, Map<Exp, Exp> replacements) {
        Set<Exp> newExps = new LinkedHashSet<>();
        for (Exp exp : expressions) {
            newExps.add(exp.substitute(replacements));
        }

        return newExps;
    }

    /**
     * <p>This is a helper method that checks to see if the any of the assume expressions
     * can be substituted. If it can, it will generate a new sequent. If it cannot, the original
     * sequent will be kept. Once this step is over, it will call the next assume application step
     * to generate the final {@link Sequent}.</p>
     *
     * @param seq The current {@link Sequent} we are processing.
     * @param assumeExpList A list of {@link Exp Exps} being assumed.
     *
     * @return The modified {@link Sequent}.
     */
    private Sequent substitutionStep(Sequent seq, List<Exp> assumeExpList) {
        // New antecedent and consequent expressions
        Set<Exp> newAntecedents = new LinkedHashSet<>(seq.getAntecedents());
        Set<Exp> newConsequents = new LinkedHashSet<>(seq.getConcequents());

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

                // Check to see if we have P_val or Cum_Dur
                // TODO: Make sure this still works!
                if (equalsExp.getLeft().containsVar("P_val", false) ||
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

            // Check to see if this is a stipulate assume clause
            // If yes, we keep a copy of the current
            // assume expression.
            if (myAssumeStmt.getIsStipulate()) {
                remAssumeExpList.add(currentAssumeExp.clone());
            }

            Set<Exp> antencedentsSubtituted = substituteExps(newAntecedents, substitutions);
            Set<Exp> consequentsSubtituted = substituteExps(newConsequents, substitutions);

            // No substitutions
            if (antencedentsSubtituted.equals(newAntecedents) &&
                    consequentsSubtituted.equals(newConsequents)) {
                // Check to see if this a verification
                // variable. If yes, we don't keep this assume.
                // Otherwise, we need to store this for the
                // step that generates the parsimonious vcs.
                if (!hasVerificationVar) {
                    remAssumeExpList.add(currentAssumeExp.clone());
                }
            }

            newAntecedents = antencedentsSubtituted;
            newConsequents = consequentsSubtituted;
        }

        // Perform the assume application step and return the new sequent
        return assumeApplicationStep(new Sequent(seq.getLocation(), newAntecedents, newConsequents), remAssumeExpList);
    }
}