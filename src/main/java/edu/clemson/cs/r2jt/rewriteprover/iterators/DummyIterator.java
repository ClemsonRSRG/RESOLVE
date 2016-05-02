/**
 * DummyIterator.java
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
package edu.clemson.cs.r2jt.rewriteprover.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DummyIterator<T> implements Iterator<T> {

    private final static DummyIterator<Object> INSTANCE =
            new DummyIterator<Object>();

    private DummyIterator() {

    }

    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> getInstance(Iterator<T> i) {
        return (Iterator<T>) INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> getInstance() {
        return (Iterator<T>) INSTANCE;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {

    }

}
