/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer. 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution. 
 *   * Neither the name of the Clemson University nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University.  Contributors to the initial version are:
 * 
 *     Steven Atkinson
 *     Greg Kulczycki
 *     Kunal Chopra
 *     John Hunt
 *     Heather Keown
 *     Ben Markle
 *     Kim Roche
 *     Murali Sitaraman
 */
/*
 * List.java 
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.collections;

import edu.clemson.cs.r2jt.data.Copyable;
import edu.clemson.cs.r2jt.data.AsStringCapability;

public class List<A> extends java.util.ArrayList<A>
    implements AsStringCapability, Copyable {

    // ===========================================================
    // Variables
    // ===========================================================

    private String s = "";

    // ===========================================================
    // Constructors
    // ===========================================================

    public List() {
        super();
        this.s = "Unspecified_Type";
    }

    public List(String s) {
        super();
        this.s = s;
    }
    
    public List(java.util.List<A> source) {
    	super(source);
    	this.s = "Unspecified_Type";
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Methods that Override
    // -----------------------------------------------------------

    public Iterator<A> iterator() {
        Iterator<A> iterator = new ListIterator<A>(super.listIterator());
        return iterator;
    }

    // -----------------------------------------------------------
    // Methods that Extend
    // -----------------------------------------------------------
    
    public boolean add(A a) {
    	if(a != null) { super.add(a); }
    	return true;
    }

    /** Adds an element to the list if it's not already there. */
    public void addUnique(A a) {
        if (!this.contains(a)) { this.add(a); }
    }

    /** Adds all elements to the list that are not already there. */
    public void addAllUnique(List<A> s) {
        Iterator<A> i = s.iterator();
        while (i.hasNext()) {
            this.addUnique(i.next());
        }
    }

    /**
     * Keeps the first occurance of an item in the list, and removes
     * all others, preserving the order.
     */
    public void removeDuplicates() {
        
        List<A> tmp = new List<A>();
        tmp.addAll(this);
        this.clear();
        Iterator<A> i = tmp.iterator();
        while (i.hasNext()) {
            A item = i.next();
            if (!this.contains(item)) { this.add(item); }
        }
    }

    /**
     * Prints the elements of the list according to the given
     * indentation and increment.
     */
    public String asString(int indent, int increment) {
        
        StringBuffer sb = new StringBuffer();

        sb.append(printSpace(indent));
        sb.append("List<"+s+">\n");

        Iterator<A> i = this.iterator();
        while (i.hasNext()) {
            A x = i.next();
            if (x == null) {
                sb.append(printSpace(indent+increment));
                sb.append("Null Element\n");
            } else if (x instanceof AsStringCapability) {
                sb.append(((AsStringCapability)x).asString(
                        indent+increment,increment));
            } else {
                sb.append(printSpace(indent+increment));
                sb.append("Does not implement AsStringCapability\n");
            } 
        }
        return sb.toString();
    }

    /**
     * Print the elements of the list.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("( ");
        Iterator<A> i = this.iterator();
        while (i.hasNext()) {
            A a = i.next();
            sb.append(a.toString());
            if (i.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(" )");
        return sb.toString();
    }

    /**
     * Returns a deep copy of the current list. If an element does
     * not implement the Copyable interface, the program will abort.
     */
    public List<A> copy() {
        List<A> result = new List<A>();
        Iterator<A> i = this.iterator();
        while (i.hasNext()) {
            A a = i.next();
            assert a instanceof Copyable : "a is not Copyable";
            result.add((A) ((Copyable) a).copy());
        }
        return result;
    }
    //JMH - this line was changed to be compatible with Java 1.4.2 with the
    //adding_generics-2_0.ea package
    //this may not be compatible with Java 1.5 generics when the become 
    //available
    //public void addAll(A[] array) {
    // avoid problems with 1.5 generics move the loop up a level - jmh
    /*
    public void addAll(A[] array) {
        for (int i = 0; i < array.length; i++) {
            this.add(array[i]);
        }
    }
    */

    // ===========================================================
    // Private Methods
    // ===========================================================

    private String printSpace(int n) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}

class ListIterator<A> implements Iterator<A> {

    // ===========================================================
    // Variables
    // ===========================================================

    private java.util.ListIterator<A> iterator;

    // ===========================================================
    // Constructors
    // ===========================================================

    ListIterator(java.util.ListIterator<A> iterator) {
        this.iterator = iterator;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public boolean hasPrevious() {
        return iterator.hasPrevious();
    }

    public A next() {
        return iterator.next();
    }

    public A previous() {
        return iterator.previous();
    }

    public int nextIndex() {
        return iterator.nextIndex();
    }

    public int previousIndex() {
        return iterator.previousIndex();
    }

    public void add(A a) {
        iterator.add(a);
    }

    public void set(A a) {
        iterator.set(a);
    }

    public void remove() {
        iterator.remove();
    }
}
