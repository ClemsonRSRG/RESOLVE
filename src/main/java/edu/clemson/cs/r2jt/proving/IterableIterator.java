/**
 * IterableIterator.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.proving;

import java.util.Iterator;

public class IterableIterator<T> implements Iterable<T> {

    private final Iterator<T> myIterator;

    public IterableIterator(Iterator<T> iterator) {
        myIterator = iterator;
    }

    @Override
    public Iterator<T> iterator() {
        return myIterator;
    }

}
