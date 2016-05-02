/**
 * EmptyImmutableList.java
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
package edu.clemson.cs.r2jt.rewriteprover.immutableadts;

import java.util.Iterator;

import edu.clemson.cs.r2jt.rewriteprover.iterators.DummyIterator;

public class EmptyImmutableList<E> extends AbstractImmutableList<E> {

    private final Iterator<E> TYPESAFE_ITERATOR = (Iterator<E>) null;

    @Override
    public E get(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ImmutableList<E> head(int length) {

        if (length != 0) {
            throw new IndexOutOfBoundsException();
        }

        return this;
    }

    @Override
    public Iterator<E> iterator() {
        return DummyIterator.getInstance(TYPESAFE_ITERATOR);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public ImmutableList<E> tail(int startIndex) {

        if (startIndex != 0) {
            throw new IndexOutOfBoundsException();
        }

        return this;
    }

}
