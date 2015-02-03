/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.typeandpopulate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * TODO: This is a wreck.  Clean it up.
 */
public class MTCartesian extends MTAbstract<MTCartesian> {

    private static final int BASE_HASH = "MTCartesian".hashCode();

    private List<Element> myElements = new LinkedList<Element>();
    private List<MTType> myElementTypes = new LinkedList<MTType>();
    private Map<String, Element> myTagsToElementsTable =
            new HashMap<String, Element>();
    private Map<Element, String> myElementsToTagsTable =
            new HashMap<Element, String>();

    private final int mySize;

    public MTCartesian(TypeGraph g, Element... elements) {
        this(g, elements, elements.length);
    }

    public MTCartesian(TypeGraph g, List<Element> elements) {
        this(g, elements.toArray(new Element[0]), elements.size());
    }

    private MTCartesian(TypeGraph g, Element[] elements, int elementCount) {
        super(g);

        if (elementCount < 2) {
            //We assert this isn't possible, but who knows?
            throw new IllegalArgumentException(
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

    public int size() {
        return mySize;
    }

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
                //ASSERT: myElements.get(0) cannot be an instance of MTCartesian
                if (index != 0) {
                    throw new IndexOutOfBoundsException("" + index);
                }

                result = myElements.get(0);
            }
            else {
                //ASSERT: myElements.get(0) MUST be an instance of MTCartesian
                result =
                        ((MTCartesian) myElements.get(0).myElement)
                                .getElement(index);
            }
        }

        return result;
    }

    public String getTag(int index) {
        return getElement(index).myTag;
    }

    public MTType getFactor(int index) {
        return getElement(index).myElement;
    }

    public MTType getFactor(String tag) {
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

    @Override
    public int getHashCode() {
        int result = BASE_HASH;

        for (Element t : myElements) {
            result *= 37;
            result += t.myElement.hashCode();
        }

        return result;
    }

    @Override
    public String toString() {
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

    @Override
    public void acceptOpen(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTCartesian(this);
    }

    @Override
    public void accept(TypeVisitor v) {
        acceptOpen(v);

        v.beginChildren(this);

        for (Element t : myElements) {
            t.myElement.accept(v);
        }

        v.endChildren(this);

        acceptClose(v);
    }

    @Override
    public void acceptClose(TypeVisitor v) {
        v.endMTCartesian(this);
        v.endMTAbstract(this);
        v.endMTType(this);
    }

    @Override
    public List<MTType> getComponentTypes() {
        return myElementTypes;
    }

    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
        List<Element> newElements = new LinkedList<Element>(myElements);
        newElements.set(index, new Element(newElements.get(index).myTag,
                newType));

        return new MTCartesian(getTypeGraph(), newElements);
    }

    public static class Element {

        private String myTag;
        private MTType myElement;

        public Element(Element element) {
            this(element.myTag, element.myElement);
        }

        public Element(MTType element) {
            this(null, element);
        }

        public Element(String tag, MTType element) {
            if (element == null) {
                throw new IllegalArgumentException("Element \"" + tag + "\" "
                        + "has null type.");
            }

            myElement = element;
            myTag = tag;
        }

        @Override
        public String toString() {
            String result = myElement.toString();

            if (myTag != null) {
                result = "(" + myTag + " : " + result + ")";
            }

            return result;
        }

        private void addTo(List<Element> elements, List<MTType> elementTypes,
                Map<String, Element> tagsToElements,
                Map<Element, String> elementsToTags) {

            elements.add(this);
            elementTypes.add(myElement);

            if (myTag != null) {

                if (tagsToElements.containsKey(myTag)) {
                    throw new IllegalArgumentException("Duplicate tag: "
                            + myTag);
                }

                tagsToElements.put(myTag, this);
                elementsToTags.put(this, myTag);
            }
        }
    }
}