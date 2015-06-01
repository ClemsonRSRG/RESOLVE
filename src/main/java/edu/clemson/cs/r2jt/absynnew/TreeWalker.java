/**
 * TreeWalker.java
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
package edu.clemson.cs.r2jt.absynnew;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * The {@code TreeWalker} is used to apply the visitor pattern to the
 * RESOLVE abstract syntax tree. The visitor logic is implemented as a
 * {@code TreeWalkerVisitor}.
 */
public class TreeWalker {

    private final TreeWalkerVisitor myVisitor;

    /**
     * Constructs a new {@code TreeWalker} that applies the logic of
     * {@code TreeWalkerVisitor} to a abstract syntax tree.
     *
     * @param listener	An instance of TreeWalkerListener which implements
     * 				    listener methods to be applied to nodes of the AST.
     */
    private TreeWalker(TreeWalkerVisitor listener) {
        myVisitor = listener;
    }

    public static void walk(TreeWalkerVisitor v, ResolveAST e) {
        new TreeWalker(v).visit(e);
    }

    /**
     * Visits the node {@code e} by calling pre listener
     * methods, recursively visiting child nodes, and calling appropriate
     * post methods.
     * <p>
     * If the {@code TreeWalkerVisitor} happens to encounter a method
     * named {@pre walk[className]}, that returns true, the children are
     * skipped.
     *
     * @param e	The RESOLVE ast node to walk
     */
    public void visit(ResolveAST e) {

        if (e != null && !walkOverride(e)) {
            invokeVisitorMethods("pre", e);

            List<ResolveAST> children = e.getChildren();

            if (children.size() > 0) {
                Iterator<ResolveAST> iter = children.iterator();

                ResolveAST prevChild = null, nextChild = null;
                while (iter.hasNext()) {
                    prevChild = nextChild;
                    nextChild = iter.next();
                    invokeVisitorMethods("mid", e, prevChild, nextChild);
                    visit(nextChild);
                }
                invokeVisitorMethods("mid", e, nextChild, null);
            }
            invokeVisitorMethods("post", e);
        }
    }

    private void invokeVisitorMethods(String prefix, ResolveAST... e) {
        boolean pre = prefix.equals("pre"), post = prefix.equals("post"), mid =
                prefix.equals("mid");

        // Invoke generic visitor methods (preAny, postAny)
        if (pre) {
            myVisitor.preAny(e[0]);
        }

        // Get the heirarchy of classes from which this node inherits
        // e.g., [ConceptModuleDec, ModuleDec, Dec, ResolveConceptualElement]
        Class<?> elementClass = e[0].getClass();
        ArrayList<Class<?>> classHierarchy = new ArrayList<Class<?>>();

        if (pre || post) {
            while (elementClass != ResolveAST.class) {
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

            className = currentClass.getSimpleName();
            methodName = prefix + className;

            ResolveAST[] parent = Arrays.copyOf(e, e.length);
            // Get parent and child types if this is a list node
            Class<?> paramType = ResolveAST.class;

            // Now try to obtain the proper visitor method
            try {
                Method visitorMethod;
                if (pre || post) { // pre and post methods
                    visitorMethod =
                            this.myVisitor.getClass().getMethod(methodName,
                                    currentClass);
                }
                else { // mid methods
                    visitorMethod =
                            this.myVisitor.getClass().getMethod(methodName,
                                    currentClass, paramType, paramType);
                }
                // Invoking the visitor method now!!!
                visitorMethod.invoke(this.myVisitor, (Object[]) parent);
            }
            catch (NoSuchMethodException nsme) {
                throw new RuntimeException(nsme);
            }
            catch (IllegalAccessException iae) {
                throw new RuntimeException(iae);
            }
            catch (InvocationTargetException ite) {
                Throwable iteCause = ite.getCause();

                if (iteCause instanceof RuntimeException) {
                    throw (RuntimeException) iteCause;
                }

                throw new RuntimeException(iteCause);
            }
        }

        if (post) {
            myVisitor.postAny(e[0]);
        }
    }

    private boolean walkOverride(ResolveAST e) {
        Class<?> elementClass = e.getClass();
        ArrayList<Class<?>> classHierarchy = new ArrayList<Class<?>>();
        while (elementClass != ResolveAST.class) {
            classHierarchy.add(0, elementClass);
            elementClass = elementClass.getSuperclass();
        }

        boolean foundOverride = false;
        Iterator<Class<?>> iter = classHierarchy.iterator();
        while (iter.hasNext() && !foundOverride) {
            Class<?> c = iter.next();

            String walkMethodName = "walk" + c.getSimpleName();
            try {
                Method walkMethod =
                        this.myVisitor.getClass().getMethod(walkMethodName, c);
                foundOverride =
                        ((Boolean) walkMethod.invoke(this.myVisitor, e));
            }
            catch (NoSuchMethodException nsme) {
                //Shouldn't be possible
                throw new RuntimeException(nsme);
            }
            catch (IllegalAccessException iae) {
                //Shouldn't be possible
                throw new RuntimeException(iae);
            }
            catch (InvocationTargetException ite) {
                //An exception was thrown inside the corresponding walk method
                Throwable iteCause = ite.getCause();

                if (iteCause instanceof RuntimeException) {
                    throw (RuntimeException) iteCause;
                }

                throw new RuntimeException(iteCause);
            }
        }
        return foundOverride;
    }
}