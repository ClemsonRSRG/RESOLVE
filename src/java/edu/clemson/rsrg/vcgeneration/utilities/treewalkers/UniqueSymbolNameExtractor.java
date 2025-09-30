/*
 * UniqueSymbolNameExtractor.java
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
package edu.clemson.rsrg.vcgeneration.utilities.treewalkers;

import edu.clemson.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.rsrg.vcgeneration.proofrules.statements.AssumeStmtRule;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>
 * This class extracts unique symbols from an {@link Exp}. This visitor logic is implemented as a
 * {@link TreeWalkerVisitor}.
 * </p>
 * <p>
 * Note that this class was designed to be an helper class for the {@link AssumeStmtRule}. Any modifications to this
 * class might break the rule!
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class UniqueSymbolNameExtractor extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Set of expression names extracted by this visitor.
     * </p>
     */
    private final Set<String> myExpNames;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that that extract expression names for anything that descends from {@link MathExp}.
     * </p>
     */
    public UniqueSymbolNameExtractor() {
        myExpNames = new LinkedHashSet<>();
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Math Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * This method redefines how a {@link DotExp} should be walked.
     * </p>
     *
     * @param exp
     *            A dotted expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkDotExp(DotExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preDotExp(exp);

        // So before we do anything, we store the current set
        // of symbols we have encountered so far.
        Set<String> tempSet = new LinkedHashSet<>(myExpNames);

        // YS: We really don't want the individual names from
        // each of the segments. What we really want are the
        // different dotted segment names.
        // Ex: "p.q.r" -> "p", "p.q", "p.q.r"
        StringBuilder sb = new StringBuilder();
        for (Exp segmentExp : exp.getSegments()) {
            // Clear myExpNames because we don't want intermediate
            // results.
            myExpNames.clear();

            // If this is a FunctionExp, we first visit any caratExp
            // and its arguments. These simply have to be added to tempSet
            // because they are not part of the segment names we are constructing
            // Lastly, we visit the name to extract the next segment name.
            if (segmentExp instanceof FunctionExp) {
                FunctionExp segmentExpAsFunctionExp = (FunctionExp) segmentExp;

                // Visit any caratExp.
                if (segmentExpAsFunctionExp.getCaratExp() != null) {
                    TreeWalker.visit(this, segmentExpAsFunctionExp.getCaratExp());
                    tempSet.addAll(myExpNames);
                    myExpNames.clear();
                }

                // Visit the arguments
                for (Exp argExp : segmentExpAsFunctionExp.getArguments()) {
                    TreeWalker.visit(this, argExp);
                    tempSet.addAll(myExpNames);
                    myExpNames.clear();
                }

                TreeWalker.visit(this, segmentExpAsFunctionExp.getName());
            } else {
                TreeWalker.visit(this, segmentExp);
            }

            // Make sure our segment only has 1 name
            if (myExpNames.size() != 1) {
                throw new SourceErrorException("[VCGenerator] Cannot extract name from this segment: " + segmentExp,
                        segmentExp.getLocation());
            } else {
                // Store this new string into tempSet
                sb.append(myExpNames.iterator().next());
                tempSet.add(sb.toString());

                // Add a "." for the next segment
                sb.append(".");
            }
        }

        // Clear any intermediate results and add all
        // the strings from tempSet back to myExpNames.
        myExpNames.clear();
        myExpNames.addAll(tempSet);

        postDotExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * This method redefines how a {@link LambdaExp} should be walked.
     * </p>
     *
     * @param exp
     *            A lambda expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkLambdaExp(LambdaExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preLambdaExp(exp);

        // So before we do anything, we store the current set
        // of symbols we have encountered so far.
        Set<String> tempSet = new LinkedHashSet<>(myExpNames);

        // YS: We really don't want the names of parameters
        // being bound in this expression. So we simply remove
        // those after we visit.
        myExpNames.clear();
        TreeWalker.visit(this, exp.getBody());

        // Remove the bound parameter names.
        for (MathVarDec varDec : exp.getParameters()) {
            myExpNames.remove(varDec.getName().getName());
        }

        // YS: We are using a LinkedHashSet, so insertion order
        // does matter for us, so we will need to combine the sets
        // in the right order
        tempSet.addAll(myExpNames);
        myExpNames.clear();
        myExpNames.addAll(tempSet);

        postLambdaExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * This method redefines how an {@link OldExp} should be walked.
     * </p>
     *
     * @param exp
     *            An old expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkOldExp(OldExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preOldExp(exp);

        // So before we do anything, we store the current set
        // of symbols we have encountered so far.
        Set<String> tempSet = new LinkedHashSet<>(myExpNames);

        // YS: We really don't want the individual names from
        // the expression. What we really want is "#<Name of Expression>"
        // So we clear whatever is in myExpNames and apply the visit to
        // the inner expression.
        myExpNames.clear();

        // If this is a FunctionExp, we first visit any caratExp
        // and its arguments. These simply have to be added to tempSet
        // because they are not part of the segment names we are constructing
        // Lastly, we visit the name to obtain the string we are going to use
        // to build our name.
        if (exp.getExp() instanceof FunctionExp) {
            FunctionExp innerFunctionExp = (FunctionExp) exp.getExp();

            // Visit any caratExp.
            if (innerFunctionExp.getCaratExp() != null) {
                TreeWalker.visit(this, innerFunctionExp.getCaratExp());
                tempSet.addAll(myExpNames);
                myExpNames.clear();
            }

            // Visit the arguments
            for (Exp argExp : innerFunctionExp.getArguments()) {
                TreeWalker.visit(this, argExp);
                tempSet.addAll(myExpNames);
                myExpNames.clear();
            }

            TreeWalker.visit(this, innerFunctionExp.getName());
        } else {
            TreeWalker.visit(this, exp.getExp());
        }

        // Construct a new string name from the names in myExpNames
        for (String name : myExpNames) {
            tempSet.add("#" + name);
        }

        // Clear any intermediate results and add all
        // the strings from tempSet back to myExpNames.
        myExpNames.clear();
        myExpNames.addAll(tempSet);

        postOldExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * This method redefines how a {@link QuantExp} should be walked.
     * </p>
     *
     * @param exp
     *            A quantified expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkQuantExp(QuantExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preQuantExp(exp);

        // So before we do anything, we store the current set
        // of symbols we have encountered so far.
        Set<String> tempSet = new LinkedHashSet<>(myExpNames);

        // YS: We really don't want the names of variables
        // being bound in this expression. So we simply remove
        // those after we visit.
        myExpNames.clear();

        if (exp.getWhere() != null) {
            TreeWalker.visit(this, exp.getWhere());
        }

        TreeWalker.visit(this, exp.getBody());

        // Remove the bound variable names.
        for (MathVarDec varDec : exp.getVars()) {
            myExpNames.remove(varDec.getName().getName());
        }

        // YS: We are using a LinkedHashSet, so insertion order
        // does matter for us, so we will need to combine the sets
        // in the right order
        tempSet.addAll(myExpNames);
        myExpNames.clear();
        myExpNames.addAll(tempSet);

        postQuantExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * This method redefines how a {@link RecpExp} should be walked.
     * </p>
     *
     * @param exp
     *            A type receptacles expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkRecpExp(RecpExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);

        // YS: Don't need to walk the inner expression.
        myExpNames.add(exp.asString(0, 0));

        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * This method redefines how a {@link SetCollectionExp} should be walked.
     * </p>
     *
     * @param exp
     *            A set collection expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkSetCollectionExp(SetCollectionExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preSetCollectionExp(exp);

        // YS: Our tree walker visitor doesn't visit items inside
        // sets right now, so we will need to redefine the walk method
        // to walk it ourselves.
        for (Exp innerExp : exp.getVars()) {
            TreeWalker.visit(this, innerExp);
        }

        postSetCollectionExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * This method redefines how a {@link SetExp} should be walked.
     * </p>
     *
     * @param exp
     *            A set expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkSetExp(SetExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preSetExp(exp);

        // So before we do anything, we store the current set
        // of symbols we have encountered so far.
        Set<String> tempSet = new LinkedHashSet<>(myExpNames);

        // YS: We really don't want the names of variables
        // being bound in this expression. So we simply remove
        // those after we visit.
        myExpNames.clear();

        if (exp.getWhere() != null) {
            TreeWalker.visit(this, exp.getWhere());
        }

        TreeWalker.visit(this, exp.getBody());

        // Remove the bound variable name.
        myExpNames.remove(exp.getVar().getName().getName());

        // YS: We are using a LinkedHashSet, so insertion order
        // does matter for us, so we will need to combine the sets
        // in the right order
        tempSet.addAll(myExpNames);
        myExpNames.clear();
        myExpNames.addAll(tempSet);

        postSetExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * This method redefines how a {@link TypeReceptaclesExp} should be walked.
     * </p>
     *
     * @param exp
     *            A type receptacles expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkTypeReceptaclesExp(TypeReceptaclesExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);

        // YS: Don't need to walk the inner expression.
        myExpNames.add(exp.asString(0, 0));

        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link VarExp}.
     * </p>
     *
     * @param exp
     *            A variable expression.
     */
    @Override
    public final void preVarExp(VarExp exp) {
        // Don't do this if it is a Precis definition name
        if (!exp.isIsPrecisDefinitionName()) {
            // Build the name
            StringBuilder sb = new StringBuilder();
            if (exp.getQualifier() != null) {
                sb.append(exp.getQualifier().getName());
                sb.append("::");
            }
            sb.append(exp.getName().getName());

            // Add the possibility qualified name.
            myExpNames.add(sb.toString());
        }
    }

    /**
     * <p>
     * This method redefines how a {@link VCVarExp} should be walked.
     * </p>
     *
     * @param exp
     *            A verification variable expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkVCVarExp(VCVarExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);

        // YS: Need special handle FunctionExp
        if (exp.getExp() instanceof FunctionExp) {
            FunctionExp innerFunctionExp = (FunctionExp) exp.getExp();

            // YS: Add the function name with the correct number of primes
            VarExp functionNameExp = innerFunctionExp.getName();

            // Build the name
            StringBuilder sb = new StringBuilder();
            if (innerFunctionExp.getQualifier() != null) {
                sb.append(innerFunctionExp.getQualifier().getName());
                sb.append("::");
            }
            sb.append(functionNameExp.getName().getName());

            // Append the number of primes
            for (int i = 0; i < exp.getStateNum(); i++) {
                sb.append("'");
            }

            // Add the possibility qualified name.
            myExpNames.add(sb.toString());
        }

        // YS: A VCVarExp is something like: a' or a'''.
        // We don't want all the variations so rather than walking
        // the inner expression, we simply use the asString
        // method to extract the name.
        myExpNames.add(exp.asString(0, 0));

        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    // -----------------------------------------------------------
    // Program Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link ProgramExp}.
     * </p>
     *
     * @param exp
     *            A programming expression.
     */
    @Override
    public final void preProgramExp(ProgramExp exp) {
        // This is an error! We should have converted all ProgramExp
        // to their math counterparts.
        throw new SourceErrorException("[VCGenerator] Unexpected ProgramExp: " + exp, exp.getLocation());
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the set of symbols extracted by this tree walker visitor.
     * </p>
     *
     * @return Set of expression names.
     */
    public final Set<String> getSymbols() {
        return myExpNames;
    }

}
