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

import edu.clemson.cs.r2jt.data.Copyable;

public class Map<A, B> extends java.util.HashMap<A, B> implements Copyable {

    // ===========================================================
    // Constructors
    // ===========================================================

    public Map() {
        super();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * Returns an iterator of the current key set.
     */
    public Iterator<A> keyIterator() {
        List<A> s = new List<A>();
        s.addAll(this.keySet());
        return s.iterator();
    }

    /**
     * Returns a deep copy of the current map. If an element does
     * not implement the Copyable interface, the program will abort.
     */
    public Map<A, B> copy() {
        Map<A, B> result = new Map<A, B>();
        Iterator<A> i = this.keyIterator();
        while (i.hasNext()) {
            A a = i.next();
            B b = get(a);

            assert b instanceof Copyable : "b is not an instance of Copyable";
            B b2 = put(a, (B) ((Copyable) b).copy());
            assert b2 == null : "map already contained a value";
        }
        return result;
    }

    /**
     * Prints a representation of the current map.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator<A> i = this.keyIterator();
        while (i.hasNext()) {
            A a = i.next();
            B b = get(a);
            sb.append("[ ");
            sb.append(a.toString());
            sb.append(" |-> ");
            sb.append(b.toString());
            sb.append(" ]\n");
        }
        return sb.toString();
    }
}
