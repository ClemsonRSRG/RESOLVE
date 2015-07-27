/**
 * StdErrHandler.java
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
package edu.clemson.cs.rsrg.errorhandling;

import java.io.PrintWriter;

/**
 * <p>This class outputs all debugging, errors and/or
 * other information coming from the compiler to standard err
 * file descriptor.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class StdErrHandler extends WriterErrorHandler implements ErrorHandler {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructor will create an output handler that
     * always output to standard err.</p>
     */
    public StdErrHandler() {
        super(new PrintWriter(System.err, true));
    }

}