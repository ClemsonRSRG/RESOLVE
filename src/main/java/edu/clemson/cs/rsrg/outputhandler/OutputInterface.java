/**
 * OutputInterface.java
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
package edu.clemson.cs.rsrg.outputhandler;

/**
 * <p>A common interface that all handlers for debugging,
 * errors and/or other information coming from the compiler
 * must implement.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public interface OutputInterface {

    /**
     * <p>This method displays the information passed in.</p>
     *
     * @param msg Message to be displayed.
     */
    void message(String msg);

}