/*
 * MTBigUnion.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.mathtypes;

import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.typeandpopulate.typevisitor.SyntacticSubtypeChecker;
import edu.clemson.rsrg.typeandpopulate.typevisitor.TypeVisitor;
import java.util.*;

/**
 * <p>
 * A constructed type consisting of the union over one or more quantified types. For example
 * <code>U{t, r : MType}{t intersect r}</code> is the type of all intersections.
 * </p>
 *
 * @version 2.0
 */
public class MTBigUnion extends MTAbstract<MTBigUnion> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * An integer value that helps us retrieve the hashcode for this class.
     * </p>
     */
    private static final int BASE_HASH = "MTBigUnion".hashCode();

    /**
     * <p>
     * A map containing all the quantified variables in this type.
     * </p>
     */
    private TreeMap<String, MTType> myQuantifiedVariables;

    /**
     * If <code>myQuantifiedVariables</code> is <code>null</code>, then <code>myUniqueQuantifiedVariableCount</code> is
     * undefined.
     */
    private final int myUniqueQuantifiedVariableCount;

    /**
     * <p>
     * The expression that binds all the variables in this type.
     * </p>
     */
    private final MTType myExpression;

    /**
     * <p>
     * A mapping between indexes and quantified variables.
     * </p>
     */
    private final Map<Integer, String> myComponentIndexes = new HashMap<>();

    /**
     * <p>
     * List of {@link MTType}s that are in this big union type.
     * </p>
     */
    private List<MTType> myComponents;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a type that is the big union over a group of variables and is bound by some expression.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param quantifiedVariables
     *            The quantified variables for this type.
     * @param expression
     *            The {@link MTType} expression that binds all the variables in this type.
     */
    public MTBigUnion(TypeGraph g, Map<String, MTType> quantifiedVariables, MTType expression) {
        super(g);
        myQuantifiedVariables = new TreeMap<>(quantifiedVariables);
        myUniqueQuantifiedVariableCount = -1;
        myExpression = expression;
    }

    /**
     * <p>
     * This provides a small optimization for working with {@link SyntacticSubtypeChecker SyntacticSubtypeChecker}. In
     * the case where we're just going to have <em>n</em> variables whose names are meant to be guaranteed not to appear
     * in <code>expression</code>, we just pass in the number of variables this union is meant to be quantified over
     * rather than going through the trouble of giving them names and types and putting them in a map.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param uniqueVariableCount
     *            The number of unique variables.
     * @param expression
     *            The {@link MTType} expression that binds all the variables in this type.
     */
    public MTBigUnion(TypeGraph g, int uniqueVariableCount, MTType expression) {
        super(g);
        myQuantifiedVariables = null;
        myUniqueQuantifiedVariableCount = uniqueVariableCount;
        myExpression = expression;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method is the {@code accept()} method in a visitor pattern for invoking an instance of {@link TypeVisitor}.
     * </p>
     *
     * @param v
     *            A visitor for types.
     */
    @Override
    public final void accept(TypeVisitor v) {
        acceptOpen(v);

        v.beginChildren(this);

        if (myQuantifiedVariables == null) {
            for (int i = 0; i < myUniqueQuantifiedVariableCount; i++) {
                myTypeGraph.CLS.accept(v);
            }
        } else {
            for (MTType t : myQuantifiedVariables.values()) {
                t.accept(v);
            }
        }

        myExpression.accept(v);

        v.endChildren(this);

        acceptClose(v);
    }

    /**
     * <p>
     * This method implements the post-visit method for invoking an instance of {@link TypeVisitor}.
     * </p>
     *
     * @param v
     *            A visitor for types.
     */
    @Override
    public final void acceptClose(TypeVisitor v) {
        v.endMTBigUnion(this);
        v.endMTAbstract(this);
        v.endMTType(this);
    }

    /**
     * <p>
     * This method implements the pre-visit method for invoking an instance of {@link TypeVisitor}.
     * </p>
     *
     * @param v
     *            A visitor for types.
     */
    @Override
    public final void acceptOpen(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTBigUnion(this);
    }

    /**
     * <p>
     * This method returns a list of {@link MTType}s that are part of this type.
     * </p>
     *
     * @return The list of {@link MTType}s in this big union type.
     */
    @Override
    public final List<MTType> getComponentTypes() {
        if (myComponents == null) {
            if (myQuantifiedVariables == null) {
                myComponents = new ArrayList<>(myUniqueQuantifiedVariableCount);

                for (int i = 0; i < myUniqueQuantifiedVariableCount; i++) {
                    myComponents.add(myTypeGraph.CLS);
                }
            } else {
                List<MTType> components = new ArrayList<>(myQuantifiedVariables.size());
                for (Map.Entry<String, MTType> entry : myQuantifiedVariables.entrySet()) {

                    myComponentIndexes.put(components.size(), entry.getKey());
                    components.add(entry.getValue());
                }
                components.add(myExpression);
                myComponents = Collections.unmodifiableList(components);
            }
        }

        return myComponents;
    }

    /**
     * <p>
     * This method returns the expression used to bind this type.
     * </p>
     *
     * @return A {@link MTType} representing the binding expression.
     */
    public final MTType getExpression() {
        return myExpression;
    }

    /**
     * <p>
     * This method returns all the quantified variables and types.
     * </p>
     *
     * @return A map containing all the quantified variables and types.
     */
    public final Map<String, MTType> getQuantifiedVariables() {
        ensureQuantifiedTypes();

        return myQuantifiedVariables;
    }

    /**
     * <p>
     * This method returns the number of quantified variables in this type.
     * </p>
     *
     * @return An integer value.
     */
    public final int getQuantifiedVariablesSize() {
        int result;

        if (myQuantifiedVariables == null) {
            result = myUniqueQuantifiedVariableCount;
        } else {
            result = myQuantifiedVariables.size();
        }

        return result;
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
        ensureQuantifiedTypes();

        return "BigUnion" + myQuantifiedVariables + "{" + myExpression + "}";
    }

    /**
     * <p>
     * This method attempts to replace a component type at the specified index.
     * </p>
     *
     * @param index
     *            Index to a component type.
     * @param newType
     *            The {@link MTType} to replace the one in our component list.
     *
     * @return A new {@link MTBigUnion} with the type at the specified index replaced with {@code newType}.
     */
    @Override
    public final MTType withComponentReplaced(int index, MTType newType) {
        ensureQuantifiedTypes();

        Map<String, MTType> newQuantifiedVariables;
        MTType newExpression;

        if (index < myQuantifiedVariables.size()) {
            newQuantifiedVariables = new HashMap<>(myQuantifiedVariables);

            newQuantifiedVariables.put(myComponentIndexes.get(index), newType);

            newExpression = myExpression;
        } else if (index == myQuantifiedVariables.size()) {
            newQuantifiedVariables = myQuantifiedVariables;

            newExpression = newType;
        } else {
            throw new IndexOutOfBoundsException();
        }

        return new MTBigUnion(getTypeGraph(), newQuantifiedVariables, newExpression);
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * This is just a template method to <em>force</em> all concrete subclasses of {@link MTType} to implement
     * <code>hashCode()</code>, as the type resolution algorithm depends on it being implemented sensibly.
     * </p>
     *
     * @return A hashcode consistent with <code>equals()</code> and thus alpha-equivalency.
     */
    @Override
    protected final int getHashCode() {
        ensureQuantifiedTypes();

        int result = BASE_HASH;

        // Note that order of these MTTypes doesn't matter
        for (MTType t : myQuantifiedVariables.values()) {
            result += t.hashCode();
        }

        result *= 57;
        result += myExpression.hashCode();

        return result;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * Converts us from a "enh, some number of unique variables" big union to a "specific named unique variables" big
     * union if one of the methods is called that requires such a thing.
     * </p>
     */
    private void ensureQuantifiedTypes() {
        if (myQuantifiedVariables == null) {
            myQuantifiedVariables = new TreeMap<>();

            for (int i = 0; i < myUniqueQuantifiedVariableCount; i++) {
                myQuantifiedVariables.put("*" + i, myTypeGraph.CLS);
            }
        }
    }

}
