/**
 * TypeMismatchException.java
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
package edu.clemson.cs.r2jt.typeandpopulate2;

public class TypeMismatchException extends Exception {

    public static final TypeMismatchException INSTANCE =
            new TypeMismatchException();

    private static final long serialVersionUID = 1L;

    private TypeMismatchException() {}
}
