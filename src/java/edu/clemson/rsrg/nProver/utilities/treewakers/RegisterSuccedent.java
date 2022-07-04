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

    /**
     * <p>
     * This queue contains the arguments for the most immediate operator to be registered.
     * </p>
     */
    protected final Queue<Integer> myArguments;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that labels all relevant {@link Exp} in the antecedents with a number and registers them.
     * </p>
     *
     * @param registry
     *            The registry that will contain the target sequent VC to be proved.
     * @param expLabels
     *            A mapping between expressions and its associated integer number.
     * @param nextLabel
     *            The number to be assigned initially as a label.
     */
    public RegisterSuccedent(CongruenceClassRegistry<Integer, String, String, String> registry, Map<String, Integer> expLabels,
            int nextLabel) {
        super(registry, expLabels, nextLabel);
        myArguments = new ArrayDeque<>();
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
            myRegistry.appendToClusterArgList(myArguments.remove());
        }

        if (super.getAncestorSize() == 1) {
            BitSet attb = new BitSet();
            attb.set(1); // set the class succedent
            attb.set(2); // set the class ultimate

            if ( operatorNumber == 2) { // if it is succedent equal
                myRegistry.addOperatorToSuccedentReflexiveOperatorSet(operatorNumber);
                accessor = myRegistry.registerCluster(operatorNumber);
                myRegistry.updateClassAttributes(accessor, attb);
            } else if(operatorNumber == 1){ //if it is succedent <=
                myRegistry.addOperatorToSuccedentReflexiveOperatorSet(operatorNumber);
                if(myRegistry.checkIfRegistered(operatorNumber)){
                    myRegistry.updateClassAttributes(accessor, attb);
                }else{
                    accessor = myRegistry.registerCluster(operatorNumber);
                    myRegistry.updateClassAttributes(accessor, attb);
                }
            } else {

                if (myRegistry.checkIfRegistered(operatorNumber)) {
                    myRegistry.updateClassAttributes(myRegistry.getAccessorFor(operatorNumber), attb);
                } else {
                    // register if new, and make it an argument for the next higher level operator
                    accessor = myRegistry.registerCluster(operatorNumber);
                    myRegistry.updateClassAttributes(accessor, attb);
                }

            }
        } else {
            // check if registered, no duplicates allowed
            if (myRegistry.checkIfRegistered(operatorNumber)) {
                myArguments.add(super.getRegistry().getAccessorFor(operatorNumber));
            } else {
                // register if new, and make it an argument for the next higher level operator
                accessor = myRegistry.registerCluster(operatorNumber);
                // only non-ultimate classes can be used as arguments in clusters
                myArguments.add(accessor);
            }
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
        super.postLiteralExp(exp);
        int variableNumber = myExpLabels.get(exp.toString());
        int accessor = 0;

        // Logic for handling variable expressions in the antecedent

        if (super.getRegistry().checkIfRegistered(variableNumber)) {
            myArguments.add(myRegistry.getAccessorFor(variableNumber));
        } else {
            accessor = myRegistry.registerCluster(variableNumber);
            myArguments.add(accessor);
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
        myRegistry.appendToClusterArgList(myArguments.remove());

        // check if registered, no duplicates allowed
        if (myRegistry.checkIfRegistered(operatorNumber)) {
            myArguments.add(myRegistry.getAccessorFor(operatorNumber));
        } else {
            // register if new, and make it an argument for the next higher level operator
            accessor = myRegistry.registerCluster(operatorNumber);

            // if exp is ultimate i.e., at root
            if (super.getAncestorSize() == 1) {
                BitSet attb = new BitSet();
                attb.set(1); // succedent
                attb.set(2); // ultimate
                myRegistry.updateClassAttributes(accessor, attb);
            } else {
                // only non-ultimate classes can be used as arguments in clusters
                myArguments.add(accessor);
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

        if (myRegistry.checkIfRegistered(variableNumber)) {
            myArguments.add(myRegistry.getAccessorFor(variableNumber));
        } else {
            accessor = myRegistry.registerCluster(variableNumber);
            myArguments.add(accessor);
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
        super.walkVCVarExp(exp);

        int variableNumber = myExpLabels.get(exp.toString());
        int accessor = 0;
        if (exp.getExp() instanceof VarExp) {
            if (super.getRegistry().checkIfRegistered(variableNumber)) {
                myArguments.add(myRegistry.getAccessorFor(variableNumber));
            } else {
                accessor = myRegistry.registerCluster(variableNumber);
                myArguments.add(accessor);
            }
        }

        return true;
    }

}
