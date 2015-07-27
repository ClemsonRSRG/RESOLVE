/**
 * WriterOutputHandler.java
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

import java.io.IOException;
import java.io.Writer;

/**
 * <p>This class outputs all debugging, errors and/or
 * other information coming from the compiler to the specified
 * writer.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class WriterErrorHandler implements OutputInterface {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>This is the output writer object.</p> */
    private final Writer myOutputWriter;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructor takes a Java <code>Writer</code> object
     * that will be used to display the information.</p>
     *
     * @param outWriter A <code>Writer</code> object.
     */
    public WriterErrorHandler(Writer outWriter) {
        myOutputWriter = outWriter;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Message Output Methods
    // -----------------------------------------------------------

    /**
     * <p>Outputs an informational message, not an error or warning.</p>
     *
     * @param msg A compilation message.
     */
    public void message(String msg) {
        try {
            myOutputWriter.write(msg + "\n");
            myOutputWriter.flush();
        }
        catch (IOException e) {
            System.err
                    .println("Error writing information to the specified output");
            e.printStackTrace();
        }
    }

}