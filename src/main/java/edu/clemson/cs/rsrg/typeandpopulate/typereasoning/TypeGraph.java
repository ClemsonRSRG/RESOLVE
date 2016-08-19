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
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSolutionException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.TypeMismatchException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.*;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.relationships.TypeRelationshipPredicate;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.FunctionApplicationFactory;
import java.util.HashMap;
import java.util.Iterator;
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

    public static MTType getCopyWithVariablesSubstituted(MTType original,
            Map<String, MTType> substitutions) {
        return null;
    }

    public static <T extends Exp> T getCopyWithVariablesSubstituted(T original,
            Map<String, MTType> substitutions) {
        return null;
    }

    public boolean isSubtype(MTType subtype, MTType supertype) {
        return false;
    }

    public boolean isKnownToBeIn(Exp value, MTType expected) {
        return false;
    }

    public boolean isKnownToBeIn(MTType value, MTType expected) {
        return false;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * Returns the conditions required to establish that <code>foundValue</code>
     * is a member of the type represented by <code>expectedEntry</code> along
     * the path from <code>foundEntry</code> to <code>expectedEntry</code>. If
     * no such conditions exist (i.e., if the conditions would be
     * <code>false</code>), throws a <code>TypeMismatchException</code>.
     * </p>
     *
     * @param foundValue The value we'd like to establish is in the type
     *        represented by <code>expectedEntry</code>.
     * @param foundEntry A node in the type graph of which
     *        <code>foundValue</code> is a syntactic subtype.
     * @param expectedEntry A node in the type graph of which representing a
     *        type in which we would like to establish <code>foundValue</code>
     *        resides.
     * @param pathStrategy The strategy for following the path between
     *        <code>foundEntry</code> and <code>expectedEntry</code>.
     *
     * @return The conditions under which the path can be followed.
     *
     * @throws TypeMismatchException If the conditions under which the path can
     *         be followed would be <code>false</code>.
     */
    private <V> Exp getPathConditions(V foundValue, Map.Entry<MTType, Map<String, MTType>> foundEntry,
            Map.Entry<MTType, Map<String, MTType>> expectedEntry, NodePairPathStrategy<V> pathStrategy)
            throws TypeMismatchException {
        Map<String, MTType> combinedBindings = new HashMap<>();

        combinedBindings.clear();
        combinedBindings.putAll(updateMapLabels(foundEntry.getValue(), "_s"));
        combinedBindings
                .putAll(updateMapLabels(expectedEntry.getValue(), "_d"));

        Exp newCondition =
                pathStrategy.getValidTypeConditionsBetween(foundValue,
                        foundEntry.getKey(), expectedEntry.getKey(),
                        combinedBindings);

        return newCondition;
    }

    /**
     * <p>This method returns all the syntactic subtypes associated with {@code query}
     * as well as any type relationships that has been established.</p>
     *
     * @param query A mathematical type.
     *
     * @return A map containing subtypes and associated type relationships.
     */
    private Map<MTType, Map<String, MTType>> getSyntacticSubtypesWithRelationships(MTType query) {
        Map<MTType, Map<String, MTType>> result = new HashMap<>();

        Map<String, MTType> bindings;

        for (MTType potential : myTypeNodes.keySet()) {
            try {
                bindings = query.getSyntacticSubtypeBindings(potential);
                result.put(potential, new HashMap<>(bindings));
            }
            catch (NoSolutionException nse) {}
        }

        return result;
    }

    /**
     * <p>
     * Returns the conditions under which <code>value</code> could be
     * demonstrated to be a member of <code>expected</code>, given that
     * <code>value</code> is known to be in <strong>MType</strong>.
     * </p>
     *
     * <p>
     * The result is a series of disjuncts expressing possible situations under
     * which the <code>value</code> would be known to be in
     * <code>expected</code>. One or more of these disjuncts may be
     * <code>false</code>, but if one or more would have been <code>true</code>,
     * this method will simplify the result to simply <code>true</code>.
     * </p>
     *
     * <p>
     * If there is no known set of circumstances under which <code>value</code>
     * could be demonstrated a member of <code>expected</code> (i.e., if the
     * return value would simply be <code>false</code>), this method throws a
     * <code>TypeMismatchException</code>.
     * </p>
     *
     * @param value The <code>RESOLVE</code> value to test for membership.
     * @param expected A <code>RESOLVE</code> type against which to test
     *        membership.
     *
     * @return The conditions under which <code>value</code> could be
     *         demonstrated to be in <code>expected</code>.
     *
     * @throws TypeMismatchException If there are no known conditions under
     *         which <code>value</code> could be demonstrated to be in
     *         <code>expected</code>.
     */
    private Exp getValidTypeConditions(MTType value, MTType expected)
            throws TypeMismatchException {
        //See note in the getValidTypeConditionsTo() in TypeRelationship,
        //re: Lovecraftian nightmare-scape

        Exp result = MathExp.getFalseVarExp(null, this);

        if (expected == CLS) {
            //Every CLS is in CLS except for Entity and CLS, itself
            result = MathExp.getTrueVarExp(null, this);
        }
        else if (expected instanceof MTPowertypeApplication) {
            if (value.equals(EMPTY_SET)) {
                //The empty set is in all powertypes
                result = MathExp.getTrueVarExp(null, this);
            }
            else {
                //If "expected" happens to be Power(t) for some t, we can
                //"demote" value to an INSTANCE of itself (provided it is not
                //the empty set), and expected to just t
                MTPowertypeApplication expectedAsPowertypeApplication =
                        (MTPowertypeApplication) expected;

                DummyExp memberOfValue = new DummyExp(null, value);

                if (isKnownToBeIn(memberOfValue, expectedAsPowertypeApplication
                        .getArgument(0))) {
                    result = MathExp.getTrueVarExp(null, this);
                }
            }
        }

        //If we've already established it statically, no need for further work
        if (!MathExp.isLiteralTrue(result)) {
            //If we haven't...

            //At this stage, we've done everything safe and sensible that we can
            //do if the value we're looking at exists outside Entity
            if (value == CLS || value == ENTITY) {
                throw new TypeMismatchException(
                        "Unexpected mathematical type: " + value);
            }

            try {
                Exp intermediateResult =
                        getValidTypeConditions(value, value.getType(),
                                expected, MTTYPE_VALUE_PATH);

                if (MathExp.isLiteralTrue(intermediateResult)) {
                    result = intermediateResult;
                }
                else {
                    result =
                            MathExp.formDisjunct(result.getLocation(), result,
                                    intermediateResult);
                }
            }
            catch (TypeMismatchException tme) {
                if (MathExp.isLiteralFalse(result)) {
                    throw tme;
                }
            }
        }

        return result;
    }

    /**
     * <p>
     * Returns the conditions under which <code>value</code> could be
     * demonstrated to be a member of <code>expected</code>.
     * </p>
     *
     * <p>
     * The result is a series of disjuncts expressing possible situations under
     * which the <code>value</code> would be known to be in
     * <code>expected</code>. One or more of these disjuncts may be
     * <code>false</code>, but if one or more would have been <code>true</code>,
     * this method will simplify the result to simply <code>true</code>.
     * </p>
     *
     * <p>
     * If there is no known set of circumstances under which <code>value</code>
     * could be demonstrated a member of <code>expected</code> (i.e., if the
     * return value would simply be <code>false</code>), this method throws a
     * <code>TypeMismatchException</code>.
     * </p>
     *
     * @param value The <code>RESOLVE</code> value to test for membership.
     * @param expected A <code>RESOLVE</code> type against which to test
     *        membership.
     *
     * @return The conditions under which <code>value</code> could be
     *         demonstrated to be in <code>expected</code>.
     *
     * @throws TypeMismatchException If there are no known conditions under
     *         which <code>value</code> could be demonstrated to be in
     *         <code>expected</code>.
     */
    private Exp getValidTypeConditions(Exp value, MTType expected)
            throws TypeMismatchException {
        Exp result;

        MTType valueTypeValue = value.getMathTypeValue();
        if (expected == ENTITY && valueTypeValue != CLS
                && valueTypeValue != ENTITY) {
            //Every RESOLVE value is in Entity.  The only things we could get
            //passed that are "special" and not "RESOLVE values" are MType and
            //Entity itself
            result = MathExp.getTrueVarExp(null, this);
        }
        else if (valueTypeValue == CLS || valueTypeValue == ENTITY) {
            //MType and Entity aren't in anything
            throw new TypeMismatchException("Unexpected mathematical type: "
                    + value);
        }
        else if (valueTypeValue == null) {
            result =
                    getValidTypeConditions(value, value.getMathType(),
                            expected, EXP_VALUE_PATH);
        }
        else {
            //We're looking at an expression that defines a type
            result = getValidTypeConditions(valueTypeValue, expected);
        }

        return result;
    }

    /**
     * <p>
     * Returns the conditions under which <code>foundValue</code>, which is of
     * type <code>foundType</code>, could be demonstrated to be a member of
     * <code>expected</code>. Individual paths are tested using the given
     * <code>pathStrategy</code> (which lets us forget about what the java type
     * of <code>foundValue</code> is&mdash;only that it's a type
     * <code>pathStrategy</code> can handle.)
     * </p>
     *
     * <p>
     * The result is a series of disjuncts expressing possible situations under
     * which the <code>value</code> would be known to be in
     * <code>expected</code>. One or more of these disjuncts may be
     * <code>false</code>, but if one or more would have been <code>true</code>,
     * this method will simplify the result to simply <code>true</code>.
     * </p>
     *
     * <p>
     * If there is no known set of circumstances under which <code>value</code>
     * could be demonstrated a member of <code>expected</code> (i.e., if the
     * return value would simply be <code>false</code>), this method throws a
     * <code>TypeMismatchException</code>.
     * </p>
     *
     * @param foundValue The <code>RESOLVE</code> value to test for membership.
     * @param foundType The mathematical type of the <code>RESOLVE</code> value
     *        to test for membership.
     * @param expected A <code>RESOLVE</code> type against which to test
     *        membership.
     *
     * @return The conditions under which <code>value</code> could be
     *         demonstrated to be in <code>expected</code>.
     *
     * @throws TypeMismatchException If there are no known conditions under
     *         which <code>value</code> could be demonstrated to be in
     *         <code>expected</code>.
     */
    private <V> Exp getValidTypeConditions(V foundValue, MTType foundType,
            MTType expected, NodePairPathStrategy<V> pathStrategy)
            throws TypeMismatchException {
        if (foundType == null) {
            throw new IllegalArgumentException(foundValue + " has no type.");
        }

        Map<MTType, Map<String, MTType>> potentialFoundNodes =
                getSyntacticSubtypesWithRelationships(foundType);
        Map<MTType, Map<String, MTType>> potentialExpectedNodes =
                getSyntacticSubtypesWithRelationships(expected);

        Exp result = MathExp.getFalseVarExp(null, this);

        Exp newCondition;

        Iterator<Map.Entry<MTType, Map<String, MTType>>> expectedEntries;
        Iterator<Map.Entry<MTType, Map<String, MTType>>> foundEntries =
                potentialFoundNodes.entrySet().iterator();
        Map.Entry<MTType, Map<String, MTType>> foundEntry, expectedEntry;

        boolean foundPath = false;

        //If foundType equals expected, we're done
        boolean foundTrivialPath = foundType.equals(expected);

        while (!foundTrivialPath && foundEntries.hasNext()) {
            foundEntry = foundEntries.next();

            expectedEntries = potentialExpectedNodes.entrySet().iterator();

            while (!foundTrivialPath && expectedEntries.hasNext()) {

                expectedEntry = expectedEntries.next();

                try {
                    newCondition =
                            getPathConditions(foundValue, foundEntry,
                                    expectedEntry, pathStrategy);

                    foundPath =
                            foundPath | !MathExp.isLiteralFalse(newCondition);

                    foundTrivialPath = MathExp.isLiteralTrue(newCondition);

                    result =
                            MathExp.formDisjunct(newCondition.getLocation(),
                                    newCondition, result);
                }
                catch (TypeMismatchException e) {}
            }
        }

        if (foundTrivialPath) {
            result = MathExp.getTrueVarExp(null, this);
        }
        else if (!foundPath) {
            throw new TypeMismatchException("No path found!");
        }

        return result;
    }

    /**
     * <p>An helper method that updates entries in a map.</p>
     *
     * @param original The original map.
     * @param suffix The new suffix to be added to the map's key.
     * @param <T> The class associated with the map's values.
     *
     * @return The modified map.
     */
    private <T> Map<String, T> updateMapLabels(Map<String, T> original, String suffix) {
        Map<String, T> result = new HashMap<>();
        for (Map.Entry<String, T> entry : original.entrySet()) {
            result.put(entry.getKey() + suffix, entry.getValue());
        }

        return result;
    }

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