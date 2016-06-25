/**
 * EqualsPredicate.java
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
package edu.clemson.cs.rsrg.typeandpopulate.typereasoning;

import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.VariableReplacingVisitor;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import java.util.Map;

/**
 * <p>This class establishes that two {@link MTType}s can be established
 * to be "equals".</p>
 *
 * @version 2.0
 */
public class EqualsPredicate implements TypeRelationshipPredicate {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The first {@link MTType} object in this equals predicate.</p> */
    private final MTType myType1;

    /** <p>The second {@link MTType} object in this equals predicate.</p> */
    private final MTType myType2;

    /** <p>The current type graph object in use.</p> */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new "equals" predicate for two types.</p>
     *
     * @param g The current type graph.
     * @param type1 First {@link MTType} object.
     * @param type2 Second {@link MTType} object.
     */
    public EqualsPredicate(TypeGraph g, MTType type1, MTType type2) {
        myType1 = type1;
        myType2 = type2;
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
    public final boolean canBeDemonstratedStatically(MTType canonical1,
            MTType canonical2, Map<String, MTType> typeBindings,
            Map<String, Exp> expressionBindings) {
        MTType substituted1 =
                myType1.getCopyWithVariablesSubstituted(typeBindings);
        MTType substituted2 =
                myType2.getCopyWithVariablesSubstituted(typeBindings);

        //TODO : This was not well considered, it just made some fun stuff work
        //       out right.  So think about if it's ok to make this a "subset
        //       of" predicate rather than an "equals" predicate.

        return myTypeGraph.isSubtype(substituted1, substituted2);
        //return substituted1.equals(substituted2);
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
    public final TypeRelationshipPredicate replaceUnboundVariablesInTypes(
            Map<String, String> substitutions) {
        VariableReplacingVisitor renamer =
                new VariableReplacingVisitor(substitutions, myTypeGraph);

        myType1.accept(renamer);
        MTType newType1 = renamer.getFinalExpression();

        renamer = new VariableReplacingVisitor(substitutions, myTypeGraph);

        myType2.accept(renamer);
        MTType newType2 = renamer.getFinalExpression();

        return new EqualsPredicate(myTypeGraph, newType1, newType2);
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        return myType1 + " = " + myType2;
    }

}