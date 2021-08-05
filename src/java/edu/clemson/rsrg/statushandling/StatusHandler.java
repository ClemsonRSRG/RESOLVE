/*
 * StatusHandler.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.statushandling;

import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.statushandling.exception.CompilerException;

/**
 * <p>
 * A common interface that all handlers for debugging, errors and/or other information coming from the compiler must
 * implement.
 * </p>
 *
 * @author Yu-Shan Sun
 * 
 * @version 1.0
 */
public interface StatusHandler {

    /**
     * <p>
     * This method displays the error message passed in.
     * </p>
     *
     * @param l
     *            The location where we encountered the error.
     * @param msg
     *            Message to be displayed.
     */
    void error(Location l, String msg);

    /**
     * <p>
     * Checks to see if we are still logging information.
     * </p>
     *
     * @return True if we are done logging, false otherwise.
     */
    boolean hasStopped();

    /**
     * <p>
     * This method displays the information passed in.
     * </p>
     *
     * @param l
     *            The location where we encountered the error.
     * @param msg
     *            Message to be displayed.
     */
    void info(Location l, String msg);

    /**
     * <p>
     * This method prints the stack trace to the desired output stream.
     * </p>
     *
     * @param e
     *            The encountered compiler exception.
     */
    void printStackTrace(CompilerException e);

    /**
     * <p>
     * Stop logging anymore information.
     *
     * (Note: Should only be called when the compile process is over or has been aborted due to an error.)
     * </p>
     */
    void stopLogging();

    /**
     * <p>
     * This method displays compiler warning passed in.
     * </p>
     *
     * @param l
     *            The location where we encountered the error.
     * @param msg
     *            Message to be displayed.
     */
    void warning(Location l, String msg);

}
