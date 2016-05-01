/**
 * SingletonIterator.java
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

public class SingletonIterator<T> implements Iterator<T> {

    private final T myElement;
    private boolean myReturnedFlag = false;

    public SingletonIterator(T element) {
        myElement = element;
    }

    @Override
    public boolean hasNext() {
        return !myReturnedFlag;
    }

    @Override
    public T next() {
        myReturnedFlag = true;
        return myElement;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
