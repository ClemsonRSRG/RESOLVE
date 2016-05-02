/**
 * Utils.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.misc;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    public static class Indirect<T> {

        public T data;
    }

    /**
     * <p>A builder of objects of type <code>T</code>.</p>
     *
     * @param <T> The type object to be created.
     */
    public interface Builder<T> {

        T build();
    }

    /**
     * <p>A two-parameter mapping.</p>
     */
    public interface Mapping<I, O> {

        public O map(I input);
    }

    /**
     * <p>A three-parameter mapping.</p>
     */
    public interface Mapping3<P1, P2, P3, R> {

        public R map(P1 p1, P2 p2, P3 p3);
    }

    public static <T> String join(Collection<T> data, String separator) {
        return join(data.iterator(), separator, "", "");
    }

    public static <T> String join(Collection<T> data, String separator,
            String left, String right) {
        return join(data.iterator(), separator, left, right);
    }

    public static <T> String join(Iterator<T> iter, String separator,
            String left, String right) {
        StringBuilder buf = new StringBuilder();

        while (iter.hasNext()) {
            buf.append(iter.next());
            if (iter.hasNext()) {
                buf.append(separator);
            }
        }
        return left + buf.toString() + right;
    }

    public static <T> String join(T[] array, String separator) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < array.length; ++i) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

    /**
     * Returns a list of {@code E} given: an expected type {@code T}, some
     * number
     * of concrete syntax {@code nodes}, and a mapping from rule contexts to
     * some number of elements descending from {@code E}.
     *
     * @param expectedType The class type to inhabit the returned list
     * @param nodes A list of concrete syntax nodes, as obtained through
     *        a visitor, listener, etc.
     * @param annotations A map from rule context to the primary supertype
     *        of {@code expectedType} ({@code E}).
     * @param <E> Super type of {@code expectedType}.
     * @param <T> The expected type.
     * @return A list of {@code T}.
     */
    public static <E, T extends E> List<T> collect(
            Class<T> expectedType, List<? extends ParseTree> nodes,
            ParseTreeProperty<? extends E> annotations) {
        List<T> result = new ArrayList<>();
        for (ParseTree node : nodes) {
            result.add(expectedType.cast(annotations.get(node)));
        }
        return result;
    }
}
