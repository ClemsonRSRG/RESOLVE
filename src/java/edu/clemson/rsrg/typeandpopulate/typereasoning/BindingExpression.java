/*
 * BindingExpression.java
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
package edu.clemson.rsrg.typeandpopulate.typereasoning;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.AbstractFunctionExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.LambdaExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.TupleExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.exception.BindingException;
import edu.clemson.rsrg.typeandpopulate.exception.TypeMismatchException;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * This class attempts to bind an expression using the type graph and any type bindings we currently have.
 * </p>
 *
 * @version 2.0
 */
public class BindingExpression {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The current type graph object in use.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    /**
     * <p>
     * The expression we are attempting to bind
     * </p>
     */
    private Exp myExpression;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs an object where we are going to attempt bind the {@code expression} passed in using our current
     * {@link TypeGraph}.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param expression
     *            The expression we are attempting to bind.
     */
    public BindingExpression(TypeGraph g, Exp expression) {
        myExpression = expression;
        myTypeGraph = g;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method attempts to bind the expression in this object with the expression passed in and the map of type
     * bindings.
     * </p>
     *
     * @param expr
     *            The expression we want bind our expression to.
     * @param typeBindings
     *            A map of type bindings.
     *
     * @return A map containing all the expressions we bound.
     *
     * @throws TypeMismatchException
     *             A type mismatch between what we are trying to bind.
     * @throws BindingException
     *             Some error occurred during the binding process.
     */
    public Map<String, Exp> bindTo(Exp expr, Map<String, MTType> typeBindings)
            throws TypeMismatchException, BindingException {
        Map<String, Exp> result = new HashMap<>();
        bindTo(myExpression, expr, typeBindings, result);

        return result;
    }

    /**
     * <p>
     * This method gets the mathematical type associated with the expression in this object.
     * </p>
     *
     * @return The {@link MTType} type object.
     */
    public final MTType getType() {
        return myExpression.getMathType();
    }

    /**
     * <p>
     * This method gets the mathematical type value associated with the expression in this object.
     * </p>
     *
     * @return The {@link MTType} type object.
     */
    public final MTType getTypeValue() {
        return myExpression.getMathTypeValue();
    }

    /**
     * <p>
     * This method returns the object in string format.
     * </p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        return myExpression.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void bindTo(Exp expr1, Exp expr2, Map<String, MTType> typeBindings, Map<String, Exp> accumulator)
            throws TypeMismatchException, BindingException {

        // TODO : Ultimately, in theory, one of the arguments THEMSELVES could
        // involve a reference to a named type. We don't deal with that
        // case (only the case where the TYPE of the argument involves a
        // named type.)

        // Either type might actually be a named type that's already been mapped,
        // so perform the substitution if necessary
        MTType expr1Type = getTypeUnderBinding(expr1.getMathType(), typeBindings);
        MTType expr2Type = getTypeUnderBinding(expr2.getMathType(), typeBindings);

        if (!myTypeGraph.isSubtype(expr2Type, expr1Type)) {
            throw new TypeMismatchException("Type: " + expr2Type + " is not a subtype of type: " + expr1);
        }

        if (expr1 instanceof VarExp) {
            VarExp e1AsVarExp = (VarExp) expr1;
            String e1Name = e1AsVarExp.getName().getName();

            if (e1AsVarExp.getQuantification() == SymbolTableEntry.Quantification.UNIVERSAL) {
                if (accumulator.containsKey(e1Name)) {
                    bindTo(accumulator.get(e1Name), expr2, typeBindings, accumulator);
                } else {
                    accumulator.put(e1Name, expr2);
                }
            } else {
                if (expr2 instanceof VarExp) {
                    VarExp e2AsVarExp = (VarExp) expr2;

                    if (!e1Name.equals(e2AsVarExp.getName().getName())) {
                        throw new BindingException(expr1, expr2);
                    }
                } else {
                    throw new BindingException(expr1, expr2);
                }
            }
        } else if (expr1 instanceof AbstractFunctionExp && expr2 instanceof AbstractFunctionExp) {

            AbstractFunctionExp funExpr1 = (AbstractFunctionExp) expr1;
            String fun1Name = funExpr1.getOperatorAsString();

            AbstractFunctionExp funExpr2 = (AbstractFunctionExp) expr2;

            if (funExpr1.getQuantification() == SymbolTableEntry.Quantification.UNIVERSAL) {
                if (accumulator.containsKey(fun1Name)) {
                    fun1Name = ((AbstractFunctionExp) accumulator.get(fun1Name)).getOperatorAsString();

                    if (!fun1Name.equals(funExpr2.getOperatorAsString())) {
                        throw new BindingException(expr1, expr2);
                    }
                } else {
                    accumulator.put(fun1Name, expr2);

                    /*
                     * if (myTypeGraph.isSubtype(expr2Type, expr1Type)) { accumulator.put(fun1Name, expr2); } else {
                     * throw new TypeMismatchException(expr1.getMathType(), expr2.getMathType()); }
                     */
                }
            } else {
                if (!fun1Name.equals(funExpr2.getOperatorAsString())) {
                    throw new BindingException(expr1, expr2);
                }
            }

            /*
             * if (!myTypeGraph.isSubtype(expr2Type, expr1Type)) { throw new TypeMismatchException(expr1.getMathType(),
             * expr2.getMathType()); }
             */

            Iterator<Exp> fun1Args = funExpr1.getParameters().iterator();
            Iterator<Exp> fun2Args = funExpr2.getParameters().iterator();

            // There must be the same number of parameters, otherwise the
            // original typecheck would have failed
            while (fun1Args.hasNext()) {
                bindTo(fun1Args.next(), fun2Args.next(), typeBindings, accumulator);
            }
        } else if (expr1 instanceof TupleExp) {

            TupleExp expr1AsTupleExp = (TupleExp) expr1;

            // TODO : Do we need to somehow "descend" (into what is in all
            // likelihood a DummyExp) and match universal fields to sub
            // components of expr2?

            // We checked earlier that it's a subtype. So, if it's universally
            // quantified--we're done here.
            if (!expr1AsTupleExp.isUniversallyQuantified()) {
                Iterator<Exp> tuple1Fields = ((TupleExp) expr1).getFields().iterator();
                Iterator<Exp> tuple2Fields = ((TupleExp) expr2).getFields().iterator();

                // There must be the same number of fields, otherwise the above
                // typecheck would have failed
                while (tuple1Fields.hasNext()) {
                    bindTo(tuple1Fields.next(), tuple2Fields.next(), typeBindings, accumulator);
                }
            }
        } else if (expr1 instanceof LambdaExp && expr2 instanceof LambdaExp) {
            LambdaExp expr1AsLambdaExp = (LambdaExp) expr1;
            LambdaExp expr2AsLambdaExp = (LambdaExp) expr2;

            // Note that we don't have to worry about parameters counts or types:
            // the original type check would have kicked us out if those didn't
            // match

            bindTo(expr1AsLambdaExp.getBody(), expr2AsLambdaExp.getBody(), typeBindings, accumulator);

        } else {
            throw new BindingException(expr1, expr2);
        }
    }

    /**
     * <p>
     * This method gets the mathematical type for the passed in type after we apply all the type bindings.
     * </p>
     *
     * @param original
     *            The original mathematical type.
     * @param typeBindings
     *            A map of type bindings.
     *
     * @return The {@link MTType} type object.
     */
    private MTType getTypeUnderBinding(MTType original, Map<String, MTType> typeBindings) {
        return original.getCopyWithVariablesSubstituted(typeBindings);
    }

}
