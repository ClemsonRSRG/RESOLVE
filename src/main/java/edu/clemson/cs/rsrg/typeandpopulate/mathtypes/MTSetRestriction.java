/**
 * MTSetRestriction.java
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

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.typevisitor.TypeVisitor;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * <p>An set restriction type that is restricted to an
 * {@link Exp}.</p>
 *
 * @version 2.0
 */
public class MTSetRestriction extends MTAbstract<MTSetRestriction> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>A base type to be restricted.</p> */
    private final MTType myBaseType;

    /** <p>The variable representing an element of the set.</p> */
    private final String mySetVar;

    /** <p>The restriction condition expression.</p> */
    private final Exp myRestriction;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a set restriction type for a base type,
     * a variable that represents an element of the set and the
     * restriction condition expression.</p>
     *
     * @param g The current type graph.
     * @param baseType The {@link MTType} to be restricted.
     * @param setVar A string that represents an element in the set.
     * @param restriction The restriction condition expression.
     */
    public MTSetRestriction(TypeGraph g, MTType baseType, String setVar,
            Exp restriction) {
        super(g);
        myBaseType = baseType;
        mySetVar = setVar;
        myRestriction = restriction;
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
        myBaseType.accept(v);
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
        v.endMTSetRestriction(this);
        v.endMTAbstract(this);
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
        v.beginMTAbstract(this);
        v.beginMTSetRestriction(this);
    }

    /**
     * <p>This method returns a list of containing the
     * base {@link MTType} for this type.</p>
     *
     * @return A singleton list with the base {@link MTType}.
     */
    @Override
    public final List<MTType> getComponentTypes() {
        return Collections.unmodifiableList(Collections
                .singletonList(myBaseType));
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
        return myBaseType.isKnownToContainOnlyMTypes();
    }

    /**
     * <p>Indicates that every instance of this type is itself known to contain
     * only elements that are types. Practically, this answers the question,
     * "if a function returns an instance of this type, can that instance itself
     * be said to contain only types?"</p>
     *
     * @return {@code true} if it can, {@code false} otherwise.
     */
    @Override
    public final boolean membersKnownToContainOnlyMTypes() {
        return myBaseType.membersKnownToContainOnlyMTypes();
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        return "{" + mySetVar + " : " + myBaseType.toString() + " | "
                + myRestriction.toString() + "}";
    }

    /**
     * <p>This method attempts to replace a component type at the specified
     * index.</p>
     *
     * @param index Index to a component type.
     * @param newType The {@link MTType} to replace the one in our component list.
     *
     * @return A new {@link MTSetRestriction} with the type at the specified index
     * replaced with {@code newType}.
     */
    @Override
    public final MTType withComponentReplaced(int index, MTType newType) {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }

        return new MTSetRestriction(getTypeGraph(), newType, mySetVar,
                myRestriction);
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
        //This is fun.  At the moment MTSetRestrictions are not alpha-equivalent
        //to anything, including themselves, so the best thing we can do is
        //provide an integer that is maximally unlikely to be equal to any
        //object's (including this one's!) hash.
        return (new Random()).nextInt();
    }

}