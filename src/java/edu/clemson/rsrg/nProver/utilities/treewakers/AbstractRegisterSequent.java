/*
 * AbstractRegisterSequent.java
 * ---------------------------------
 * Copyright (c) 2023
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
import edu.clemson.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.treewalk.TreeWalkerStackVisitor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * A counter to enumerate the number of literals we have encountered. This is used to allow duplicates literal
     * expressions in our map.
     * </p>
     */
    private int myLiteralCounter;

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
        myLiteralCounter = 0;
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
        // If this is not an infix operator we have seen, then add it to our map
        if (!myExpLabels.containsKey(exp.getOperatorAsString())) {
            myExpLabels.put(exp.getOperatorAsString(), myNextLabel);
            myNextLabel++;
        }
    }

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

        // YS: We will need to special handle DotExp since it is
        // really one named expression separated by dots.
        List<Exp> segments = exp.getSegments();
        for (Exp e : segments) {
            if (e instanceof FunctionExp) {
                // YS: For right now we can handle everything,
                // but FunctionExp (i.e., Stack.Val_in(...))
                // since we don't know how to do deal with them yet.
                throw new SourceErrorException("[nProver] Cannot handle function " + ((FunctionExp) e).getName()
                        + " in the dot expression " + exp, e.getLocation());
            }
        }

        // YS: If we got here, we didn't have any FunctionExps inside our DotExp
        // Logic for handling dot expressions
        storeInArgumentCache(exp);

        postDotExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
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
    public void postFunctionExp(FunctionExp exp) {
        // If this is not a function name we have seen, then add it to our map
        if (!myExpLabels.containsKey(exp.getOperatorAsString())) {
            myExpLabels.put(exp.getOperatorAsString(), myNextLabel);
            myNextLabel++;
        }
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
        preAbstractFunctionExp(exp);
        preFunctionExp(exp);

        // YS: We walk the arguments first
        List<Exp> arguments = exp.getArguments();
        for (Exp e : arguments) {
            TreeWalker.visit(this, e);
        }

        // YS: We then walk any carat expressions
        if (exp.getCaratExp() != null) {
            TreeWalker.visit(this, exp.getCaratExp());
        }

        // YS: Lastly, we walk the name of the function
        TreeWalker.visit(this, exp.getName());

        postFunctionExp(exp);
        postAbstractFunctionExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
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
        // YS: LiteralExps are so common, so we need to allow for duplicates my storing a location detail model
        if (exp.getLocationDetailModel() == null) {
            myLiteralCounter++;
            exp.setLocationDetailModel(new LocationDetailModel(exp.getLocation().clone(), exp.getLocation().clone(),
                    "[Prover] Encountered Literal Exp #" + myLiteralCounter));
        }
        storeInArgumentCache(exp);
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
    public void postOutfixExp(OutfixExp exp) {
        // If this is not an outfix operator we have seen, then add it to our map
        if (!myExpLabels.containsKey(exp.getOperatorAsString())) {
            myExpLabels.put(exp.getOperatorAsString(), myNextLabel);
            myNextLabel++;
        }
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
    public void postPrefixExp(PrefixExp exp) {
        // If this is not a prefix operator we have seen, then add it to our map
        if (!myExpLabels.containsKey(exp.getOperatorAsString())) {
            myExpLabels.put(exp.getOperatorAsString(), myNextLabel);
            myNextLabel++;
        }
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
    public void postSetCollectionExp(SetCollectionExp exp) {
        // If this is not a prefix operator we have seen, then add it to our map
        if (!myExpLabels.containsKey("{_}")) {
            myExpLabels.put("{_}", myNextLabel);
            myNextLabel++;
        }
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

        // YS: Walk each of the expressions inside SetCollectionExp
        Set<MathExp> vars = exp.getVars();
        for (MathExp v : vars) {
            TreeWalker.visit(this, v);
        }

        postSetCollectionExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
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
    public void postTupleExp(TupleExp exp) {
        // If this is not a prefix operator we have seen, then add it to our map
        if (!myExpLabels.containsKey("(_)")) {
            myExpLabels.put("(_)", myNextLabel);
            myNextLabel++;
        }
    }

    /**
     * <p>
     * This method redefines how a {@link TupleExp} should be walked.
     * </p>
     *
     * @param exp
     *            A tuple expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkTupleExp(TupleExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preTupleExp(exp);

        // YS: Walk each of the expressions inside TupleExp
        List<Exp> fields = exp.getFields();
        for (Exp field : fields) {
            TreeWalker.visit(this, field);
        }

        postTupleExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
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
        // Logic for handling variable expressions
        storeInArgumentCache(exp);
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

        // YS: Need special handle VarExp inside a VCVarExp
        if (exp.getExp() instanceof VarExp) {
            // YS: A VCVarExp is something like: a' or a'''.
            // We don't want all the variations so rather than walking
            // the inner expression, we simply store the expression
            storeInArgumentCache(exp);
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

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method that updates the label number if it is the first time we see the expression and put the results
     * from the registry to our argument cache.
     * </p>
     *
     * @param exp
     *            Expression that we are currently evaluating.
     */
    private void storeInArgumentCache(Exp exp) {
        if (!myExpLabels.containsKey(exp.toString())) {
            myExpLabels.put(exp.toString(), myNextLabel);
            myNextLabel++;
        }

        // Logic for handling variable, VC variable and literal expressions as
        // arguments to other functions and operators.
        int variableNumber = myExpLabels.get(exp.toString());
        if (myRegistry.checkIfRegistered(variableNumber)) {
            myArgumentsCache.put(exp, myRegistry.getAccessorFor(variableNumber));
        } else {
            myArgumentsCache.put(exp, myRegistry.registerCluster(variableNumber));
        }
    }

}