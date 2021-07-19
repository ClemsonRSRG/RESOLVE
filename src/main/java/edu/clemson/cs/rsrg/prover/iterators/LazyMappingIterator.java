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
package edu.clemson.cs.rsrg.prover.iterators;

import edu.clemson.cs.rsrg.misc.Utilities.Mapping;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

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
 *
 * @author Hampton Smith
 * @version 2.0
 */
public final class LazyMappingIterator<I, O> implements Iterator<O> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The source iterator.
     * </p>
     */
    private final Iterator<I> mySource;

    /**
     * <p>
     * A mapping to items
     * </p>
     */
    private final Mapping<I, O> myMapper;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Creates an lazy iterator for a mapping of elements.
     * </p>
     *
     * @param source The source iterator.
     * @param mapper The mapping of elements.
     */
    public LazyMappingIterator(Iterator<I> source, Mapping<I, O> mapper) {
        mySource = source;
        myMapper = mapper;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean hasNext() {
        return mySource.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final O next() {
        try {
            return myMapper.map(mySource.next());
        }
        catch (ConcurrentModificationException cme) {
            int i = 5;
            throw new RuntimeException(cme);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void remove() {
        mySource.remove();
    }

}
