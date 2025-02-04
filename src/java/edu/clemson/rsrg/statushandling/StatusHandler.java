/*
 * StatusHandler.java
 * ---------------------------------
 * Copyright (c) 2024
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

import java.util.List;

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
     * This method registers and displays compiler fault passed in.
     * </p>
     *
     * @param fault
     *            The fault to be registered and displayed
     */
    void registerAndStreamFault(Fault fault);

    /**
     * <p>
     * This method returns the number of faults captured by this status handler.
     * </p>
     *
     * @return The number of captured faults
     */
    public int retrieveFaultCount();

    /**
     * <p>
     * This method returns an ordered list of registered faults on the system
     * </p>
     *
     * @return The ordered list of faults
     */
    public List<Fault> getFaults();

    /**
     * <p>
     * This method registers a new inorder fault
     * </p>
     */
    public void registerFault(Fault fault);

    /**
     * <p>
     * This method writes and flushes all registered faults to the output
     * </p>
     */
    public void streamAllFaults();
}
