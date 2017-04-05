/*
 * TypeDerivedException.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.absyn;

public class TypeDerivedException extends Exception {

    public static final TypeDerivedException INSTANCE =
            new TypeDerivedException();

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private TypeDerivedException() {

    }
}
