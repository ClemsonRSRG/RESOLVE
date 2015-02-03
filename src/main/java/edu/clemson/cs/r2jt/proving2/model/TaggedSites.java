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
package edu.clemson.cs.r2jt.proving2.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p><code>TaggedSites</code> provides a way of tagging sub-sites of a 
 * root <code>Site</code> with associated data of type <code>T</code>, as 
 * well as providing facilities to search tagged sub-expression via which 
 * sub-expressions they contain.</p>
 * 
 * @param <T> The type of the associated data. 
 */
public class TaggedSites<T> {

    private final Site myRoot;

    /**
     * <p>The set of all those <code>Site</code>s with associated data that 
     * are not contained inside another such <code>PExp</code>.  Note that, 
     * since <code>PExp</code>s nest, the elements in this set are therefore 
     * disjoint.</p>
     */
    private Set<Site> myTopLevelIdentifiedNodes = new HashSet<Site>();

    /**
     * <p>A mapping from each tagged <code>Site</code>s to the set of tagged
     * <code>Sites</code>s that represent subexpressions of it.</p>
     */
    private Map<Site, Set<Site>> myIdentifiedNodes =
            new HashMap<Site, Set<Site>>();

    /**
     * <p>A map each tagged <code>Site</code> to the data associated with it.
     * </p>
     */
    private Map<Site, T> myDataMap = new HashMap<Site, T>();

    public TaggedSites(Site root) {
        myRoot = root;
    }

    public Site getLargestIdentifiedAncestor(Site s) {
        return getContainer(s, myTopLevelIdentifiedNodes);
    }

    public List<Site> getIdentifiedAncestors(Site s) {
        List<Site> result = new LinkedList<Site>();

        try {
            getSmallestIdentifiedAncestorIn(s, myTopLevelIdentifiedNodes,
                    result);
        }
        catch (NoSuchElementException e) {
            //That's ok--if it has no ancestors, return an empty list
        }

        return result;
    }

    public Site getSmallestIdentifiedAncestor(Site s) {
        return getSmallestIdentifiedAncestorIn(s, myTopLevelIdentifiedNodes,
                null);
    }

    public void traverse(TaggedSiteVisitor<T> v) {
        traverseAndMap(myTopLevelIdentifiedNodes, v);
    }

    private void traverseAndMap(Set<Site> s, TaggedSiteVisitor<T> m) {

        for (Site i : s) {
            m.visitSite(i, myDataMap.get(i));
            traverseAndMap(myIdentifiedNodes.get(i), m);
        }
    }

    private Site getSmallestIdentifiedAncestorIn(Site s,
            Set<Site> possibleAncestors, List<Site> path) {

        Site result;

        try {
            Site containedIn = getContainer(s, possibleAncestors);

            try {
                if (path != null) {
                    path.add(containedIn);
                }

                result =
                        getSmallestIdentifiedAncestorIn(s, myIdentifiedNodes
                                .get(containedIn), path);
            }
            catch (NoSuchElementException e) {
                result = containedIn;
            }
        }
        catch (NoSuchElementException e) {
            throw new NoSuchElementException();
        }

        return result;
    }

    public void put(Site s, T data) {
        if (!s.root.equals(myRoot)) {
            throw new IllegalArgumentException("s' root is a Site "
                    + "different from the one that is the root of this "
                    + this.getClass() + ".");
        }

        if (myDataMap.containsKey(s)) {
            myDataMap.put(s, data);
        }
        else {
            Set<Site> nodeChildren = doTopLevelStuff(s, data);
            Set<Site> layer;
            try {
                Site container =
                        getSmallestIdentifiedAncestorIn(s,
                                myTopLevelIdentifiedNodes, null);

                layer = myIdentifiedNodes.get(container);
            }
            catch (NoSuchElementException e) {
                layer = myTopLevelIdentifiedNodes;
            }

            addToLayer(s, nodeChildren, layer);
        }
    }

    private void addToLayer(Site newSite, Set<Site> newSiteChildren,
            Set<Site> layer) {

        Iterator<Site> layerIter = layer.iterator();
        Site sibling;
        while (layerIter.hasNext()) {
            sibling = layerIter.next();
            if (sibling.inside(newSite)) {
                layerIter.remove();
                newSiteChildren.add(sibling);
            }
        }

        layer.add(newSite);
    }

    public T getData(Site s) {
        if (!myDataMap.containsKey(s)) {
            throw new NoSuchElementException();
        }

        return myDataMap.get(s);
    }

    private Set<Site> doTopLevelStuff(Site s, T data) {
        Set<Site> result = new HashSet<Site>();

        myIdentifiedNodes.put(s, result);
        myDataMap.put(s, data);

        return result;
    }

    private Site getContainer(Site s, Set<Site> setToSearch) {

        Site containedIn = null;

        Site child;
        Iterator<Site> children = setToSearch.iterator();
        while (containedIn == null && children.hasNext()) {
            child = children.next();

            if (s.inside(child)) {
                containedIn = child;
            }
        }

        if (containedIn == null) {
            throw new NoSuchElementException();
        }

        return containedIn;
    }
}