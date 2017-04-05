/*
 * MTUnion.java
 * ---------------------------------
 * Copyright (c) 2017
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
import java.util.*;

/**
 * <p>An union type that contains various other
 * {@link MTType}s.</p>
 *
 * @version 2.0
 */
public class MTUnion extends MTAbstract<MTUnion> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>An integer value that helps us retrieve the hashcode for this class.</p> */
    private final static int BASE_HASH = "MTUnion".hashCode();

    /** <p>List of {@link MTType}s that are in this union type. </p> */
    private List<MTType> myMembers = new LinkedList<>();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an union type with no members initially.</p>
     *
     * @param g The current type graph.
     */
    public MTUnion(TypeGraph g) {
        super(g);
    }

    /**
     * <p>This constructs an union type with the {@link MTType}s
     * as members of this type.</p>
     *
     * @param g The current type graph.
     * @param members The {@link MTType}s that are in this union type.
     */
    public MTUnion(TypeGraph g, MTType... members) {
        this(g, Arrays.asList(members));
    }

    /**
     * <p>This constructs an union type with the list of {@link MTType}s
     * as members of this type.</p>
     *
     * @param g The current type graph.
     * @param members The list of {@link MTType}s that are in this union type.
     */
    public MTUnion(TypeGraph g, List<MTType> members) {
        this(g);
        myMembers.addAll(members);
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

        for (MTType t : myMembers) {
            t.accept(v);
        }

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
        v.endMTUnion(this);
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
        v.beginMTUnion(this);
    }

    /**
     * <p>Add a new {@link MTType} to this union type.</p>
     *
     * @param t A {@link MTType} object.
     */
    public final void addMember(MTType t) {
        myMembers.add(t);
    }

    /**
     * <p>Checks to see if {@code member} is part of this union
     * type.</p>
     *
     * @param member A {@link MTType} object.
     *
     * @return {@code true} if {@code member} is in this union type,
     * {@code false} otherwise.
     */
    public final boolean containsMember(MTType member) {
        return myMembers.contains(member);
    }

    /**
     * <p>This method returns a list of {@link MTType}s
     * that are part of this type.</p>
     *
     * @return The list of {@link MTType}s in this union type.
     */
    @Override
    public final List<MTType> getComponentTypes() {
        return Collections.unmodifiableList(myMembers);
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
        Iterator<MTType> members = myMembers.iterator();
        while (members.hasNext()) {
            MTType member = members.next();
            if (!member.isKnownToContainOnlyMTypes()) {
                return false;
            }
        }
        return true;
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
        Iterator<MTType> members = myMembers.iterator();
        while (members.hasNext()) {
            MTType member = members.next();
            if (!member.membersKnownToContainOnlyMTypes()) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        Iterator<MTType> members = myMembers.iterator();
        while (members.hasNext()) {
            MTType member = members.next();
            if (sb.length() > 1) {
                sb.append(" union ");
            }
            sb.append(member.toString());
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * <p>This method attempts to replace a component type at the specified
     * index.</p>
     *
     * @param index Index to a component type.
     * @param newType The {@link MTType} to replace the one in our component list.
     *
     * @return A new {@link MTUnion} with the type at the specified index
     * replaced with {@code newType}.
     */
    @Override
    public final MTType withComponentReplaced(int index, MTType newType) {
        List<MTType> newMembers = new LinkedList<>(myMembers);
        newMembers.set(index, newType);

        return new MTUnion(getTypeGraph(), newMembers);
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
        int result = BASE_HASH;

        for (MTType t : myMembers) {
            result *= 61;
            result += t.hashCode();
        }

        return result;
    }

}