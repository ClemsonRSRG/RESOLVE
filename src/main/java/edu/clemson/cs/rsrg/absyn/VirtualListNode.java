/**
 * VirtualListNode.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn;

import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.*;

/**
 * <p>This is a virtual list node that allow us to store a list of children
 * elements that exist inside the parent element.</p>
 *
 * @author Blair Durkee
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class VirtualListNode extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>List of children elements</p> */
    private final List<ResolveConceptualElement> myList;

    /** <p>Class type of the children elements</p> */
    private final Class<?> myListType;

    /** <p>Name of the current element</p> */
    private final String myName;

    /** <p>Parent element</p> */
    private final ResolveConceptualElement myParent;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This object is used by the {link Treewalker} class to create
     * virtual list nodes for all the children objects inside the current
     * traversing object.</p>
     *
     * @param parent Parent element.
     * @param listName Name of the current element.
     * @param list List of children elements.
     * @param listType Class type of the children elements.
     */
    public VirtualListNode(ResolveConceptualElement parent, String listName,
            List<ResolveConceptualElement> list, Class<?> listType) {
        myParent = parent;
        myName = parent.getClass().getSimpleName() + toCamelCase(listName);
        myList = list;
        myListType = listType;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method creates a special indented
     * text version of the class as a string.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentSize The additional indentation increment
     *                        for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public String asString(int indentSize, int innerIndentSize) {
        throw new MiscErrorException("Not supported yet.",
                new UnsupportedOperationException());
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {link VirtualListNode} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public VirtualListNode clone() {
        List<ResolveConceptualElement> listCopy = new ArrayList<>(myList.size());
        Collections.copy(listCopy, myList);
        return new VirtualListNode(myParent.clone(), myName, listCopy, myListType);
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {link VirtualListNode} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        throw new MiscErrorException("Not supported yet.",
                new UnsupportedOperationException());
    }

    /**
     * <p>This allow us to return all the children object in the class.</p>
     *
     * @return A list containing all the children.
     */
    @Override
    public List<ResolveConceptualElement> getChildren() {
        List<ResolveConceptualElement> children =
                new LinkedList<>();
        Iterator<ResolveConceptualElement> iter = myList.iterator();
        while (iter.hasNext()) {
            children.add(ResolveConceptualElement.class.cast(iter.next()));
        }

        return children;
    }

    /**
     * <p>Returns of the location where this object
     * originated from.</p>
     *
     * @return A {link Location} representation object.
     */
    @Override
    public Location getLocation() {
        throw new MiscErrorException(this.getClass()
                + " has no location by definition.",
                new UnsupportedOperationException());
    }

    public Class<?> getListType() {
        return myListType;
    }

    /**
     * <p>Returns the string representation of the {link ResolveConceptualElement}
     * that created this object.</p>
     *
     * @return Name as a string.
     */
    public String getNodeName() {
        return myName;
    }

    /**
     * <p>Returns the string representation of the {link ResolveConceptualElement}
     * that created this object.</p>
     *
     * @return Name as a string.
     */
    public ResolveConceptualElement getParent() {
        return myParent;
    }

    /**
     * <p>Returns the node in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public String toString() {
        throw new MiscErrorException("Not supported yet.",
                new UnsupportedOperationException());
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Applies proper camel casing to the string passed in.</p>
     *
     * @param s Original string.
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