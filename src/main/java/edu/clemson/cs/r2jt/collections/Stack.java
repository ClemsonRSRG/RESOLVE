/**
 * Stack.java
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
package edu.clemson.cs.r2jt.collections;

public class Stack<A> {

    // ===========================================================
    // Variables
    // ===========================================================

    List<A> s = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public Stack() {
        s = new List<A>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * Returns an iterator that goes through the stack from
     * bottom to top.
     */
    public Iterator<A> iterator() {
        return s.iterator();
    }

    /**
     * Remove all elements from the stack.
     */
    public void clear() {
        s.clear();
    }

    /**
     * Returns true if the stack is empty, false otherwise.
     */
    public boolean isEmpty() {
        return s.isEmpty();
    }

    /**
     * Pushes the specified element onto the stack.
     */
    public void push(A a) {
        s.add(a);
    }

    /**
     * Pops an element from the stack.
     */
    public A pop() {
        return s.remove(s.size() - 1);
    }

    /**
     * Returns a handle to the element at the top of the stack.
     */
    public A getTop() {
        if (s.size() == 0) {
            return null;
        }
        else {
            return s.get(s.size() - 1);
        }
    }

    /**
     * Returns the size of the stack.
     */
    public int size() {
        return s.size();
    }

    /**
     * Prints the elements of the list.
     */
    public String toString() {
        return s.toString();
    }
}
