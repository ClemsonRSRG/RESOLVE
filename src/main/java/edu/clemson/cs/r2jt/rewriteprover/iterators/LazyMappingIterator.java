/*
 * LazyMappingIterator.java
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
package edu.clemson.cs.r2jt.rewriteprover.iterators;

import java.util.Iterator;

import edu.clemson.cs.r2jt.misc.Utils.Mapping;
import java.util.ConcurrentModificationException;

/**
 * <p>
 * A <code>LazyMappingIterator</code> wraps an <code>Iterator</code> that
 * iterates over objects of
 * type <code>I</code> and presents an interface for mapping over objects of
 * type <code>O</code>. A
 * <code>Mapping</code> from <code>I</code> to <code>O</code> is used to
 * transform each object as it
 * is requested.
 * </p>
 * 
 * @param <I> The type of the objects in the source iterator.
 * @param <O> The type of the final objects.
 */
public final class LazyMappingIterator<I, O> implements Iterator<O> {

    private final Iterator<I> mySource;
    private final Mapping<I, O> myMapper;

    public LazyMappingIterator(Iterator<I> source, Mapping<I, O> mapper) {
        mySource = source;
        myMapper = mapper;
    }

    @Override
    public boolean hasNext() {
        return mySource.hasNext();
    }

    @Override
    public O next() {
        try {
            return myMapper.map(mySource.next());
        }
        catch (ConcurrentModificationException cme) {
            int i = 5;
            throw new RuntimeException(cme);
        }
    }

    @Override
    public void remove() {
        mySource.remove();
    }
}
