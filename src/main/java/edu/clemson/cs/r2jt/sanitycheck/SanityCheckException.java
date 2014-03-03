/**
 * SanityCheckException.java
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
package edu.clemson.cs.r2jt.sanitycheck;

/*
 * An exception to be thrown by sanity checking operations.
 * 
 * Created May 29, 2008.
 * 
 * @author Hampton Smith
 */
public class SanityCheckException extends Exception {

    public SanityCheckException() {
        ;
    }

    public SanityCheckException(String msg) {
        super(msg);
    }
}
