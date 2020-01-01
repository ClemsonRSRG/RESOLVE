/*
 * FlagDependencyException.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.misc;

/**
 * <p>
 * A <code>FlagDependencyException</code> indicates that the user-provided flag
 * configuration is not
 * acceptable for some reason. The reason for the exception is provided in the
 * exception's message.
 * </p>
 */
public class FlagDependencyException extends Exception {

    private static final long serialVersionUID = 8233299508253914859L;

    public FlagDependencyException(String msg) {
        super(msg);
    }
}
