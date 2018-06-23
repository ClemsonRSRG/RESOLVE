/*
 * MapOfLists.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.utilities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>A map from K to Lists of E.  The getList method will always return a 
 * list--even if no element has previously been added for that key. In this cas
 * it will return the empty list.  The putElement method will create lists as
 * necessary, even if no list already exists for the current key.</p>
 */
public class MapOfLists<K, E> {

    private final Map<K, List<E>> myBaseMap;

    public MapOfLists() {
        myBaseMap = new HashMap<K, List<E>>();
    }

    public void clear() {
        myBaseMap.clear();
    }

    public void putElement(K key, E element) {
        getList(key).add(element);
    }

    public List<E> getList(K key) {
        List<E> result = myBaseMap.get(key);

        if (result == null) {
            result = new LinkedList<E>();
            myBaseMap.put(key, result);
        }

        return result;
    }

}
