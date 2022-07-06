/*
 * AbstractRegisterSequent.java
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
import edu.clemson.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.rsrg.nProver.GeneralPurposeProver;
import edu.clemson.rsrg.nProver.registry.CongruenceClassRegistry;
import edu.clemson.rsrg.treewalk.TreeWalkerStackVisitor;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * This abstract class labels all the {@link Exp} with a number to be used by the {@link GeneralPurposeProver}. The
 * concrete logic of registering each expression is left to its child class. This visitor logic is implemented as a
 * {@link TreeWalkerStackVisitor}.
 * </p>
 *
 * @author Yu-Shan Sun
 * @author Nicodemus Msafiri J. M.
 *
 * @version 1.0
 */
public abstract class AbstractRegisterSequent extends TreeWalkerStackVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * This map contains the mapping between the argument expressions for the most immediate operator to be registered.
     * </p>
     */
    protected final Map<Exp, Integer> myArgumentsCache;

    /**
     * <p>
     * This map contains the mapping between expressions and its associated integer number.
     * </p>
     */
    protected final Map<String, Integer> myExpLabels;

    /**
     * <p>
     * A counter for the next expression
     * </p>
     */
    protected int myNextLabel;

    /**
     * <p>
     * This registry contains the target sequent VC to be proved.
     * </p>
     */
    protected final CongruenceClassRegistry<Integer, String, String, String> myRegistry;

    // ===========================================================
    // Global Operator Labels
    // ===========================================================

    /**
     * <p>
     * A constant representing the {@code =} operator.
     * </p>
     */
    public static final int OP_EQUALS = 2;

    /**
     * <p>
     * A constant representing the {@code <=} operator.
     * </p>
     */
    public static final int OP_LESS_THAN_OR_EQUALS = 1;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This helper constructor stores all relevant classes for the child classes to use.
     * </p>
     *
     * @param registry
     *            The registry that will contain the target sequent VC to be proved.
     * @param expLabels
     *            A mapping between expressions and its associated integer number.
     * @param nextLabel
     *            The number to be assigned initially as a label.
     */
    protected AbstractRegisterSequent(CongruenceClassRegistry<Integer, String, String, String> registry,
            Map<String, Integer> expLabels, int nextLabel) {
        myArgumentsCache = new LinkedHashMap<>();
        myRegistry = registry;
        myExpLabels = expLabels;
        myNextLabel = nextLabel;
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
    public void postInfixExp(InfixExp exp) {
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
    public void postLiteralExp(LiteralExp exp) {
        // If this is not a variable expression we have seen, then add it to our map
        if (!myExpLabels.containsKey(exp.toString())) {
            myExpLabels.put(exp.toString(), myNextLabel);
            myNextLabel++;
        }

        // Logic for handling literal expressions
        int variableNumber = myExpLabels.get(exp.toString());
        if (myRegistry.checkIfRegistered(variableNumber)) {
            myArgumentsCache.put(exp, myRegistry.getAccessorFor(variableNumber));
        } else {
            myArgumentsCache.put(exp, myRegistry.registerCluster(variableNumber));
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link OutfixExp}.
     * </p>
     *
     * @param exp
     *            An outfix expression.
     */
    @Override
    public void postOutfixExp(OutfixExp exp) {
        // If this is not a variable expression we have seen, then add it to our map
        if (!myExpLabels.containsKey(exp.getOperatorAsString())) {
            myExpLabels.put(exp.getOperatorAsString(), myNextLabel);
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
    public void postVarExp(VarExp exp) {
        // If this is not a variable expression we have seen, then add it to our map
        if (!myExpLabels.containsKey(exp.toString())) {
            myExpLabels.put(exp.toString(), myNextLabel);
            myNextLabel++;
        }

        // Logic for handling variable expressions
        int variableNumber = myExpLabels.get(exp.toString());
        if (myRegistry.checkIfRegistered(variableNumber)) {
            myArgumentsCache.put(exp, myRegistry.getAccessorFor(variableNumber));
        } else {
            myArgumentsCache.put(exp, myRegistry.registerCluster(variableNumber));
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
    public boolean walkVCVarExp(VCVarExp exp) {
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

            // Logic for handling VC variable expressions
            int variableNumber = myExpLabels.get(exp.toString());
            if (myRegistry.checkIfRegistered(variableNumber)) {
                myArgumentsCache.put(exp, myRegistry.getAccessorFor(variableNumber));
            } else {
                myArgumentsCache.put(exp, myRegistry.registerCluster(variableNumber));
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

    /**
     * <p>
     * This method returns the next number to be assigned as a label.
     * </p>
     *
     * @return The next label number.
     */
    public final int getNextLabel() {
        return myNextLabel;
    }

    /**
     * <p>
     * This method returns the congruence class registry.
     * </p>
     *
     * @return The registry containing the sequent we are trying to prove.
     */
    public final CongruenceClassRegistry<Integer, String, String, String> getRegistry() {
        return myRegistry;
    }

}