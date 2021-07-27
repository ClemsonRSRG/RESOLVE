/*
 * Map.java
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
     * Returns a deep copy of the current map. If an element does not implement
     * the Copyable
     * interface, the program will abort.
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
