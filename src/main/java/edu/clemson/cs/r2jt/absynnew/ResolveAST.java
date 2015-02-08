/**
 * ResolveAST.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absynnew;

import org.antlr.v4.runtime.Token;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * <p>The root of the <tt>RESOLVE</tt> ast hierarchy.</p>
 */
public abstract class ResolveAST {

    private final Token myStart, myStop;

    public ResolveAST(Token start, Token stop) {
        myStart = start;
        myStop = stop;
    }

    public Token getStart() {
        return myStart;
    }

    public Token getStop() {
        return myStop;
    }

    /**
     * <p>Prints out this whole subtree, not just a particular node. Print just a
     * node if this is a leaf.</p>
     */
    @Override
    public String toString() {
        TextRenderingVisitor renderer = new TextRenderingVisitor();
        TreeWalker.walk(renderer, this);
        return renderer.getTemplates().get(this).render();
    }

    public List<ResolveAST> getChildren() {
        Deque<Class<?>> hierarchy = new LinkedList<Class<?>>();
        Class<?> curClass = this.getClass();
        do {
            hierarchy.push(curClass);
            curClass = curClass.getSuperclass();
        } while (curClass != ResolveAST.class);

        List<ResolveAST> children = new ArrayList<ResolveAST>();
        //get a list of all the declared and inherited members of that class
        ArrayList<Field> fields = new ArrayList<Field>();
        while (!hierarchy.isEmpty()) {

            curClass = hierarchy.pop();

            Field[] curFields = curClass.getDeclaredFields();
            for (int i = 0; i < curFields.length; ++i) {
                fields.add(curFields[i]);
            }
            curClass = curClass.getSuperclass();
        }

        for (Field fi : fields) {
            if (Modifier.isStatic(fi.getModifiers())) {
                continue;
            }
            fi.setAccessible(true);
            try {
                Object o = fi.get(this);
                if (o instanceof ResolveAST) { // single ast object
                    children.add((ResolveAST) o);
                }
                else if (o instanceof Collection || o instanceof ResolveAST[]) {
                    if (o instanceof ResolveAST[]) {
                        o = Arrays.asList((ResolveAST[]) o);
                    }
                    Collection<?> nestedElements = (Collection<?>) o;
                    for (Object nestedElement : nestedElements) {
                        if (nestedElement == null) {
                            continue;
                        }
                        children.add((ResolveAST) nestedElement);
                    }
                }
            }
            catch (IllegalAccessException iae) {
                throw new RuntimeException(iae);
            }
        }
        return children;
    }
}
