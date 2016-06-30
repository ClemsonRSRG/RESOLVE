/**
 * MTProper.java
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
package edu.clemson.cs.rsrg.typeandpopulate.mathtypes;

import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.typevisitor.TypeVisitor;
import java.util.Collections;
import java.util.List;

/**
 * <p>A proper type. Any type that does not depend on other types. I.e., it
 * is atomic.</p>
 *
 * @version 2.0
 */
public class MTProper extends MTType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The proper type's name.</p> */
    private final String myName;

    /** <p>The proper type's implementing type.</p> */
    private final MTType myType;

    /**
     * <p>A flag to indicate if this {@code MTProper} is allowed
     * to contain other types.</p>
     */
    private final boolean myKnownToContainOnlyMTypesFlag;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a proper type with no name that cannot contain
     * other types.</p>
     *
     * @param g The current type graph.
     */
    public MTProper(TypeGraph g) {
        this(g, null, false, null);
    }

    /**
     * <p>This constructs a proper type with no name, but could possibly
     * contain other types.</p>
     *
     * @param g The current type graph.
     */
    public MTProper(TypeGraph g, boolean knownToContainOnlyMTypes) {
        this(g, null, knownToContainOnlyMTypes, null);
    }

    /**
     * <p>This constructs a proper type that has a name that
     * cannot contain other types.</p>
     *
     * @param g The current type graph.
     * @param name The name for this type.
     */
    public MTProper(TypeGraph g, String name) {
        this(g, null, false, name);
    }

    /**
     * <p>This constructs a proper type with a name, a type and could possibly
     * contain other types.</p>
     *
     * @param g The current type graph.
     */
    public MTProper(TypeGraph g, MTType type, boolean knownToContainOnlyMTypes,
            String name) {
        super(g);
        myKnownToContainOnlyMTypesFlag = knownToContainOnlyMTypes;
        myType = type;
        myName = name;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method is the {@code accept()} method in a visitor pattern
     * for invoking an instance of {@link TypeVisitor}.</p>
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
     * <p>This method implements the post-visit method
     * for invoking an instance of {@link TypeVisitor}.</p>
     *
     * @param v A visitor for types.
     */
    @Override
    public final void acceptClose(TypeVisitor v) {
        v.endMTProper(this);
        v.endMTType(this);
    }

    /**
     * <p>This method implements the pre-visit method
     * for invoking an instance of {@link TypeVisitor}.</p>
     *
     * @param v A visitor for types.
     */
    @Override
    public final void acceptOpen(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTProper(this);
    }

    /**
     * <p>This method returns a list of {@link MTType}s
     * that are part of this type.</p>
     *
     * @return An empty list, because {@link MTProper} cannot
     * contain component types.
     */
    @Override
    public final List<MTType> getComponentTypes() {
        return Collections.emptyList();
    }

    /**
     * <p>This method returns the name for this {@link MTProper} type.</p>
     *
     * @return Name as a string.
     */
    public final String getName() {
        return myName;
    }

    /**
     * <p>This method returns the type for this {@link MTProper} type.</p>
     *
     * @return A {@link MTType} representing this object's type.
     */
    public final MTType getType() {
        return myType;
    }

    /**
     * <p>Indicates that this type is known to contain only elements <em>that
     * are themselves</em> types. Practically, this answers the question, "can
     * an instance of this type itself be used as a type?"</p>
     *
     * @return {@code true} if it can, {@code false} otherwise.
     */
    @Override
    public final boolean isKnownToContainOnlyMTypes() {
        return myKnownToContainOnlyMTypesFlag;
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        String result;

        if (myName == null) {
            result = super.toString();
        }
        else {
            result = myName;
        }

        return result;
    }

    /**
     * <p>This method attempts to replace a component type at the specified
     * index.</p>
     *
     * @param index Index to a component type.
     * @param newType The {@link MTType} to replace the one in our component list.
     *
     * @return This method will always throw an {@link IndexOutOfBoundsException},
     * since {@link MTProper} cannot contain component types.
     */
    @Override
    public final MTType withComponentReplaced(int index, MTType newType) {
        throw new IndexOutOfBoundsException();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>This is just a template method to <em>force</em> all concrete
     * subclasses of {@link MTType} to implement <code>hashCode()</code>,
     * as the type resolution algorithm depends on it being implemented
     * sensibly.</p>
     *
     * @return A hashcode consistent with <code>equals()</code> and thus
     * alpha-equivalency.
     */
    @Override
    protected final int getHashCode() {
        return objectReferenceHashCode();
    }

}