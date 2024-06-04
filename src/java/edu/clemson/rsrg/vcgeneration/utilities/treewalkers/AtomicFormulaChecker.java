/*
 * AtomicFormulaChecker.java
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

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;

/**
 * <p>
 * This class determines if an {@link Exp} is an atomic formula or not. This visitor logic is implemented as a
 * {@link TreeWalkerVisitor}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class AtomicFormulaChecker extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * This flag will indicate if the {@link Exp} we are walking is atomic or not.
     * </p>
     */
    private boolean myIsAtomicFormulaFlag;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that checks to see if an {@link Exp} is an atomic formula.
     * </p>
     */
    public AtomicFormulaChecker() {
        myIsAtomicFormulaFlag = true;
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Math Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link BetweenExp}.
     * </p>
     *
     * @param exp
     *            A conjunct expression.
     */
    @Override
    public final void preBetweenExp(BetweenExp exp) {
        // Ex: "min_int <= i <= max_int". This is represented in the compiler
        // using two InfixExps: "min_int <= i and i <= max_int". The "and"
        // operator makes it not atomic.
        myIsAtomicFormulaFlag = false;
    }

    /**
     * <p>
     * This method redefines how an {@link EqualsExp} should be walked.
     * </p>
     *
     * @param exp
     *            An equality/inequality expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkEqualsExp(EqualsExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preEqualsExp(exp);

        // No need to walk any of our children. This is definitely atomic.

        postEqualsExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * This method redefines how a {@link FunctionExp} should be walked.
     * </p>
     *
     * @param exp
     *            A function expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkFunctionExp(FunctionExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preFunctionExp(exp);

        // Don't walk any of the function parameter expressions.

        postFunctionExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * Code that gets executed before visiting an {@link InfixExp}.
     * </p>
     *
     * @param exp
     *            An infix expression.
     */
    @Override
    public final void preInfixExp(InfixExp exp) {
        String operatorAsString = exp.getOperatorAsString();

        // YS: For the moment we are ignoring the possibility of having operators
        // like "xor" and "if and only if" operators. At some point, we should add
        // these here if it shows up in our specification languages.
        if (operatorAsString.equals("and") || operatorAsString.equals("or") || operatorAsString.equals("implies")) {
            myIsAtomicFormulaFlag = false;
        }
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

        // Don't walk any of the lambda parameter expressions.

        postLambdaExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * Code that gets executed before visiting a {@link PrefixExp}.
     * </p>
     *
     * @param exp
     *            A prefix expression.
     */
    @Override
    public final void prePrefixExp(PrefixExp exp) {
        if (exp.getOperatorAsString().equals("not")) {
            myIsAtomicFormulaFlag = false;
        }
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
        // This is an error! We should have converted all ProgramExp to their
        // MathExp counterparts.
        throw new SourceErrorException("[VCGenerator] Found: " + exp + " in a Sequent!", exp.getLocation());
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns a flag that indicates whether or not this is an atomic formula or not.
     * </p>
     *
     * @return {@code true} if the {@link Exp} that was walked contains only atomic formulas, {@code false} otherwise.
     */
    public final boolean getIsAtomicFormula() {
        return myIsAtomicFormulaFlag;
    }

}
