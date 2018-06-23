/*
 * CompilerException.java
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
package edu.clemson.cs.r2jt.init;

public class CompilerException extends Exception {

    // ==========================================================
    // Constructors
    // ==========================================================

    public CompilerException() {
        ;
    }

    public CompilerException(String msg) {
        super(msg);
    }
}
