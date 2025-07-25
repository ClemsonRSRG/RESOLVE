/*
 * RCollections.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * A set of utility functions for java collections.
 * </p>
 *
 * @version 2.0
 */
public class RCollections {

    /**
     * <p>
     * Returns a new list that is the result of processing each element of the original list with the given mapping, in
     * order, and adding it to the final list.
     * </p>
     * <p>
     * This method will return an <code>ArrayList</code> if given one, and a <code>LinkedList</code> if given one, but
     * otherwise the specific subclass of list returned is undefined.
     * </p>
     *
     * @param <T>
     *            Type of the original list.
     * @param <R>
     *            Type for the return list.
     * @param original
     *            The original list.
     * @param mapping
     *            A mapping to be used to render the new list.
     *
     * @return The new list.
     */
    public static <T, R> List<R> map(List<T> original, Utilities.Mapping<T, R> mapping) {
        List<R> result;

        if (original instanceof ArrayList) {
            result = new ArrayList<>(original.size());
        } else {
            result = new LinkedList<>();
        }

        for (T t : original) {
            result.add(mapping.map(t));
        }

        return result;
    }

    public static <P1, P2, R> R foldr2(List<P1> list1, List<P2> list2, Utilities.Mapping3<P1, P2, R, R> mapping,
            R initialValue) {
        if (list1.size() != list2.size()) {
            throw new IllegalArgumentException();
        }

        R result = initialValue;

        Iterator<P1> list1Iter = list1.iterator();
        Iterator<P2> list2Iter = list2.iterator();
        while (list1Iter.hasNext()) {
            result = mapping.map(list1Iter.next(), list2Iter.next(), result);
        }

        return result;
    }

}
