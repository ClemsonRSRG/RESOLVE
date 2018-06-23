/*
 * LazyMappingIterator.java
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
package edu.clemson.cs.rsrg.typeandpopulate.utilities;

import edu.clemson.cs.rsrg.misc.Utilities.Mapping;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>A <code>LazyMappingIterator</code> wraps an {@link Iterator} that
 * iterates over objects of type <code>I</code> and presents an interface for
 * mapping over objects of type <code>O</code>. A {@link Mapping} from
 * <code>I</code> to <code>O</code> is used to transform each object as it is
 * requested.</p>

 * @param <I> The type of the objects in the source iterator.
 * @param <O> The type of the final objects.
 *
 * @version 2.0
 */
public final class LazyMappingIterator<I, O> implements Iterator<O> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>An iterator over <code>I</code> type objects.</p> */
    private final Iterator<I> mySource;

    /** <p>A mapping between two types of objects.</p> */
    private final Mapping<I, O> myMapper;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an iterator over a mapping of objects.</p>
     *
     * @param source An iterator over <code>I</code> type objects.
     * @param mapper A mapping between two types of objects.
     */
    public LazyMappingIterator(Iterator<I> source, Mapping<I, O> mapper) {
        mySource = source;
        myMapper = mapper;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)</p>
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public final boolean hasNext() {
        return mySource.hasNext();
    }

    /**
     * <p>Returns the next element in the iteration.</p>
     *
     * @return the next element in the iteration
     *
     * @throws NoSuchElementException if the iteration has no more elements
     * @throws ConcurrentModificationException if there is concurrent modification.
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
     * <p>Removes from the underlying collection the last element returned
     * by this iterator (optional operation).  This method can be called
     * only once per call to {@link #next}.  The behavior of an iterator
     * is unspecified if the underlying collection is modified while the
     * iteration is in progress in any way other than by calling this
     * method.</p>
     *
     * @throws UnsupportedOperationException if the {@code remove}
     *         operation is not supported by this iterator
     *
     * @throws IllegalStateException if the {@code next} method has not
     *         yet been called, or the {@code remove} method has already
     *         been called after the last call to the {@code next}
     *         method
     */
    @Override
    public final void remove() {
        mySource.remove();
    }

}