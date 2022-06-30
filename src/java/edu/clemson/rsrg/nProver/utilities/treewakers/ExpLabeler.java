/*
 * ExpLabeler.java
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
package edu.clemson.rsrg.nProver.utilities.treewakers;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.InfixExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.LiteralExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VCVarExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.rsrg.nProver.GeneralPurposeProver;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * This class labels all the {@link Exp} with a number to be used by the {@link GeneralPurposeProver}. This visitor
 * logic is implemented as a {@link TreeWalkerVisitor}.
 * </p>
 *
 * @author Yu-Shan Sun
 * @author Nicodemus Msafiri J. M.
 *
 * @version 1.0
 */
public class ExpLabeler extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * This map contains the mapping between expressions and its associated integer number.
     * </p>
     */
    private final Map<String, Integer> myExpLabels;

    /**
     * <p>
     * A counter for the next expression
     * </p>
     */
    private int myNextLabel;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that labels all relevant {@link Exp} with a number.
     * </p>
     */
    public ExpLabeler() {
        myExpLabels = new LinkedHashMap<>();
        myNextLabel = 2;
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Math Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed after visiting a {@link InfixExp}.
     * </p>
     *
     * @param exp
     *            An infix expression.
     */
    @Override
    public final void postInfixExp(InfixExp exp) {
        // If this is not a variable expression we have seen, then add it to our map
        if (!myExpLabels.containsKey(exp.getOperatorAsString())) {
            myExpLabels.put(exp.getOperatorAsString(), myNextLabel);
            myNextLabel++;
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link LiteralExp}.
     * </p>
     *
     * @param exp
     *            A literal expression.
     */
    @Override
    public final void postLiteralExp(LiteralExp exp) {
        // If this is not a variable expression we have seen, then add it to our map
        if (!myExpLabels.containsKey(exp.toString())) {
            myExpLabels.put(exp.toString(), myNextLabel);
            myNextLabel++;
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link VarExp}.
     * </p>
     *
     * @param exp
     *            A variable expression.
     */
    @Override
    public final void postVarExp(VarExp exp) {
        // If this is not a variable expression we have seen, then add it to our map
        if (!myExpLabels.containsKey(exp.toString())) {
            myExpLabels.put(exp.toString(), myNextLabel);
            myNextLabel++;
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

        // YS: Need special handle VarExp
        if (exp.getExp() instanceof VarExp) {
            if (!myExpLabels.containsKey(exp.toString())) {
                // YS: A VCVarExp is something like: a' or a'''.
                // We don't want all the variations so rather than walking
                // the inner expression, we simply store the expression
                myExpLabels.put(exp.toString(), myNextLabel);
                myNextLabel++;
            }
        }

        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the mapping from expression to its associated number.
     * </p>
     *
     * @return A mapping from {@link String} to {@link Integer}.
     */
    public final Map<String, Integer> getExpLabels() {
        return myExpLabels;
    }

}