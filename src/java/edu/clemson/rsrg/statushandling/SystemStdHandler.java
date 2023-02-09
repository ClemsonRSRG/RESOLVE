/*
 * SystemStdHandler.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.statushandling;

import java.io.PrintWriter;

/**
 * <p>
 * This class outputs all information to {@link System#out} and all warning and errors to {@link System#err} file
 * descriptors.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class SystemStdHandler extends WriterStatusHandler implements StatusHandler {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructor will create an output handler that displays all information and warning output to
     * {@link System#out} and all error output to {@link System#err}.
     * </p>
     */
    public SystemStdHandler() {
        super(new PrintWriter(System.out), new PrintWriter(System.err));
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Stop logging anymore information.
     *
     * (Note: Should only be called when the compile process is over or has been aborted due to an error.)
     * </p>
     */
    @Override
    public synchronized final void stopLogging() {
        stopLogging = true;
    }

}
