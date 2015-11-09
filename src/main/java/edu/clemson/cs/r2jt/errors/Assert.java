/**
 * Assert.java
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
package edu.clemson.cs.r2jt.errors;

public class Assert {

    public static void isTrue(boolean condition) {
        if (!condition) {
            abortProgram();
        }
    }

    public static void isNull(Object value) {
        if (value != null) {
            abortProgram();
        }
    }

    public static void isNotNull(Object value) {
        if (value == null) {
            abortProgram();
        }
    }

    public static void abortProgram() {
        System.out.println("===== Assertion Failed =====");
        Thread.dumpStack();
        throw new RuntimeException("Assertion Failed");
    }
}
