/*
 * TypeRelationship.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.typereasoning.relationships;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.exception.BindingException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSolutionException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.TypeMismatchException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.BindingExpression;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.*;

/**
 * <p>Indicates how one type relates to another, including a
 * binding expression and binding conditions. This information
 * is gathered from a type theorem.</p>
 *
 * @version 2.0
 */
public class TypeRelationship {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The current type graph object in use.</p> */
    private final TypeGraph myTypeGraph;

    /** <p>The type that we are trying to establish a relationship to.</p> */
    private final MTType myDestinationType;

    /** <p>The biding condition.</p> */
    private final Exp myBindingCondition;

    /** <p>The expression we are binding.</p> */
    private final BindingExpression myBindingExpression;

    /** <p>A list containing all the type relationship predicates.</p> */
    private final List<TypeRelationshipPredicate> myStaticPredicates;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a type relationship between types gathered
     * from a type theorem.</p>
     *
     * @param g The current type graph.
     * @param destinationType The type we are trying to establish a relationship to.
     * @param bindingCondition The binding condition for this type.
     * @param bindingExpression The expression we are binding to.
     * @param staticPredicates The list containing all the relationship predicates.
     */
    public TypeRelationship(TypeGraph g, MTType destinationType, Exp bindingCondition,
            Exp bindingExpression, List<TypeRelationshipPredicate> staticPredicates) {
        myTypeGraph = g;
        myDestinationType = destinationType;
        myBindingCondition = bindingCondition.clone();
        myBindingExpression = new BindingExpression(myTypeGraph, bindingExpression);
        myStaticPredicates = new LinkedList<>(staticPredicates);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Returns the binding expression as a string.</p>
     *
     * @return Binding expression as a string.
     */
    public final String getBindingExpressionString() {
        return myBindingExpression.toString();
    }

    /**
     * <p>Returns the condition expression for this type relationship.</p>
     *
     * @return An {@link Exp} object.
     */
    public final Exp getCondition() {
        return myBindingCondition;
    }

    /**
     * <p>Returns the type we are trying to establish a relationship to.</p>
     *
     * @return The {@link MTType} type object.
     */
    public final MTType getDestinationType() {
        return myDestinationType;
    }

    /**
     * <p>Returns the source type we are trying to establish a relationship.</p>
     *
     * @return The {@link MTType} type object.
     */
    public final MTType getSourceType() {
        return myBindingExpression.getType();
    }

    /**
     * <p>Returns a substituted expression generated from binding condition with the
     * mathematical type bounded to it.</p>
     *
     * @param value The {@link MTType} value object we are binding.
     * @param typeBindings Map of established type bindings.
     *
     * @return The substituted expression that got the mathematical type to bind to.
     *
     * @throws NoSolutionException
     */
    public final Exp getValidTypeConditionsTo(MTType value,
            Map<String, MTType> typeBindings) throws NoSolutionException {

        //The Exp hierarchy is a Lovecraftian nightmare-scape.  If we can avoid
        //having to reason about it, we will.  So while we could certainly
        //convert "value" into an Exp and piggy-back on the logic of the other
        //version of this method, we won't.

        MTType bindingExpressionTypeValue = myBindingExpression.getTypeValue();
        if (bindingExpressionTypeValue == null) {
            //If our bindingExpression doesn't define a type, there's no way
            //it binds to a type
            throw new NoSolutionException("", new BindingException(value,
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
            throw new NoSolutionException(be.getMessage(), be);
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
                            new HashMap<String, Exp>());
        }

        if (!holdsSoFar) {
            throw new NoSolutionException("Predicates do not hold.", null);
        }

        //This is a valid typing, just need to pretty up our binding
        //conditions and return them
        Exp result =
                TypeGraph.getCopyWithVariablesSubstituted(myBindingCondition,
                        typeBindings);

        return result;
    }

    /**
     * <p>Returns a substituted expression generated from binding condition
     * from the {@code value}.</p>
     *
     * @param value The {@link Exp} value object we are retrieving the type conditions to.
     * @param typeBindings Map of established type bindings.
     *
     * @return The substituted expression that got the value bound to.
     *
     * @throws NoSolutionException
     */
    public final Exp getValidTypeConditionsTo(Exp value,
            Map<String, MTType> typeBindings) throws NoSolutionException {

        Exp result;

        Map<String, Exp> internalBindings;
        try {
            internalBindings = myBindingExpression.bindTo(value, typeBindings);
        }
        catch (TypeMismatchException tme) {
            throw new NoSolutionException(tme.getMessage(), tme);
        }
        catch (BindingException be) {
            throw new NoSolutionException(be.getMessage(), be);
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
            throw new NoSolutionException("Predicates did not hold.", null);
        }

        //This is a valid typing, just need to pretty up our binding
        //conditions and return them
        result = TypeGraph.getCopyWithVariablesSubstituted(myBindingCondition, typeBindings);

        Map<Exp, Exp> finalSubstitutions = new HashMap<>();
        for (Map.Entry<String, Exp> substitution : internalBindings.entrySet()) {
            Location newExpLoc = null;
            Location newPosSymbolLoc = null;
            if (value.getLocation() != null) {
                newExpLoc = value.getLocation().clone();
                newPosSymbolLoc = value.getLocation().clone();
            }

            finalSubstitutions.put(new VarExp(newExpLoc, null,
                    new PosSymbol(newPosSymbolLoc, substitution.getKey())),
                    substitution.getValue());
        }
        result = result.substitute(finalSubstitutions);

        return result;
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    public final String toString() {
        return "Destination: " + myDestinationType + "\nBindingExpression: "
                + myBindingExpression + "\nPredicates: " + myStaticPredicates
                + "\nCondition: " + myBindingCondition;
    }

}