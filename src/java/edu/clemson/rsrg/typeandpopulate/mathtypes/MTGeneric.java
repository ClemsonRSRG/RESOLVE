/*
 * MTGeneric.java
 * ---------------------------------
 * Copyright (c) 2024
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
import edu.clemson.rsrg.typeandpopulate.typevisitor.TypeVisitor;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * A generic mathematical type.
 * </p>
 *
 * @version 2.0
 */
public class MTGeneric extends MTAbstract<MTGeneric> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * An integer value that helps us retrieve the hashcode for this class.
     * </p>
     */
    private static final int BASE_HASH = "MTGeneric".hashCode();

    /**
     * <p>
     * The generic type's name.
     * </p>
     */
    private final String myName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a generic type with some name.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param name
     *            The name for this type.
     */
    public MTGeneric(TypeGraph g, String name) {
        super(g);
        myName = name;
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
        v.endMTGeneric(this);
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
        v.beginMTGeneric(this);
    }

    /**
     * <p>
     * This method returns a list of {@link MTType}s that are part of this type.
     * </p>
     *
     * @return An empty list, because {@link MTGeneric} cannot contain component types.
     */
    @Override
    public final List<MTType> getComponentTypes() {
        return Collections.emptyList();
    }

    /**
     * <p>
     * This method returns the name for this {@link MTGeneric} type.
     * </p>
     *
     * @return Name as a string.
     */
    public final String getName() {
        return myName;
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
     * @return This method will always throw an {@link IndexOutOfBoundsException}, since {@link MTGeneric} cannot
     *         contain component types.
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
     * This is just a template method to <em>force</em> all concrete subclasses of {@link MTType} to implement
     * <code>hashCode()</code>, as the type resolution algorithm depends on it being implemented sensibly.
     * </p>
     *
     * @return A hashcode consistent with <code>equals()</code> and thus alpha-equivalency.
     */
    @Override
    protected final int getHashCode() {
        return BASE_HASH + myName.hashCode();
    }

}
