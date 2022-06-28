/*
 * VirtualListNode.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.absyn;

import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.rsrg.treewalk.TreeWalker;
import java.util.*;

/**
 * <p>
 * This is a virtual list node that allow us to store a list of children elements that exist inside the parent element.
 * </p>
 *
 * @author Blair Durkee
 * @author Yu-Shan Sun
 *
 * @version 2.0
 */
public class VirtualListNode extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * List of children elements
     * </p>
     */
    private final List<ResolveConceptualElement> myList;

    /**
     * <p>
     * Class type of the children elements
     * </p>
     */
    private final Class<?> myListType;

    /**
     * <p>
     * Name of the current element
     * </p>
     */
    private final String myName;

    /**
     * <p>
     * Parent element
     * </p>
     */
    private final ResolveConceptualElement myParent;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This object is used by the {@link TreeWalker} class to create virtual list nodes for all the children objects
     * inside the current traversing object.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param parent
     *            Parent element.
     * @param listName
     *            Name of the current element.
     * @param list
     *            List of children elements.
     * @param listType
     *            Class type of the children elements.
     */
    public VirtualListNode(Location l, ResolveConceptualElement parent, String listName,
            List<ResolveConceptualElement> list, Class<?> listType) {
        super(l);
        myParent = parent;
        myName = parent.getClass().getSimpleName() + toCamelCase(listName);
        myList = list;
        myListType = listType;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        throw new MiscErrorException("Not supported yet.", new UnsupportedOperationException());
    }

    /**
     * <p>
     * This method overrides the default clone method implementation for the {@link VirtualListNode} class.
     * </p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final VirtualListNode clone() {
        List<ResolveConceptualElement> listCopy = new ArrayList<>(myList.size());
        Collections.copy(listCopy, myList);

        return new VirtualListNode(myLoc, myParent.clone(), myName, listCopy, myListType);
    }

    /**
     * <p>
     * This method overrides the default equals method implementation for the {@link VirtualListNode} class.
     * </p>
     *
     * @param o
     *            Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        VirtualListNode that = (VirtualListNode) o;

        if (myList != null ? !myList.equals(that.myList) : that.myList != null)
            return false;
        if (myListType != null ? !myListType.equals(that.myListType) : that.myListType != null)
            return false;
        if (myName != null ? !myName.equals(that.myName) : that.myName != null)
            return false;

        return myParent != null ? myParent.equals(that.myParent) : that.myParent == null;
    }

    /**
     * <p>
     * This allow us to return all the children object in the class.
     * </p>
     *
     * @return A list containing all the children.
     */
    @Override
    public final List<ResolveConceptualElement> getChildren() {
        List<ResolveConceptualElement> children = new LinkedList<>();
        for (ResolveConceptualElement aMyList : myList) {
            children.add(ResolveConceptualElement.class.cast(aMyList));
        }

        return children;
    }

    /**
     * <p>
     * Returns of the location where this object originated from.
     * </p>
     *
     * @return A {@link Location} representation object.
     */
    @Override
    public final Location getLocation() {
        throw new MiscErrorException(this.getClass() + " has no location by definition.",
                new UnsupportedOperationException());
    }

    /**
     * <p>
     * Returns the type stored in this virtual list.
     * </p>
     *
     * @return The associated {@link Class} type.
     */
    public final Class<?> getListType() {
        return myListType;
    }

    /**
     * <p>
     * Returns the string representation of the {@link ResolveConceptualElement} that created this object.
     * </p>
     *
     * @return Name as a string.
     */
    public final String getNodeName() {
        return myName;
    }

    /**
     * <p>
     * Returns the string representation of the {@link ResolveConceptualElement} that created this object.
     * </p>
     *
     * @return Name as a string.
     */
    public final ResolveConceptualElement getParent() {
        return myParent;
    }

    /**
     * <p>
     * This method overrides the default hashCode method implementation for the {@link VirtualListNode} class.
     * </p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myList != null ? myList.hashCode() : 0;
        result = 31 * result + (myListType != null ? myListType.hashCode() : 0);
        result = 31 * result + (myName != null ? myName.hashCode() : 0);
        result = 31 * result + (myParent != null ? myParent.hashCode() : 0);

        return result;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * Applies proper camel casing to the string passed in.
     * </p>
     *
     * @param s
     *            Original string.
     *
     * @return Modified string.
     */
    private String toCamelCase(String s) {
        StringBuilder buffer = new StringBuilder();
        StringTokenizer tokens = new StringTokenizer(s, "_");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            buffer.append(Character.toUpperCase(token.charAt(0)));
            buffer.append(token.substring(1));
        }

        return buffer.toString();
    }

}
