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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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
