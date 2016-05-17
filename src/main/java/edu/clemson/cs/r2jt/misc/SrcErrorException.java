/**
 * SrcErrorException.java
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
package edu.clemson.cs.r2jt.misc;

import org.antlr.v4.runtime.Token;

// Todo: rename this to SourceErrorException once we're settled and all moved
// over.
public class SrcErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final Token myOffendingToken;

    public SrcErrorException(String message, Token offendingToken,
            Throwable cause) {
        super(message, cause);
        myOffendingToken = offendingToken;
    }

    public SrcErrorException(String message, Token t) {
        this(message, t, null);
    }

    public Token getOffendingToken() {
        return myOffendingToken;
    }
}