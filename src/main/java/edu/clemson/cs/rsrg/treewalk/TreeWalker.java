/*
 * TreeWalker.java
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
package edu.clemson.cs.rsrg.treewalk;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.VirtualListNode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * The {@code TreeWalker} is used to apply the visitor pattern to the RESOLVE
 * abstract syntax tree.
 * The visitor logic is implemented as a {@link TreeWalkerVisitor} or as a
 * {@link TreeWalkerStackVisitor}.
 * </p>
 *
 * @author Blair Durkee
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 2.0
 */
public class TreeWalker {

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Visits the node {@code e} by calling pre visitor methods, recursively
     * visiting child nodes, and
     * calling appropriate post methods.
     * </p>
     *
     * <p>
     * If the {@link TreeWalkerVisitor} happens to encounter a method named
     * {@code walk[className]},
     * that returns true, the children are skipped.
     * </p>
     *
     * @param visitor An instance of {@link TreeWalkerVisitor} which implements
     *        visit methods to be
     *        applied to nodes of the RESOLVE AST.
     * @param e The RESOLVE ast node to walk
     */
    public static void visit(TreeWalkerVisitor visitor,
            ResolveConceptualElement e) {
        if (e != null) {
            // are we overriding the walking for this element?
            if (!walkOverride(visitor, e)) {
                // invoke the "pre" visitor method(s)
                invokeVisitorMethods(visitor, "pre", e);

                List<ResolveConceptualElement> children = e.getChildren();
                if (children.size() > 0) {
                    Iterator<ResolveConceptualElement> iter =
                            children.iterator();

                    ResolveConceptualElement prevChild = null, nextChild = null;
                    while (iter.hasNext()) {
                        prevChild = nextChild;
                        nextChild = iter.next();
                        invokeVisitorMethods(visitor, "mid", e, prevChild,
                                nextChild);
                        visit(visitor, nextChild);
                    }
                    invokeVisitorMethods(visitor, "mid", e, nextChild, null);
                }
                // invoke the "post" visitor method(s)
                invokeVisitorMethods(visitor, "post", e);
            }
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * Invokes each of the visitor methods on the various different
     * {@link ResolveConceptualElement}s.
     * </p>
     *
     * @param visitor An instance of {@link TreeWalkerVisitor} which implements
     *        visit methods to be
     *        applied to nodes of the RESOLVE AST.
     * @param prefix Prefix string for the current walking method.
     * @param e The node to walk.
     */
    private static void invokeVisitorMethods(TreeWalkerVisitor visitor,
            String prefix, ResolveConceptualElement... e) {
        boolean pre = prefix.equals("pre"), post = prefix.equals("post"),
                mid = prefix.equals("mid"),
                list = (e[0] instanceof VirtualListNode);

        // Invoke generic visitor methods (preAny, postAny)
        if (pre) {
            visitor.preAny(e[0]);
        }

        // Get the heirarchy of classes from which this node inherits
        // e.g., [ConceptModuleDec, ModuleDec, Dec, ResolveConceptualElement]
        Class<?> elementClass = e[0].getClass();
        List<Class<?>> classHierarchy = new ArrayList<>();

        if (list) {
            classHierarchy.add(((VirtualListNode) e[0]).getParent().getClass());
        }
        else if (pre || post) {
            while (elementClass != ResolveConceptualElement.class) {
                if (post) {
                    classHierarchy.add(elementClass);
                }
                else {
                    classHierarchy.add(0, elementClass);
                }
                elementClass = elementClass.getSuperclass();
            }
        }
        else {
            classHierarchy.add(elementClass);
        }

        // Iterate over the class hierarchy
        Iterator<Class<?>> iter = classHierarchy.iterator();
        while (iter.hasNext()) {
            Class<?> currentClass = iter.next();

            // Construct name of method
            String className, methodName;
            if (!list) {
                className = currentClass.getSimpleName();
            }
            else {
                className = ((VirtualListNode) e[0]).getNodeName();
            }
            methodName = prefix + className;

            ResolveConceptualElement[] parent = Arrays.copyOf(e, e.length);
            // Get parent and child types if this is a list node
            Class<?> paramType = ResolveConceptualElement.class;
            if (list) {
                paramType = ((VirtualListNode) e[0]).getListType();
                parent[0] = ((VirtualListNode) e[0]).getParent();
            }

            // Now try to obtain the proper visitor method
            try {
                Method visitorMethod;
                if (pre || post) { // pre and post methods
                    visitorMethod = visitor.getClass().getMethod(methodName,
                            currentClass);
                }
                else { // mid methods
                    visitorMethod = visitor.getClass().getMethod(methodName,
                            currentClass, paramType, paramType);
                }

                // Invoking the visitor method now!!!
                visitorMethod.invoke(visitor, (Object[]) parent);
            }
            catch (NoSuchMethodException nsme) {
                // This is fine if we're dealing with a virtual node, otherwise
                // it shouldn't be possible
                if (!list) {
                    throw new RuntimeException("Cannot locate method", nsme);
                }
            }
            catch (IllegalAccessException iae) {
                throw new RuntimeException("Error accessing class: "
                        + currentClass.getSimpleName(), iae);
            }
            catch (InvocationTargetException ite) {
                // An exception was thrown inside the corresponding walk method
                Throwable throwable = ite.getTargetException();
                while (throwable instanceof RuntimeException
                        && throwable.getCause() != null) {
                    throwable = throwable.getCause();
                }

                throw new RuntimeException("Target invocation error for class: "
                        + currentClass.getSimpleName(), throwable);
            }
        }

        if (post) {
            visitor.postAny(e[0]);
        }
    }

    /**
     * <p>
     * Check to see if {@code e} has override the default walking mechanism.
     * </p>
     *
     * @param visitor An instance of {@link TreeWalkerVisitor} which implements
     *        visit methods to be
     *        applied to nodes of the RESOLVE AST.
     * @param e Current element that we are walking.
     *
     * @return {@code true} if override exists, {@code false} otherwise.
     */
    private static boolean walkOverride(TreeWalkerVisitor visitor,
            ResolveConceptualElement e) {
        Class<?> elementClass = e.getClass();
        List<Class<?>> classHierarchy = new ArrayList<>();
        while (elementClass != ResolveConceptualElement.class) {
            classHierarchy.add(0, elementClass);
            elementClass = elementClass.getSuperclass();
        }

        boolean foundOverride = false;
        Iterator<Class<?>> iter = classHierarchy.iterator();
        while (iter.hasNext() && !foundOverride) {
            Class<?> c = iter.next();

            if (!c.equals(VirtualListNode.class)) {
                String walkMethodName = "walk" + c.getSimpleName();
                try {
                    Method walkMethod =
                            visitor.getClass().getMethod(walkMethodName, c);
                    foundOverride = ((Boolean) walkMethod.invoke(visitor, e));
                }
                catch (NoSuchMethodException nsme) {
                    // Shouldn't be possible
                    throw new RuntimeException(
                            "Cannot locate method: " + walkMethodName, nsme);
                }
                catch (IllegalAccessException iae) {
                    // Shouldn't be possible
                    throw new RuntimeException(
                            "Error accessing class: " + c.getSimpleName(), iae);
                }
                catch (InvocationTargetException ite) {
                    // An exception was thrown inside the corresponding walk method
                    Throwable throwable = ite.getTargetException();
                    while (throwable instanceof RuntimeException
                            && throwable.getCause() != null) {
                        throwable = throwable.getCause();
                    }

                    throw new RuntimeException(
                            "Target invocation error for class: "
                                    + c.getSimpleName(),
                            throwable);
                }
            }
        }

        return foundOverride;
    }
}
