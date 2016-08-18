/**
 * TypeGraph.java
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

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.typeandpopulate.exception.TypeMismatchException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.*;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.relationships.TypeRelationshipPredicate;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.FunctionApplicationFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Represents a directed graph of types, where edges between types
 * indicate a possible coercion that the type checker can perform.</p>
 *
 * @version 2.0
 */
public class TypeGraph {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>A set of non-thread-safe resources to be used during general type
     * reasoning. This really doesn't belong here, but anything that's reasoning
     * about types should already have access to a type graph, and only one type
     * graph is created per thread, so this is a convenient place to put it.</p>
     */
    public final PerThreadReasoningResources threadResources =
            new PerThreadReasoningResources();

    /** <p>A {@link NodePairPathStrategy} for {@link Exp}.</p> */
    private final ExpValuePathStrategy EXP_VALUE_PATH =
            new ExpValuePathStrategy();

    /** <p>A {@link NodePairPathStrategy} for {@link MTType}.</p> */
    private final MTTypeValuePathStrategy MTTYPE_VALUE_PATH =
            new MTTypeValuePathStrategy();

    /** <p>This contains all mathematical nodes for this graph.</p> */
    private final HashMap<MTType, TypeNode> myTypeNodes;

    // ===========================================================
    // Global Mathematical Types
    // ===========================================================

    public final MTType ENTITY = new MTProper(this, "Entity");
    public final MTProper CLS = new MTProper(this, null, true, "MType");
    public final MTProper BOOLEAN = new MTProper(this, CLS, false, "B");
    public final MTProper EMPTY_SET =
            new MTProper(this, CLS, false, "Empty_Set");
    public final MTProper VOID = new MTProper(this, CLS, false, "Void");
    public final MTType ELEMENT = new MTProper(this, "Element");

    public MTFunction POWERTYPE;
    public MTFunction POWERCLASS;
    public MTFunction UNION;
    public MTFunction INTERSECT;
    public MTFunction FUNCTION;
    public MTFunction CROSS;
    public MTFunction AND;
    public MTFunction NOT;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a mathematical type graph.</p>
     */
    public TypeGraph() {
        myTypeNodes = new HashMap<>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public void addRelationship(Exp bindingExpression, MTType destination,
            Exp bindingCondition, Scope environment) {}

    public boolean isSubtype(MTType subtype, MTType supertype) {
        return false;
    }

    public boolean isKnownToBeIn(Exp value, MTType expected) {
        return false;
    }

    public boolean isKnownToBeIn(MTType value, MTType expected) {
        return false;
    }

    public static MTType getCopyWithVariablesSubstituted(MTType original,
            Map<String, MTType> substitutions) {
        return null;
    }

    public static <T extends Exp> T getCopyWithVariablesSubstituted(T original,
            Map<String, MTType> substitutions) {
        return null;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>An helper class that helps establish canonicalization results for
     * a {@link MTType}.</p>
     */
    private class CanonicalizationResult {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /** <p>A mathematical type.</p> */
        final MTType canonicalType;

        /** <p>A list of established type relationships.</p> */
        final List<TypeRelationshipPredicate> predicates;

        /** <p>A map of conversions.</p> */
        final Map<String, String> canonicalToEnvironmental;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This creates a canonicalization result for {@code canonicalType}.</p>
         *
         * @param canonicalType A mathematical type.
         * @param predicates A list of established type relationships.
         * @param canonicalToOriginal A map of conversions.
         */
        CanonicalizationResult(MTType canonicalType,
                List<TypeRelationshipPredicate> predicates,
                Map<String, String> canonicalToOriginal) {
            this.canonicalType = canonicalType;
            this.predicates = predicates;
            this.canonicalToEnvironmental = canonicalToOriginal;
        }

    }

    /**
     * <p>A strategy pattern interface for a class type {@code V}
     * that tests valid type conditions between a source and expected
     * {@link MTType MTTypes}.</p>
     *
     * @param <V> The class of objects to be tested.
     */
    private interface NodePairPathStrategy<V> {

        /**
         * <p>This method establishes a valid type conditions for {@code sourceValue}
         * using {@code sourceType}, {@code expectedType} and {@code bindings}.</p>
         *
         * @param sourceValue An object of type {@link V}.
         * @param sourceType The mathematical source type.
         * @param expectedType The mathematical expected type.
         * @param bindings Map of established type bindings.
         *
         * @return An {@link Exp} with the valid type conditions
         * between the types.
         *
         * @throws TypeMismatchException We cannot establish a type condition
         * between the types for {@code sourceValue}.
         */
        Exp getValidTypeConditionsBetween(V sourceValue, MTType sourceType,
                MTType expectedType, Map<String, MTType> bindings)
                throws TypeMismatchException;

    }

    /**
     * <p>An implementation of {@link NodePairPathStrategy} for {@link Exp}.</p>
     */
    private class ExpValuePathStrategy implements NodePairPathStrategy<Exp> {

        /**
         * <p>This method establishes a valid type conditions for {@code sourceValue}
         * using {@code sourceType}, {@code expectedType} and {@code bindings}.</p>
         *
         * @param sourceValue An {@link Exp}.
         * @param sourceType The mathematical source type.
         * @param expectedType The mathematical expected type.
         * @param bindings Map of established type bindings.
         *
         * @return An {@link Exp} with the valid type conditions
         * between the types.
         *
         * @throws TypeMismatchException We cannot establish a type condition
         * between the types for {@code sourceValue}.
         */
        @Override
        public final Exp getValidTypeConditionsBetween(Exp sourceValue,
                MTType sourceType, MTType expectedType,
                Map<String, MTType> bindings) throws TypeMismatchException {
            return myTypeNodes.get(sourceType).getValidTypeConditionsTo(
                    sourceValue, expectedType, bindings);
        }

    }

    /**
     * <p>An implementation of {@link NodePairPathStrategy} for {@link MTType}.</p>
     */
    private class MTTypeValuePathStrategy
            implements
                NodePairPathStrategy<MTType> {

        /**
         * <p>This method establishes a valid type conditions for {@code sourceValue}
         * using {@code sourceType}, {@code expectedType} and {@code bindings}.</p>
         *
         * @param sourceValue A {@link MTType}.
         * @param sourceType The mathematical source type.
         * @param expectedType The mathematical expected type.
         * @param bindings Map of established type bindings.
         *
         * @return An {@link Exp} with the valid type conditions
         * between the types.
         *
         * @throws TypeMismatchException We cannot establish a type condition
         * between the types for {@code sourceValue}.
         */
        @Override
        public final Exp getValidTypeConditionsBetween(MTType sourceValue,
                MTType sourceType, MTType expectedType,
                Map<String, MTType> bindings) throws TypeMismatchException {
            return myTypeNodes.get(sourceType).getValidTypeConditionsTo(
                    sourceValue, expectedType, bindings);
        }

    }

    /**
     * <p>This creates a {@link MTPowertypeApplication} type.</p>
     */
    private static class PowertypeApplicationFactory
            implements
                FunctionApplicationFactory {

        /**
         * <p>This method returns a {@link MTType} resulting from a
         * function application.</p>
         *
         * @param g The current type graph.
         * @param f The function to be applied.
         * @param calledAsName The name for this function application type.
         * @param arguments List of arguments for applying the function.
         *
         * @return A function application {@link MTType}.
         */
        @Override
        public final MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTPowertypeApplication(g, arguments.get(0));
        }

    }

    /**
     * <p>This creates a {@link MTUnion} type.</p>
     */
    private static class UnionApplicationFactory
            implements
                FunctionApplicationFactory {

        /**
         * <p>This method returns a {@link MTType} resulting from a
         * function application.</p>
         *
         * @param g The current type graph.
         * @param f The function to be applied.
         * @param calledAsName The name for this function application type.
         * @param arguments List of arguments for applying the function.
         *
         * @return A function application {@link MTType}.
         */
        @Override
        public final MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTUnion(g, arguments);
        }

    }

    /**
     * <p>This creates a {@link MTIntersect} type.</p>
     */
    private static class IntersectApplicationFactory
            implements
                FunctionApplicationFactory {

        /**
         * <p>This method returns a {@link MTType} resulting from a
         * function application.</p>
         *
         * @param g The current type graph.
         * @param f The function to be applied.
         * @param calledAsName The name for this function application type.
         * @param arguments List of arguments for applying the function.
         *
         * @return A function application {@link MTType}.
         */
        @Override
        public final MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTIntersect(g, arguments);
        }

    }

    /**
     * <p>This creates a {@link MTFunction} type.</p>
     */
    private static class FunctionConstructorApplicationFactory
            implements
                FunctionApplicationFactory {

        /**
         * <p>This method returns a {@link MTType} resulting from a
         * function application.</p>
         *
         * @param g The current type graph.
         * @param f The function to be applied.
         * @param calledAsName The name for this function application type.
         * @param arguments List of arguments for applying the function.
         *
         * @return A function application {@link MTType}.
         */
        @Override
        public final MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTFunction(g, arguments.get(1), arguments.get(0));
        }

    }

    /**
     * <p>This creates a {@link MTCartesian} type.</p>
     */
    private static class CartesianProductApplicationFactory
            implements
                FunctionApplicationFactory {

        /**
         * <p>This method returns a {@link MTType} resulting from a
         * function application.</p>
         *
         * @param g The current type graph.
         * @param f The function to be applied.
         * @param calledAsName The name for this function application type.
         * @param arguments List of arguments for applying the function.
         *
         * @return A function application {@link MTType}.
         */
        @Override
        public final MTType buildFunctionApplication(TypeGraph g, MTFunction f,
                String calledAsName, List<MTType> arguments) {
            return new MTCartesian(g,
                    new MTCartesian.Element(arguments.get(0)),
                    new MTCartesian.Element(arguments.get(1)));
        }

    }

    /**
     * <p>An helper class that indicates an established type relationship
     * between two {@link MTType MTTypes}.</p>
     */
    private static class EstablishedRelationship {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /** <p>The mathematical types that has been established a type relationship.</p> */
        private final MTType myType1, myType2;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructs an object that indicates that we have established a
         * type relationship between {@code t1} and {@code t2}.</p>
         *
         * @param t1 A mathematical type.
         * @param t2 Another mathematical type.
         */
        EstablishedRelationship(MTType t1, MTType t2) {
            myType1 = t1;
            myType2 = t2;
        }

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>This method overrides the default {@code hashCode} method implementation
         * for the {@code EstablishedRelationship} class.</p>
         *
         * @return The hash code associated with the object.
         */
        @Override
        public final int hashCode() {
            return myType1.hashCode() * 31 + myType2.hashCode();
        }

        /**
         * <p>This method overrides the default {@code equals} method implementation
         * to ensure that we have a correctly established type relationship.</p>
         *
         * @param o Object to be compared.
         *
         * @return {@code true} if all the fields are equal, {@code false} otherwise.
         */
        @Override
        public final boolean equals(Object o) {
            boolean result = o instanceof EstablishedRelationship;

            if (result) {
                EstablishedRelationship oAsER = (EstablishedRelationship) o;
                result =
                        myType1.equals(oAsER.myType1)
                                && myType2.equals(oAsER.myType2);
            }

            return result;
        }

    }

}