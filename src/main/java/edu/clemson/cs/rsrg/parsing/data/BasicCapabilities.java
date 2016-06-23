/**
 * BasicCapabilities.java
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
package edu.clemson.cs.rsrg.parsing.data;

/**
 * <p>An interface that all intermediate objects created by
 * the RESOLVE compiler needs to implement.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public interface BasicCapabilities {

    /**
     * <p>This method creates a special indented
     * text version of the instantiated object.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentInc The additional indentation increment
     *                       for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    String asString(int indentSize, int innerIndentInc);

}