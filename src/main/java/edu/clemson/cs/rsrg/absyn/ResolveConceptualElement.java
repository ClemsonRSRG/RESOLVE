/**
 * ResolveConceptualElement.java
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

import edu.clemson.cs.rsrg.parsing.data.BasicCapabilities;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * <p>This is the abstract base class for all the intermediate objects
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public abstract class ResolveConceptualElement implements BasicCapabilities {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>Refers to the starting position of this {@code ResolveConceptualElement}
     * in the sourcefile.</p>
     *
     * <p>Note that this is <em>not</em> the starting position
     * of the name or anything like that -- but the actual start of the
     * construct itself.</p>
     */
    protected final Location myLoc;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>A helper constructor that allow us to store the location
     * of the created object directly in the this class.</p>
     *
     * @param l A {@link Location} representation object.
     */
    protected ResolveConceptualElement(Location l) {
        myLoc = l;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method must be implemented by all inherited classes
     * to create a special indented text version of the class as a string.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentSize The additional indentation increment
     *                        for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    public abstract String asString(int indentSize, int innerIndentSize);

    /**
     * <p>This method must be implemented by all inherited classes
     * to override the default clone method implementation.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public abstract ResolveConceptualElement clone();

    /**
     * <p>This method must be implemented by all inherited classes
     * to override the default equals method implementation.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public abstract boolean equals(Object o);

    /**
     * <p>This allow us to return all the children object in the class.</p>
     *
     * @return A list containing all the children.
     */
    public List<ResolveConceptualElement> getChildren() {
        //We'd like to hit the fields in the order they appear in the class,
        //starting with the most general class and getting more specific.  So,
        //we build a stack of the class hierarchy of this instance
        Deque<Class<?>> hierarchy = new LinkedList<>();
        Class<?> curClass = this.getClass();
        do {
            hierarchy.push(curClass);
            curClass = curClass.getSuperclass();
        } while (curClass != ResolveConceptualElement.class);

        List<ResolveConceptualElement> children =
                new ArrayList<>();
        // get a list of all the declared and inherited members of that class
        List<Field> fields = new ArrayList<>();
        while (!hierarchy.isEmpty()) {

            curClass = hierarchy.pop();

            Field[] curFields = curClass.getDeclaredFields();
            for (int i = 0; i < curFields.length; ++i) {
                fields.add(curFields[i]);
            }
            curClass = curClass.getSuperclass();
        }

        // loop through all the class members
        Iterator<Field> iterFields = fields.iterator();
        while (iterFields.hasNext()) {
            Field curField = iterFields.next();

            if (!Modifier.isStatic(curField.getModifiers())) {

                curField.setAccessible(true);
                Class<?> fieldType = curField.getType();

                try {
                    // is this member a ResolveConceptualElement?
                    // if so, add it as a child
                    if (ResolveConceptualElement.class
                            .isAssignableFrom(fieldType)) {
                        //System.out.println("Walking: " + curField.getName());
                        children.add(ResolveConceptualElement.class
                                .cast(curField.get(this)));
                    }
                    // is this member a list of ResolveConceptualElements?
                    // if so, add the elements to the list of children
                    else if (java.util.List.class.isAssignableFrom(fieldType)) {
                        Class<?> listOf =
                                (Class<?>) ((ParameterizedType) curField
                                        .getGenericType())
                                        .getActualTypeArguments()[0];
                        java.util.List<?> fieldList =
                                java.util.List.class.cast(curField.get(this));
                        if (fieldList != null
                                && fieldList.size() > 0
                                && ResolveConceptualElement.class
                                .isAssignableFrom(listOf)) {
                            children
                                    .add(new VirtualListNode(myLoc,
                                            this,
                                            curField.getName(),
                                            (java.util.List<ResolveConceptualElement>) fieldList,
                                            (Class<?>) ((ParameterizedType) curField
                                                    .getGenericType())
                                                    .getActualTypeArguments()[0]));
                        }
                    }
                }
                catch (Exception ex) {
                    if (ex instanceof RuntimeException) {
                        throw (RuntimeException) ex;
                    }
                    else {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }

        return children;
    }

    /**
     * <p>Return the location where this object
     * originated from.</p>
     *
     * @return A {@link Location} representation object.
     */
    public Location getLocation() {
        return myLoc;
    }

    /**
     * <p>This method must be implemented by all inherited classes
     * to return the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public abstract String toString();

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Builds a sequence of numSpaces spaces and returns that
     * sequence.</p>
     *
     * @param numSpaces The number of blank spaces desired.
     * @param buffer The string buffer we are currently building.
     */
    protected final void printSpace(int numSpaces, StringBuffer buffer) {
        for (int i = 0; i < numSpaces; ++i) {
            buffer.append(" ");
        }
    }

}