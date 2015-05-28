/**
 * TaggedSites.java
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
package edu.clemson.cs.r2jt.rewriteprover.model;

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