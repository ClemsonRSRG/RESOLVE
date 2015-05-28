/**
 * TypeRelationship.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typereasoning2;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.typeandpopulate2.BindingException;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.NoSolutionException;
import edu.clemson.cs.r2jt.typeandpopulate2.TypeMismatchException;

import java.util.*;

/**
 * Indicates how one type relates to another, including a
 * binding expression and binding conditions. This information
 * is gathered from a type theorem.
 */
public class TypeRelationship {

    private final TypeGraph myTypeGraph;
    private final MTType myDestinationType;
    private final ExprAST myBindingCondition;
    private final BindingExpression myBindingExpression;
    private final List<TypeRelationshipPredicate> myStaticPredicates;

    public TypeRelationship(TypeGraph typeGraph, MTType destinationType,
            ExprAST bindingCondition, ExprAST bindingExpression,
            List<TypeRelationshipPredicate> staticPredicates) {

        myTypeGraph = typeGraph;
        myDestinationType = destinationType;
        myBindingCondition = ExprAST.copy(bindingCondition);
        myBindingExpression =
                new BindingExpression(myTypeGraph, bindingExpression);
        myStaticPredicates =
                new LinkedList<TypeRelationshipPredicate>(staticPredicates);
    }

    public MTType getDestinationType() {
        return myDestinationType;
    }

    public List<TypeRelationshipPredicate> getStaticPredicates() {
        return Collections.unmodifiableList(myStaticPredicates);
    }

    public ExprAST getCondition() {
        return myBindingCondition;
    }

    public String toString() {
        return "Destination: " + myDestinationType + "\nBindingExpression: "
                + myBindingExpression + "\nPredicates: " + myStaticPredicates
                + "\nCondition: " + myBindingCondition;
    }

    public String getDestinationTypeString() {
        throw new UnsupportedOperationException(
                "Don't know what this should do");
        //return myDestinationTypeNode.getType().toString();
    }

    public boolean hasTrivialBindingCondition() {
        return myBindingCondition.isLiteralTrue();
    }

    public Map<String, ExprAST> exprBinds(ExprAST expr,
            Map<String, MTType> typeBindings)
            throws TypeMismatchException,
                BindingException {

        return myBindingExpression.bindTo(expr, typeBindings);
    }

    public String getBindingExpressionString() {
        return myBindingExpression.toString();
    }

    public MTType getSourceType() {
        return myBindingExpression.getType();
    }

    public ExprAST getValidTypeConditionsTo(MTType value,
            Map<String, MTType> typeBindings) throws NoSolutionException {

        //The Exp hierarchy is a Lovecraftian nightmare-scape.  If we can avoid
        //having to reason about it, we will.  So while we could certainly
        //convert "value" into an Exp and piggy-back on the logic of the other
        //version of this method, we won't.

        MTType bindingExpressionTypeValue = myBindingExpression.getTypeValue();
        if (bindingExpressionTypeValue == null) {
            //If our bindingExpression doesn't define a type, there's no way
            //it binds to a type
            throw new NoSolutionException(new BindingException(value,
                    myBindingExpression));
        }

        MTType substitutedValue =
                TypeGraph.getCopyWithVariablesSubstituted(value, typeBindings);
        MTType substitutedBinding =
                TypeGraph.getCopyWithVariablesSubstituted(
                        bindingExpressionTypeValue, typeBindings);

        Map<String, MTType> internalBindings;
        try {
            internalBindings =
                    substitutedValue.bindTo(substitutedBinding, typeBindings);
        }
        catch (BindingException be) {
            throw new NoSolutionException(be);
        }

        //TODO : Converting from MTTypes to Exps is a bitch, and right now our
        //       typing system shouldn't really permit any type binding to
        //       occur (this situation would be caught in the TypeGraph as an
        //		 unbound quantifier).  We just ignore internalBindings for now,
        //       all we care about is that the above would have thrown an
        //       BindingException if we couldn't bind.

        boolean holdsSoFar = true;
        Iterator<TypeRelationshipPredicate> predicates =
                myStaticPredicates.iterator();
        while (holdsSoFar && predicates.hasNext()) {
            holdsSoFar =
                    predicates.next().canBeDemonstratedStatically(
                            value.getType(), myDestinationType, typeBindings,
                            new HashMap<String, ExprAST>());
        }

        if (!holdsSoFar) {
            throw new NoSolutionException("Predicates do not hold.");
        }

        //This is a valid typing, just need to pretty up our binding
        //conditions and return them
        ExprAST result =
                TypeGraph.getCopyWithVariablesSubstituted(myBindingCondition,
                        typeBindings);

        return result;
    }

    public ExprAST getValidTypeConditionsTo(ExprAST value,
            Map<String, MTType> typeBindings) throws NoSolutionException {

        ExprAST result;

        Map<String, ExprAST> internalBindings;
        try {
            internalBindings = myBindingExpression.bindTo(value, typeBindings);
        }
        catch (TypeMismatchException tme) {
            throw new NoSolutionException(tme);
        }
        catch (BindingException be) {
            throw new NoSolutionException(be);
        }

        boolean holdsSoFar = true;
        Iterator<TypeRelationshipPredicate> predicates =
                myStaticPredicates.iterator();
        while (holdsSoFar && predicates.hasNext()) {
            holdsSoFar =
                    predicates.next().canBeDemonstratedStatically(
                            value.getMathType(), myDestinationType,
                            typeBindings, internalBindings);
        }

        if (!holdsSoFar) {
            throw new NoSolutionException("Predicates did not hold.");
        }

        //This is a valid typing, just need to pretty up our binding 
        //conditions and return them
        result =
                TypeGraph.getCopyWithVariablesSubstituted(myBindingCondition,
                        typeBindings);
        result = result.substituteNames(internalBindings);

        return result;
    }
}
