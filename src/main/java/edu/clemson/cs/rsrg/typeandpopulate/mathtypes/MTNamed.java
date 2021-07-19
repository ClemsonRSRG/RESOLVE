/*
 * MTNamed.java
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
package edu.clemson.cs.rsrg.typeandpopulate.mathtypes;

import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.typevisitor.TypeVisitor;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Represents a type that is simply a named reference to some bound variable.
 * For example, in
 * <code>BigUnion{t : MType}{t}</code>, the second "t" is a named type.
 * </p>
 *
 * @version 2.0
 */
public class MTNamed extends MTType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * An integer value that helps us retrieve the hashcode for this class.
     * </p>
     */
    private final static int BASE_HASH = "MTNamed".hashCode();

    /**
     * <p>
     * The named type's name.
     * </p>
     */
    private final String myName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a type that references some bound variable.
     * </p>
     *
     * @param g The current type graph.
     * @param name The name for this type.
     */
    public MTNamed(TypeGraph g, String name) {
        super(g);
        myName = name;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method is the {@code accept()} method in a visitor pattern for
     * invoking an instance of
     * {@link TypeVisitor}.
     * </p>
     *
     * @param v A visitor for types.
     */
    @Override
    public final void accept(TypeVisitor v) {
        acceptOpen(v);

        v.beginChildren(this);
        v.endChildren(this);

        acceptClose(v);
    }

    /**
     * <p>
     * This method implements the post-visit method for invoking an instance of
     * {@link TypeVisitor}.
     * </p>
     *
     * @param v A visitor for types.
     */
    @Override
    public final void acceptClose(TypeVisitor v) {
        v.endMTNamed(this);
        v.endMTType(this);
    }

    /**
     * <p>
     * This method implements the pre-visit method for invoking an instance of
     * {@link TypeVisitor}.
     * </p>
     *
     * @param v A visitor for types.
     */
    @Override
    public final void acceptOpen(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTNamed(this);
    }

    /**
     * <p>
     * This method returns a list of {@link MTType}s that are part of this type.
     * </p>
     *
     * @return An empty list, because {@link MTNamed} cannot contain component
     *         types.
     */
    @Override
    public final List<MTType> getComponentTypes() {
        return Collections.emptyList();
    }

    /**
     * <p>
     * This method returns the name for this {@link MTNamed} type.
     * </p>
     *
     * @return Name as a string.
     */
    public final String getName() {
        return myName;
    }

    /**
     * <p>
     * This method takes a map of original types and converts it to a map of
     * {@code MTNamed} types.
     * </p>
     *
     * @param source The current type graph.
     * @param original Original map of {@link MTType}s.
     *
     * @return A map of {@link MTNamed} types.
     */
    public static Map<MTNamed, MTType> toMTNamedMap(TypeGraph source,
            Map<String, MTType> original) {
        Map<MTNamed, MTType> result = new HashMap<>();

        for (Map.Entry<String, MTType> e : original.entrySet()) {
            result.put(new MTNamed(source, e.getKey()), e.getValue());
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
        return "'" + myName + "'";
    }

    /**
     * <p>
     * This method attempts to replace a component type at the specified index.
     * </p>
     *
     * @param index Index to a component type.
     * @param newType The {@link MTType} to replace the one in our component
     *        list.
     *
     * @return This method will always throw an
     *         {@link IndexOutOfBoundsException}, since
     *         {@link MTNamed} cannot contain component types.
     */
    @Override
    public final MTType withComponentReplaced(int index, MTType newType) {
        throw new IndexOutOfBoundsException();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * This is just a template method to <em>force</em> all concrete subclasses
     * of {@link MTType} to
     * implement <code>hashCode()</code>, as the type resolution algorithm
     * depends on it being
     * implemented sensibly.
     * </p>
     *
     * @return A hashcode consistent with <code>equals()</code> and thus
     *         alpha-equivalency.
     */
    @Override
    protected final int getHashCode() {
        return BASE_HASH;
    }

}
