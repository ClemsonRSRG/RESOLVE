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
package edu.clemson.cs.r2jt.proving.absyn;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.utilities.ImmutableIterator;

/**
 * <p><code>PExp</code>s are an inductive data structure and the same 
 * <code>PExp</code> instance may appear multiple times in a larger
 * <code>PExp</code> tree.  It becomes necessary, sometimes, to indicate a
 * particular <em>node</em> in such a tree, in which case a <code>PExp</code>
 * reference may not be unique.  A <code>NodeIdentifier</code> solves this 
 * problem by providing a mechanism for uniquely identifying a particular node 
 * of a <code>PExp</code> tree by indicating the root of the tree and then an
 * index path (0-indexed) of which child to follow at each level.</p>
 */
public class NodeIdentifier implements Iterable<Integer> {

    private List<Integer> myChildIndices = new LinkedList<Integer>();

    private int myDepth = 0;

    private int myHash = 0;

    private final PExp myPExp;

    public NodeIdentifier(PExp pexp) {
        myPExp = pexp;
        myHash = pexp.hashCode();
    }

    public NodeIdentifier(PExp pexp, Iterable<Integer> source) {
        myHash = pexp.hashCode();

        for (int i : source) {
            addLevel(i);
        }

        myPExp = pexp;
    }

    public NodeIdentifier(PExp pexp, int[] source) {
        myHash = pexp.hashCode();

        for (int i : source) {
            addLevel(i);
        }

        myPExp = pexp;
    }

    public NodeIdentifier(PExp pexp, int[] source, int offset, int length) {
        myHash = pexp.hashCode();

        for (int i = offset; i < offset + length; i++) {
            addLevel(source[i]);
        }

        myPExp = pexp;
    }

    public PExp getRoot() {
        return myPExp;
    }

    private void addLevel(int index) {
        myDepth++;
        myChildIndices.add(index);

        myHash *= 49;
        myHash += index;
    }

    public int getDepth() {
        return myDepth;
    }

    public Iterator<Integer> iterator() {
        return new ImmutableIterator<Integer>(myChildIndices.iterator());
    }

    public int hashCode() {
        return myHash;
    }

    public boolean inside(NodeIdentifier id) {
        boolean result = (id.getRoot() == myPExp);

        if (result) {
            Iterator<Integer> myIter = iterator();
            Iterator<Integer> oIter = id.iterator();

            boolean goodSoFar = true;
            while (myIter.hasNext() && oIter.hasNext() && goodSoFar) {
                goodSoFar = (myIter.next().equals(oIter.next()));
            }

            result = (goodSoFar && !oIter.hasNext());
        }

        return result;
    }

    public boolean equals(Object o) {
        boolean result =
                (o instanceof NodeIdentifier) && (myHash == o.hashCode());

        if (result) {
            NodeIdentifier oAsNID = (NodeIdentifier) o;

            result = ((oAsNID.getRoot()).equals(myPExp));

            if (result) {
                Iterator<Integer> myIter = iterator();
                Iterator<Integer> oIter = oAsNID.iterator();

                boolean goodSoFar = true;
                while (myIter.hasNext() && oIter.hasNext() && goodSoFar) {
                    goodSoFar = (myIter.next().equals(oIter.next()));
                }

                result = (goodSoFar && !myIter.hasNext() && !oIter.hasNext());
            }
        }

        return result;
    }

    public String toString() {
        return "" + myPExp + ":" + myChildIndices;
    }
}
