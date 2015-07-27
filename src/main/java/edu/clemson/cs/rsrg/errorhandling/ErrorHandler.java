/**
 * ErrorHandler.java
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

import edu.clemson.cs.rsrg.init.file.Location;

/**
 * <p>A common interface that all handlers for debugging,
 * errors and/or other information coming from the compiler
 * must implement.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public interface ErrorHandler {

    /**
     * <p>This method displays the error message passed in.</p>
     *
     * @param msg Message to be displayed.
     */
    void error(Location l, String msg);

    /**
     * <p>This method displays the information passed in.</p>
     *
     * @param msg Message to be displayed.
     */
    void info(Location l, String msg);

    /**
     * <p>This method displays compiler warning passed in.</p>
     *
     * @param msg Message to be displayed.
     */
    void warning(Location l, String msg);

}