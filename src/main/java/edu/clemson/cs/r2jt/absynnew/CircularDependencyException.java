/**
 * CircularDependencyException.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absynnew;

/**
 * An {@code CircularDependencyException} indicates an unresolvable
 * circular dependency between two (or more) modules.
 */
public class CircularDependencyException extends RuntimeException {

    public CircularDependencyException(String msg) {
        super(msg);
    }

}