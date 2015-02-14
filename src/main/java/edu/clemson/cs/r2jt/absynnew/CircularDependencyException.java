/**
 * CircularDependencyException.java
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
package edu.clemson.cs.r2jt.absynnew;

/**
 * <p>A <code>CircularDependencyException</code> indicates an unresolvable
 * circular dependency between two (or more) modules.</p>
 */
public class CircularDependencyException extends RuntimeException {

    /**
     * <p>Creates a new <code>CircularDependencyException</code> with the given
     * message.</p>
     *
     * @param msg The message.
     */
    public CircularDependencyException(String msg) {
        super(msg);
    }

}