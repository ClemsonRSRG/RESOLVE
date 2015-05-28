/**
 * Utils.java
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
package edu.clemson.cs.r2jt.misc;

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
}
