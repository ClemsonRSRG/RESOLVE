/**
 * WriterErrorHandler.java
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

import edu.clemson.cs.rsrg.parsing.data.ResolveToken;
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
public class WriterErrorHandler implements ErrorHandler {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>This is the output writer object.</p> */
    private final Writer myOutputWriter;

    /** <p>Boolean flag to check to see if we are still logging.</p> */
    protected boolean stopLogging;

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
        stopLogging = false;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Outputs a critical error message.</p>
     *
     * @param token The token where we encountered the error.
     * @param msg Message to be displayed.
     */
    public void error(ResolveToken token, String msg) {
        try {
            if (!stopLogging) {
                StringBuilder sb = new StringBuilder();
                sb.append("Error: ");
                sb.append(getLocation(token));
                sb.append(msg);
                sb.append("\n");

                myOutputWriter.write(sb.toString());
                myOutputWriter.flush();
            }
            else {
                throw new RuntimeException("Error handler has been stopped.");
            }
        }
        catch (IOException e) {
            System.err
                    .println("Error writing information to the specified output.");
            e.printStackTrace();
        }
    }

    /**
     * <p>Checks to see if we are still logging information.</p>
     *
     * @return True if we are done logging, false otherwise.
     */
    public boolean hasStopped() {
        return stopLogging;
    }

    /**
     * <p>Outputs an informational message, not an error or warning.</p>
     *
     * @param token The token where we encountered the error.
     * @param msg A compilation message.
     */
    public void info(ResolveToken token, String msg) {
        try {
            if (!stopLogging) {
                StringBuilder sb = new StringBuilder();
                sb.append(getLocation(token));
                sb.append(msg);
                sb.append("\n");

                myOutputWriter.write(sb.toString());
                myOutputWriter.flush();
            }
            else {
                throw new RuntimeException("Error handler has been stopped.");
            }
        }
        catch (IOException e) {
            System.err
                    .println("Error writing information to the specified output.");
            e.printStackTrace();
        }
    }

    /**
     * <p>Stop logging anymore information.
     *
     * (Note: Should only be called when the compile process
     * is over or has been aborted due to an error.)</p>
     */
    public void stopLogging() {
        stopLogging = true;

        try {
            myOutputWriter.close();
        }
        catch (IOException e) {
            System.err.println("Error closing the output stream.");
            e.printStackTrace();
        }
    }

    /**
     * <p>Outputs a warning message.</p>
     *
     * @param token The token where we encountered the error.
     * @param msg Message to be displayed.
     */
    public void warning(ResolveToken token, String msg) {
        try {
            if (!stopLogging) {
                StringBuilder sb = new StringBuilder();
                sb.append("Warning: ");
                sb.append(getLocation(token));
                sb.append(msg);
                sb.append("\n");

                myOutputWriter.write(sb.toString());
                myOutputWriter.flush();
            }
            else {
                throw new RuntimeException("Error handler has been stopped.");
            }
        }
        catch (IOException e) {
            System.err
                    .println("Error writing information to the specified output.");
            e.printStackTrace();
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>Returns the location from the token in string format.</p>
     *
     * @param token The token where we encountered the error.
     *
     * @return Location as a string.
     */
    private String getLocation(ResolveToken token) {
        StringBuilder sb = new StringBuilder();
        if (token != null) {
            sb.append(groomFileName(token.getTokenSource().getSourceName()));

            // Append the line and column number
            sb.append("(");
            sb.append(token.getLine());
            sb.append(":");
            sb.append(token.getCharPositionInLine());
            sb.append(")");
        }

        return sb.toString();
    }

    /**
     * <p>Trims all the path information from the filename.</p>
     *
     * @param fileName The full path filename.
     *
     * @return Filename only.
     */
    private String groomFileName(String fileName) {
        int start = fileName.lastIndexOf("/");
        if (start == -1) {
            return fileName;
        }
        return fileName.substring(start + 1, fileName.length());
    }

}