/*
 * RegisterSequent.java
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
import edu.clemson.rsrg.nProver.registry.CongruenceClassRegistry;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

public class RegisterSequent extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * This boolean variable indicates weather an expression is from antecedent.
     * </p>
     */
    private boolean antecedentExp;

    /**
     * <p>
     * This queue contains the arguments for the most immediate operator to be registered.
     * </p>
     */
    private Queue<Integer> arguments;

    /**
     * <p>
     * This boolean variable indicate if the expression is the ultimate root.
     * </p>
     */
    private boolean ultimate;

    /**
     * <p>
     * This registry contains the target sequent VC to be proved.
     * </p>
     */
    private CongruenceClassRegistry<Integer, String, String, String> registry;

    /**
     * <p>
     * This map contains the mapping between expressions and its associated integer number.
     * </p>
     */
    private Map<String, Integer> myExpLabels;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that registers all relevant {@link Exp} to the registry.
     * </p>
     */
    public RegisterSequent() {
        myExpLabels = new LinkedHashMap<>();
        registry = new CongruenceClassRegistry<>(1000, 1000, 1000, 1000);
        arguments = new ArrayDeque<>();
        antecedentExp = true;
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
        // append arguments usable in registering the infix operator
        while (arguments.size() > 0) {
            registry.appendToClusterArgList(arguments.remove());
        }
        // check if registered, no duplicates allowed
        if (registry.checkIfRegistered(myExpLabels.get(exp.getOperatorAsString()))) {
            arguments.add(registry.getAccessorFor(myExpLabels.get(exp.getOperatorAsString())));
        }
        // register if new, and make it an argument for the next higher level operator
        registry.registerCluster(myExpLabels.get(exp.getOperatorAsString()));
        arguments.add(myExpLabels.get(exp.getOperatorAsString()));

        if (ultimate && antecedentExp) {

        } else if (ultimate && !antecedentExp) {

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
        // check if registered, no duplicates allowed
        if (registry.checkIfRegistered(myExpLabels.get(exp.toString()))) {
            arguments.add(registry.getAccessorFor(myExpLabels.get(exp.toString())));
        }
        // register if new, and make it an argument for the next higher level operator
        registry.registerCluster(myExpLabels.get(exp.toString()));
        arguments.add(myExpLabels.get(exp.toString()));
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
            // check if registered, no duplicates allowed
            if (registry.checkIfRegistered(myExpLabels.get(exp.toString()))) {
                arguments.add(registry.getAccessorFor(myExpLabels.get(exp.toString())));
            }
            // register if new, and make it an argument for the next higher level operator
            registry.registerCluster(myExpLabels.get(exp.toString()));
            arguments.add(myExpLabels.get(exp.toString()));
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
     * @param myExpLabels
     *            expression labels mapping
     */
    public void setMyExpLabels(Map<String, Integer> myExpLabels) {
        this.myExpLabels = myExpLabels;
    }

    /**
     * <p>
     * This method sets the
     * </p>
     *
     * @param antecedentExp
     */
    public void setAntecedentExp(boolean antecedentExp) {
        this.antecedentExp = antecedentExp;
    }

    /**
     * <p>
     * This method sets the
     * </p>
     *
     * @param ultimate
     */
    public void setUltimate(boolean ultimate) {
        this.ultimate = ultimate;
    }

}
