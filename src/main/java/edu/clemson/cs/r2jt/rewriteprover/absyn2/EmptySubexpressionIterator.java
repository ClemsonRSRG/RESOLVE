/**
 * EmptySubexpressionIterator.java
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
package edu.clemson.cs.r2jt.rewriteprover.absyn2;

import java.util.NoSuchElementException;

public class EmptySubexpressionIterator implements PExpSubexpressionIterator {

    public static final EmptySubexpressionIterator INSTANCE =
            new EmptySubexpressionIterator();

    private EmptySubexpressionIterator() {

    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public PExpr next() {
        throw new NoSuchElementException();
    }

    @Override
    public PExpr replaceLast(PExpr newExpression) {
        throw new IllegalStateException("Must call next() first.");
    }

}