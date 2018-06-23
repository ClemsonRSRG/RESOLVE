/*
 * TypeNode.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.typereasoning;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.MathExp;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSolutionException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.TypeMismatchException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.relationships.TypeRelationship;
import java.util.*;

/**
 * <p>Generates a new node in our {@link TypeGraph} for the specified
 * {@link MTType}.</p>
 *
 * @version 2.0
 */
public class TypeNode {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Search strategy for {@link Exp}s.</p> */
    private static final ExpValuePathStrategy EXP_VALUE_PATH =
            new ExpValuePathStrategy();

    /** <p>Search strategy for {@link MTType}s.</p> */
    private static final MTTypeValuePathStrategy MTTYPE_VALUE_PATH =
            new MTTypeValuePathStrategy();

    /** <p>The {@link MTType} for the new node.</p> */
    private final MTType myType;

    /** <p>A map from math types to relationships.</p> */
    private final Map<MTType, Set<TypeRelationship>> myRelationships;

    /** <p>The current type graph object in use.</p> */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a node for the {@link MTType} object
     * in our type graph.</p>
     *
     * @param g The current type graph.
     * @param type The type we are adding to our type graph.
     */
    public TypeNode(TypeGraph g, MTType type) {
        myType = type;
        myRelationships = new HashMap<>();
        myTypeGraph = g;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Returns the type stored inside this node.</p>
     *
     * @return The {@link MTType} type object.
     */
    public final MTType getType() {
        return myType;
    }

    /**
     * <p>This generates an {@link Exp} that establishes the relationship
     * between the {@link Exp} value and the destination {@link MTType}.</p>
     *
     * @param value The {@link Exp} to be evaluated.
     * @param dst The type that we are trying to establish a relationship to.
     * @param bindings Map of established type bindings.
     *
     * @return An {@link Exp} establishing the relationship.
     *
     * @throws TypeMismatchException A type mismatch between what we are trying
     * to find the valid type conditions to.
     */
    public final Exp getValidTypeConditionsTo(Exp value, MTType dst,
            Map<String, MTType> bindings) throws TypeMismatchException {

        return getValidTypeConditionsTo(value, dst, bindings, EXP_VALUE_PATH);
    }

    /**
     * <p>This generates an {@link Exp} that establishes the relationship
     * between the {@link MTType} value and the destination {@link MTType}.</p>
     *
     * @param value The {@link MTType} to be evaluated.
     * @param dst The type that we are trying to establish a relationship to.
     * @param bindings Map of established type bindings.
     *
     * @return An {@link Exp} establishing the relationship.
     *
     * @throws TypeMismatchException A type mismatch between what we are trying
     * to find the valid type conditions to.
     */
    public final Exp getValidTypeConditionsTo(MTType value, MTType dst,
            Map<String, MTType> bindings) throws TypeMismatchException {

        return getValidTypeConditionsTo(value, dst, bindings, MTTYPE_VALUE_PATH);
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        StringBuffer str = new StringBuffer();

        for (Set<TypeRelationship> target : myRelationships.values()) {
            for (TypeRelationship rel : target) {
                str.append(rel);
                str.append("\n\n");
            }
        }

        if (myRelationships.values().isEmpty()) {
            str.append("\n");
        }

        return str.toString();
    }

    // ===========================================================
    // Package Private Methods
    // ===========================================================

    /**
     * <p>Add a relationship to this type node.</p>
     *
     * @param relationship Type relationship to be added.
     */
    //XXX : Can we do this so that analyzer isn't setting up TypeRelationship objects?
    void addRelationship(TypeRelationship relationship) {
        Set<TypeRelationship> bucket =
                myRelationships.get(relationship.getDestinationType());
        if (bucket == null) {
            bucket = new HashSet<>();
            myRelationships.put(relationship.getDestinationType(), bucket);
        }

        bucket.add(relationship);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method for generating an {@link Exp} that establishes the relationship
     * between the value and the destination {@link MTType}.</p>
     *
     * @param value The value to be evaluated.
     * @param dst The type that we are trying to establish a relationship to.
     * @param bindings Map of established type bindings.
     * @param pathStrategy The search strategy to be used.
     * @param <V> This could either be a {@link MTType} or a {@link Exp}.
     *
     * @return An {@link Exp} establishing the relationship.
     *
     * @throws TypeMismatchException
     */
    private <V> Exp getValidTypeConditionsTo(V value, MTType dst,
            Map<String, MTType> bindings,
            RelationshipPathStrategy<V> pathStrategy)
            throws TypeMismatchException {

        if (!myRelationships.containsKey(dst)) {
            throw new TypeMismatchException("The value: " + value
                    + " already has an established relationship.");
        }

        Exp finalConditions = MathExp.getFalseVarExp(null, myTypeGraph);
        Set<TypeRelationship> relationships = myRelationships.get(dst);
        boolean foundTrivialPath = false;
        Iterator<TypeRelationship> relationshipIter = relationships.iterator();
        TypeRelationship relationship;
        Exp relationshipConditions;
        while (!foundTrivialPath && relationshipIter.hasNext()) {
            relationship = relationshipIter.next();

            try {
                relationshipConditions =
                        pathStrategy.getValidTypeConditionsAlong(relationship,
                                value, bindings);

                foundTrivialPath =
                        (MathExp.isLiteralTrue(relationshipConditions));

                finalConditions =
                        MathExp.formDisjunct(relationshipConditions
                                .getLocation(), relationshipConditions,
                                finalConditions);
            }
            catch (NoSolutionException nse) {}
        }

        if (foundTrivialPath) {
            finalConditions = MathExp.getTrueVarExp(null, myTypeGraph);
        }

        return finalConditions;
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>Search strategy for type relationships.</p>
     *
     * @param <V> This could either be a {@link MTType} or a {@link Exp}.
     */
    interface RelationshipPathStrategy<V> {

        /**
         * <p>This generates a new expression generated from the
         * binding condition.</p>
         *
         * @param relationship A type relationship.
         * @param value The value to be evaluated.
         * @param bindings Map of established type bindings.
         *
         * @return The expression that binds the value to the type relationship.
         *
         * @throws NoSolutionException
         */
        Exp getValidTypeConditionsAlong(TypeRelationship relationship, V value,
                Map<String, MTType> bindings) throws NoSolutionException;

    }

    /**
     * <p>Search strategy for {@link Exp} type relationships.</p>
     */
    static class ExpValuePathStrategy implements RelationshipPathStrategy<Exp> {

        /**
         * <p>This generates a new expression generated from the
         * binding condition.</p>
         *
         * @param relationship A type relationship.
         * @param value The {@link Exp} to be evaluated.
         * @param bindings Map of established type bindings.
         *
         * @return The expression that binds the value to the type relationship.
         *
         * @throws NoSolutionException
         */
        @Override
        public Exp getValidTypeConditionsAlong(TypeRelationship relationship,
                Exp value, Map<String, MTType> bindings)
                throws NoSolutionException {

            return relationship.getValidTypeConditionsTo(value, bindings);
        }

    }

    /**
     * <p>Search strategy for {@link MTType} type relationships.</p>
     */
    static class MTTypeValuePathStrategy
            implements
                RelationshipPathStrategy<MTType> {

        /**
         * <p>This generates a new expression generated from the
         * binding condition.</p>
         *
         * @param relationship A type relationship.
         * @param value The {@link MTType} to be evaluated.
         * @param bindings Map of established type bindings.
         *
         * @return The expression that binds the value to the type relationship.
         *
         * @throws NoSolutionException
         */
        @Override
        public Exp getValidTypeConditionsAlong(TypeRelationship relationship,
                MTType value, Map<String, MTType> bindings)
                throws NoSolutionException {

            return relationship.getValidTypeConditionsTo(value, bindings);
        }

    }

}