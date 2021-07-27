/*
 * MTCartesian.java
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

import edu.clemson.cs.rsrg.typeandpopulate.exception.TypeMismatchException;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.typevisitor.TypeVisitor;
import java.util.*;

/**
 * <p>
 * A constructed type that is a Cartesian product over one or more elements.
 * </p>
 *
 * @version 2.0
 */
public class MTCartesian extends MTAbstract<MTCartesian> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * An integer value that helps us retrieve the hashcode for this class.
     * </p>
     */
    private static final int BASE_HASH = "MTCartesian".hashCode();

    /**
     * <p>
     * List of {@link Element}s that are in this type.
     * </p>
     */
    private List<Element> myElements = new LinkedList<>();

    /**
     * <p>
     * List of {@link MTType}s corresponding to each element.
     * </p>
     */
    private List<MTType> myElementTypes = new LinkedList<>();

    /**
     * <p>
     * A mapping of {@link Element}s in this type.
     * </p>
     */
    private Map<String, Element> myTagsToElementsTable = new HashMap<>();

    /**
     * <p>
     * A reverse mapping of {@link Element}s in this type.
     * </p>
     */
    private Map<Element, String> myElementsToTagsTable = new HashMap<>();

    /**
     * <p>
     * The number of elements in this type.
     * </p>
     */
    private final int mySize;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a Cartesian type with {@link Element}s that will be in
     * this type.
     * </p>
     *
     * @param g The current type graph.
     * @param elements The {@link Element}s that are in this Cartesian type.
     */
    public MTCartesian(TypeGraph g, Element... elements) {
        this(g, elements, elements.length);
    }

    /**
     * <p>
     * This constructs a Cartesian type with a list of {@link Element}s that
     * will be in this type.
     * </p>
     *
     * @param g The current type graph.
     * @param elements A list of {@link Element}s that are in this Cartesian
     *        type.
     */
    public MTCartesian(TypeGraph g, List<Element> elements) {
        this(g, elements.toArray(new Element[0]), elements.size());
    }

    /**
     * <p>
     * This constructs a Cartesian type with an array of {@link Element}s of
     * this type with an element
     * counter.
     * </p>
     *
     * @param g The current type graph
     * @param elements An array of {@link Element}s that are in this Cartesian
     *        type.
     * @param elementCount The number of elements in this type.
     */
    private MTCartesian(TypeGraph g, Element[] elements, int elementCount) {
        super(g);

        if (elementCount < 2) {
            // - YS: Should have been caught while building the AST
            // We assert this isn't possible, but who knows?
            throw new TypeMismatchException(
                    "Unexpected cartesian product size.");
        }

        int workingSize = 0;

        Element first;
        if (elementCount == 2) {
            first = new Element(elements[0]);
        }
        else {
            first = new Element(new MTCartesian(g, elements, elementCount - 1));
        }

        if (first.myElement instanceof MTCartesian) {
            workingSize += ((MTCartesian) first.myElement).size();
        }
        else {
            workingSize += 1;
        }

        Element second = new Element(elements[elementCount - 1]);
        workingSize += 1;

        first.addTo(myElements, myElementTypes, myTagsToElementsTable,
                myElementsToTagsTable);
        second.addTo(myElements, myElementTypes, myTagsToElementsTable,
                myElementsToTagsTable);
        mySize = workingSize;
        myElementTypes = Collections.unmodifiableList(myElementTypes);
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

        for (Element t : myElements) {
            t.myElement.accept(v);
        }

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
        v.endMTCartesian(this);
        v.endMTAbstract(this);
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
        v.beginMTAbstract(this);
        v.beginMTCartesian(this);
    }

    /**
     * <p>
     * This method returns a list of {@link MTType}s that are part of this type.
     * </p>
     *
     * @return The list of {@link MTType}s in this Cartesian type.
     */
    @Override
    public final List<MTType> getComponentTypes() {
        return myElementTypes;
    }

    /**
     * <p>
     * This method returns the {@link MTType} associated with the index.
     * </p>
     *
     * @param index An index.
     *
     * @return The type corresponding to the {@link Element} specified by the
     *         index.
     */
    public final MTType getFactor(int index) {
        return getElement(index).myElement;
    }

    /**
     * <p>
     * This method returns the {@link MTType} associated with the specified
     * string.
     * </p>
     *
     * @param tag A string corresponding to one of the {@link Element}s.
     *
     * @return The type corresponding to the tag.
     */
    public final MTType getFactor(String tag) {
        MTType result;

        if (myElements.get(0).myTag != null
                && myElements.get(0).myTag.equals(tag)) {
            result = myElements.get(0).myElement;
        }
        else if (myElements.get(1).myTag != null
                && myElements.get(1).myTag.equals(tag)) {
            result = myElements.get(1).myElement;
        }
        else if (myElements.get(0).myElement instanceof MTCartesian) {
            result = ((MTCartesian) myElements.get(0).myElement).getFactor(tag);
        }
        else {
            throw new NoSuchElementException();
        }

        return result;
    }

    /**
     * <p>
     * This method returns a formatted string for the elements in this type.
     * </p>
     *
     * @return String representation for the parameters.
     */
    public final String getParamString() {
        String rString = "";
        for (MTType m : myElementTypes) {
            if (m.getClass().getSimpleName().equals("MTCartesian")) {
                MTCartesian mc = (MTCartesian) m;
                rString += mc.getParamString() + " ";
            }
            else
                rString += m.toString() + " ";
        }
        return rString;
    }

    /**
     * <p>
     * This method returns the symbol representation of the {@link Element} at
     * the specified index.
     * </p>
     *
     * @param index An index.
     *
     * @return The string tag for the {@link Element}.
     */
    public final String getTag(int index) {
        return getElement(index).myTag;
    }

    /**
     * <p>
     * This method returns the size of the Cartesian product type.
     * </p>
     *
     * @return An integer.
     */
    public final int size() {
        return mySize;
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
        StringBuffer str = new StringBuffer("(");
        Iterator<Element> types = myElements.iterator();
        while (types.hasNext()) {
            str.append(types.next().toString());
            if (types.hasNext()) {
                str.append(" * ");
            }
        }
        str.append(")");
        return str.toString();
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
     * @return A new {@link MTCartesian} with the type at the specified index
     *         replaced with
     *         {@code newType}.
     */
    @Override
    public final MTType withComponentReplaced(int index, MTType newType) {
        List<Element> newElements = new LinkedList<>(myElements);
        newElements.set(index,
                new Element(newElements.get(index).myTag, newType));

        return new MTCartesian(getTypeGraph(), newElements);
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
        int result = BASE_HASH;

        for (Element t : myElements) {
            result *= 37;
            result += t.myElement.hashCode();
        }

        return result;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This retrieves the {@link Element} located at the specified index.
     * </p>
     *
     * @param index An index.
     *
     * @return An {@link Element} representation object.
     */
    private Element getElement(int index) {
        Element result;

        if (index < 0 || index >= mySize) {
            throw new IndexOutOfBoundsException("" + index);
        }

        if (index == (mySize - 1)) {
            result = myElements.get(1);
        }
        else {
            if (mySize == 2) {
                // ASSERT: myElements.get(0) cannot be an instance of MTCartesian
                if (index != 0) {
                    throw new IndexOutOfBoundsException("" + index);
                }

                result = myElements.get(0);
            }
            else {
                // ASSERT: myElements.get(0) MUST be an instance of MTCartesian
                result = ((MTCartesian) myElements.get(0).myElement)
                        .getElement(index);
            }
        }

        return result;
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>
     * This helper class allow us to store a name for an element in the
     * Cartesian type and the type
     * for the element.
     * </p>
     *
     * @version 2.0
     */
    public static class Element {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * </p>
         */
        private final String myTag;

        /**
         * <p>
         * </p>
         */
        private final MTType myElement;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * This constructs a new element from an existing {@link Element}.
         * </p>
         *
         * @param element An existing {@link Element}.
         */
        public Element(Element element) {
            this(element.myTag, element.myElement);
        }

        /**
         * <p>
         * This constructs a new element using the element's type.
         * </p>
         *
         * @param element The type for the elements.
         */
        public Element(MTType element) {
            this(null, element);
        }

        /**
         * <p>
         * This constructs a new element from the tag and the type.
         * </p>
         *
         * @param tag A name for the element.
         * @param element The type for the element.
         */
        public Element(String tag, MTType element) {
            if (element == null) {
                throw new IllegalArgumentException(
                        "Element \"" + tag + "\" " + "has null type.");
            }

            myElement = element;
            myTag = tag;
        }

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>
         * This method returns the object in string format.
         * </p>
         *
         * @return Object as a string.
         */
        @Override
        public final String toString() {
            String result = myElement.toString();

            if (myTag != null) {
                result = "(" + myTag + " : " + result + ")";
            }

            return result;
        }

        // ===========================================================
        // Private Methods
        // ===========================================================

        /**
         * <p>
         * An helper method to update the maps.
         * </p>
         *
         * @param elements Original list of {@link Element}s.
         * @param elementTypes Original list of {@link MTType}s.
         * @param tagsToElements A map contains a mapping of strings to
         *        {@link Element}s that needs to
         *        be updated.
         * @param elementsToTags A map contains a mapping of strings to
         *        {@link MTType}s that needs to be
         *        updated.
         */
        private void addTo(List<Element> elements, List<MTType> elementTypes,
                Map<String, Element> tagsToElements,
                Map<Element, String> elementsToTags) {
            elements.add(this);
            elementTypes.add(myElement);

            if (myTag != null) {

                if (tagsToElements.containsKey(myTag)) {
                    throw new IllegalArgumentException(
                            "Duplicate tag: " + myTag);
                }

                tagsToElements.put(myTag, this);
                elementsToTags.put(this, myTag);
            }
        }
    }
}
