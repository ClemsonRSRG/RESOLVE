/**
 * NoSuchEntryException.java
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
package edu.clemson.cs.r2jt.utilities;

public class NoSuchEntryException extends RuntimeException {

    private static final long serialVersionUID = -6696070520373901964L;

    public final Object entry;

    public NoSuchEntryException(Object entry) {
        this.entry = entry;
    }
}
