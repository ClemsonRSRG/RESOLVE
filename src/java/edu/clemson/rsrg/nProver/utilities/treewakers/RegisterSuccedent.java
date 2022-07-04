/*
 * RegisterSuccedent.java
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

import java.util.*;

public class RegisterSuccedent extends AbstractRegisterSequent {

    // ===========================================================
    // Member Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that labels all relevant {@link Exp} in the antecedents with a number and registers them.
     * </p>
     *
     * @param arguments
     * @param registry
     *            The registry that will contain the target sequent VC to be proved.
     * @param expLabels
     *            A mapping between expressions and its associated integer number.
     * @param nextLabel
     *            The number to be assigned initially as a label.
     */
    public RegisterSuccedent(Queue<Integer> arguments,
            CongruenceClassRegistry<Integer, String, String, String> registry, Map<String, Integer> expLabels,
            int nextLabel) {
        super(arguments, registry, expLabels, nextLabel);
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Math Expression-Related
    // -----------------------------------------------------------

    // ===========================================================
    // Public Methods
    // ===========================================================

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
        super.postInfixExp(exp);
        int operatorNumber = myExpLabels.get(exp.getOperatorAsString());
        int accessor = 0;

        // Logic for handling infix expressions in the antecedent

        // append arguments usable in registering the infix operator
        for (int i = 0; i < 2; i++) { // should only run twice
            super.getRegistry().appendToClusterArgList(super.getMyArguments().remove());
        }

        if (super.getAncestorSize() == 1) {
            BitSet attb = new BitSet();
            attb.set(1); // set the class succedent
            attb.set(2); // set the class ultimate

            if (operatorNumber == 1 || operatorNumber == 2) { // if it is antecedent equal
                super.getRegistry().addOperatorToSuccedentReflexiveOperatorSet(operatorNumber);
                accessor = super.getRegistry().registerCluster(operatorNumber);
                super.getRegistry().updateClassAttributes(accessor, attb);
            } else {

                if (super.getRegistry().checkIfRegistered(operatorNumber)) {
                    // the sequent is proved
                } else {
                    // register if new, and make it an argument for the next higher level operator
                    accessor = super.getRegistry().registerCluster(operatorNumber);
                    super.getRegistry().updateClassAttributes(accessor, attb);
                }

            }
        } else {
            // check if registered, no duplicates allowed
            if (super.getRegistry().checkIfRegistered(operatorNumber)) {
                super.getMyArguments().add(super.getRegistry().getAccessorFor(operatorNumber));
            } else {
                // register if new, and make it an argument for the next higher level operator
                accessor = super.getRegistry().registerCluster(operatorNumber);
                // only non-ultimate classes can be used as arguments in clusters
                super.getMyArguments().add(accessor);
            }
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
        super.postVarExp(exp);
        int variableNumber = myExpLabels.get(exp.toString());
        int accessor = 0;

        // Logic for handling variable expressions in the antecedent

        if (super.getRegistry().checkIfRegistered(variableNumber)) {
            super.getMyArguments().add(super.getRegistry().getAccessorFor(variableNumber));
        } else {
            accessor = super.getRegistry().registerCluster(variableNumber);
            super.getMyArguments().add(accessor);
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
        super.postOutfixExp(exp);
        int operatorNumber = myExpLabels.get(exp.getOperatorAsString());
        int accessor = 0;

        // Logic for handling outfix expressions in the antecedent

        // has only one argument, should run once
        super.getRegistry().appendToClusterArgList(super.getMyArguments().remove());

        // check if registered, no duplicates allowed
        if (super.getRegistry().checkIfRegistered(operatorNumber)) {
            super.getMyArguments().add(super.getRegistry().getAccessorFor(operatorNumber));
        } else {
            // register if new, and make it an argument for the next higher level operator
            accessor = super.getRegistry().registerCluster(operatorNumber);

            // if exp is ultimate i.e., at root
            if (super.getAncestorSize() == 1) {
                BitSet attb = new BitSet();
                attb.set(1); // succedent
                attb.set(2); // ultimate
                super.getRegistry().updateClassAttributes(accessor, attb);
            } else {
                // only non-ultimate classes can be used as arguments in clusters
                super.getMyArguments().add(accessor);
            }
        }

    }

}
