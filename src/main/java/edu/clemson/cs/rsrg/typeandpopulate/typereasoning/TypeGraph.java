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
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTFunction;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTProper;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import java.util.HashMap;
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

}