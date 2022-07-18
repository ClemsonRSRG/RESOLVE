/*
 * RegisterAntecedent.java
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
import edu.clemson.rsrg.treewalk.TreeWalkerStackVisitor;
import java.util.BitSet;
import java.util.Map;

/**
 * <p>
 * This class registers each antecedent expression. This visitor logic is implemented as a
 * {@link TreeWalkerStackVisitor}.
 * </p>
 *
 * @author Yu-Shan Sun
 * @author Nicodemus Msafiri J. M.
 *
 * @version 1.0
 */
public class RegisterAntecedent extends AbstractRegisterSequent {

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
    public RegisterAntecedent(CongruenceClassRegistry<Integer, String, String, String> registry,
            Map<String, Integer> expLabels, int nextLabel) {
        super(registry, expLabels, nextLabel);
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
        super.postInfixExp(exp);
        int operatorNumber = myExpLabels.get(exp.getOperatorAsString());
        int lhsArgument = myArgumentsCache.remove(exp.getLeft());
        int rhsArgument = myArgumentsCache.remove(exp.getRight());

        // Logic for handling infix expressions in the antecedent
        if (operatorNumber == OP_EQUALS) { // if it is antecedent equal
            if(!myRegistry.areCongruent(lhsArgument, rhsArgument)) {
                myRegistry.makeCongruent(lhsArgument, rhsArgument);
            }
        } else {
            // append arguments usable in registering the infix operator
            myRegistry.appendToClusterArgList(lhsArgument);
            myRegistry.appendToClusterArgList(rhsArgument);

            registerFunction(exp, operatorNumber);
        }
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link FunctionExp}.
     * </p>
     *
     * @param exp
     *            A function expression.
     */
    @Override
    public final void postFunctionExp(FunctionExp exp) {
        super.postFunctionExp(exp);

        // Logic for handling function expressions in the antecedent
        for (Exp argument : exp.getArguments()) {
            myRegistry.appendToClusterArgList(myArgumentsCache.remove(argument));
        }
        registerFunction(exp, myExpLabels.get(exp.getOperatorAsString()));
    }

    /**
     * <p>
     * Code that gets executed after visiting an {@link OutfixExp}.
     * </p>
     *
     * @param exp
     *            An outfix expression.
     */
    @Override
    public final void postOutfixExp(OutfixExp exp) {
        super.postOutfixExp(exp);

        // Logic for handling outfix expressions in the antecedent
        // has only one argument, should run once
        myRegistry.appendToClusterArgList(myArgumentsCache.remove(exp.getArgument()));
        registerFunction(exp, myExpLabels.get(exp.getOperatorAsString()));
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link PrefixExp}.
     * </p>
     *
     * @param exp
     *            A prefix expression.
     */
    @Override
    public final void postPrefixExp(PrefixExp exp) {
        super.postPrefixExp(exp);

        // Logic for handling prefix expressions in the antecedent
        // has only one argument, should run once
        myRegistry.appendToClusterArgList(myArgumentsCache.remove(exp.getArgument()));
        registerFunction(exp, myExpLabels.get(exp.getOperatorAsString()));
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link SetCollectionExp}.
     * </p>
     *
     * @param exp
     *            A set collection expression.
     */
    @Override
    public final void postSetCollectionExp(SetCollectionExp exp) {
        super.postSetCollectionExp(exp);

        // Logic for handling set collection expressions in the antecedent
        for (Exp argument : exp.getVars()) {
            myRegistry.appendToClusterArgList(myArgumentsCache.remove(argument));
        }
        registerFunction(exp, myExpLabels.get("{_}"));
    }

    /**
     * <p>
     * Code that gets executed after visiting a {@link TupleExp}.
     * </p>
     *
     * @param exp
     *            A tuple expression.
     */
    @Override
    public final void postTupleExp(TupleExp exp) {
        super.postTupleExp(exp);

        // Logic for handling tuple expressions in the antecedent
        for (Exp field : exp.getFields()) {
            myRegistry.appendToClusterArgList(myArgumentsCache.remove(field));
        }
        registerFunction(exp, myExpLabels.get("(_)"));
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
        // YS: If we detect a top-level antecedent that is "false", then we need to specially handle it.
        // Otherwise, we simply just add it to the registry as normal.
        // From Bill Ogden, "For the implicit and operator in an antecedent, True is an identity element
        // (i. e., ( F1 and F2 and ... and Fn and True ) = ( F1 and F2 and ... and Fn ) ) and
        // False is a zero element (i. e., ( F1 and F2 and ... and Fn and False ) = ( False ) ).
        // Dually, for the implicit or operator in a succedent, False is an identity element
        // (i. e., ( F1 or F2 or ... or Fn or False ) = ( F1 or F2 or ... or Fn ) ) and
        // True is a zero element (i. e., ( F1 or F2 or ... or Fn or True ) = ( True ) ).
        // In the zero element cases, the Boolean constant can be eliminated by expressing
        // A ==> { True } by A ==> { } and { False } ==> S by { } ==> S."
        if (!(super.getAncestorSize() == 1 && exp.toString().equals("false"))) {
            super.postVarExp(exp);
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method that registers an {@link InfixExp}, {@link FunctionExp}, {@link OutfixExp} or {@link PrefixExp}
     * to the congruence class registry.
     * </p>
     *
     * @param exp
     *            Expression that we are currently evaluating.
     * @param operatorNumber
     *            The labeling number assigned to the operator.
     */
    private void registerFunction(Exp exp, int operatorNumber) {
        // check if registered, no duplicates allowed
        if (myRegistry.checkIfRegistered(operatorNumber)) {
            myArgumentsCache.put(exp, myRegistry.getAccessorFor(operatorNumber));
        } else {
            // register if new, and make it an argument for the next higher level operator
            int accessor = myRegistry.registerCluster(operatorNumber);

            // if exp is ultimate i.e., at root
            if (super.getAncestorSize() == 1) {
                BitSet attb = new BitSet();
                attb.set(0);// set the class antecedent
                attb.set(2);// set the class ultimate
                myRegistry.updateClassAttributes(accessor, attb);
            } else {
                // only non-ultimate classes can be used as arguments in clusters
                myArgumentsCache.put(exp, accessor);
            }
        }
    }

}