/**
 * BindingException.java
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
package edu.clemson.cs.r2jt.typeandpopulate;

public class BindingException extends Exception {

    private static final long serialVersionUID = 1L;

    public final Object found;
    public final Object expected;

    public BindingException(Object found, Object expected) {
        this.found = found;
        this.expected = expected;
    }
}
