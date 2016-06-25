/**
 * IsInPredicate.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.typereasoning.relationships;

import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.VariableReplacingVisitor;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import java.util.Map;

/**
 * <p>This class establishes that one {@link MTType} can be established
 * as "is-in" another {@link MTType}.</p>
 *
 * @version 2.0
 */
public class IsInPredicate implements TypeRelationshipPredicate {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The element {@link MTType} object in this is-in predicate.</p> */
    private final MTType myElement;

    /** <p>The declared {@link MTType} object in this is-in predicate.</p> */
    private final MTType myDeclaredType;

    /** <p>The current type graph object in use.</p> */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new "is-in" predicate for two types.</p>
     *
     * @param g The current type graph.
     * @param element The element {@link MTType} to be contained.
     * @param declaredType A declared {@link MTType} that can contain types.
     */
    public IsInPredicate(TypeGraph g, MTType element, MTType declaredType) {
        myElement = element;
        myDeclaredType = declaredType;
        myTypeGraph = g;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Given two types, we attempt to statically demonstrate if
     * their type relationship can be established statically.</p>
     *
     * @param canonical1 The first {@link MTType} object.
     * @param canonical2 The second {@link MTType} object.
     * @param typeBindings Map of established type bindings.
     * @param expressionBindings Map of established expression bindings.
     *
     * @return {@code true} if it can be demonstrated statically, {@code false} otherwise.
     */
    @Override
    public boolean canBeDemonstratedStatically(MTType canonical1,
            MTType canonical2, Map<String, MTType> typeBindings,
            Map<String, Exp> expressionBindings) {
        MTType substitutedElement =
                myElement.getCopyWithVariablesSubstituted(typeBindings);
        MTType substitutedDeclaredType =
                myDeclaredType.getCopyWithVariablesSubstituted(typeBindings);

        return myTypeGraph.isKnownToBeIn(substitutedElement,
                substitutedDeclaredType);
    }

    /**
     * <p>Given a map of substitutions, we attempt to replace any
     * unbound variables in types.</p>
     *
     * @param substitutions Map of substitutions for unbound variables.
     *
     * @return A {@code TypeRelationshipPredicate} after the substitution.
     */
    @Override
    public TypeRelationshipPredicate replaceUnboundVariablesInTypes(
            Map<String, String> substitutions) {
        VariableReplacingVisitor renamer =
                new VariableReplacingVisitor(substitutions, myTypeGraph);

        myElement.accept(renamer);
        MTType newType1 = renamer.getFinalExpression();

        renamer = new VariableReplacingVisitor(substitutions, myTypeGraph);

        myDeclaredType.accept(renamer);
        MTType newType2 = renamer.getFinalExpression();

        return new IsInPredicate(myTypeGraph, newType1, newType2);
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        return myElement + " : " + myDeclaredType;
    }
}