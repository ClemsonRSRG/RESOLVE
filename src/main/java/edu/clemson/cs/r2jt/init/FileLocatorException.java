/**
 * FileLocatorException.java
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
package edu.clemson.cs.r2jt.init;

public class FileLocatorException extends Exception {

    // ==========================================================
    // Constructors
    // ==========================================================

    public FileLocatorException() {
        ;
    }

    public FileLocatorException(String msg) {
        super(msg);
    }
}
