/**
 * NoSolutionException.java
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
package edu.clemson.cs.r2jt.typeandpopulate;

public class NoSolutionException extends Exception {

    public static final NoSolutionException INSTANCE =
            new NoSolutionException();

    private static final long serialVersionUID = 1L;

    private NoSolutionException() {
        super();
    }

    public NoSolutionException(String msg) {
        super(msg);
    }

    public NoSolutionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NoSolutionException(Throwable cause) {
        super(cause);
    }
}
