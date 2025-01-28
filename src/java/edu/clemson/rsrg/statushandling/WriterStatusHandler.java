/*
 * WriterStatusHandler.java
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
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class outputs all debugging, errors and/or other information coming from the compiler to the specified writer.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class WriterStatusHandler implements StatusHandler {

    // ===========================================================
    // Member Fields
    // ===========================================================
    /**
     * <p>
     * Storage for ordered Warnings
     * </p>
     */
    private List<Fault> faults;

    /**
     * <p>
     * Writer for information output.
     * </p>
     */
    private final Writer myOutputWriter;

    /**
     * <p>
     * Writer for warning and error output.
     * </p>
     */
    private final Writer myErrorWriter;

    /**
     * <p>
     * Boolean flag to check to see if we are still logging.
     * </p>
     */
    protected boolean stopLogging;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructor takes in two {@link Writer} objects that will be used to display the various information,
     * warning and error messages provided by the compiler.
     * </p>
     *
     * @param outWriter
     *            A writer for general information output.
     * @param errorWriter
     *            A writer for warning/error output.
     */
    public WriterStatusHandler(Writer outWriter, Writer errorWriter) {
        myOutputWriter = outWriter;
        myErrorWriter = errorWriter;
        stopLogging = false;
        faults = new ArrayList<>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Checks to see if we are still logging information.
     * </p>
     *
     * @return True if we are done logging, false otherwise.
     */
    @Override
    public final boolean hasStopped() {
        return stopLogging;
    }

    /**
     * <p>
     * Outputs an informational message, not an error or warning.
     * </p>
     *
     * @param l
     *            The location where we encountered the error.
     * @param msg
     *            A compilation message.
     */
    @Override
    public synchronized final void info(Location l, String msg) {
        try {
            if (!hasStopped()) {
                StringBuilder sb = new StringBuilder();
                if (l != null) {
                    sb.append(l.toString());
                }
                sb.append(msg);
                sb.append("\n");

                myOutputWriter.write(sb.toString());
                myOutputWriter.flush();
            } else {
                throw new RuntimeException("Error handler has been stopped.");
            }
        } catch (IOException e) {
            System.err.println("Error writing information to the specified output.");
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * This method prints the stack trace to the desired output stream.
     * </p>
     *
     * @param e
     *            The encountered compiler exception.
     */
    @Override
    public synchronized final void printStackTrace(CompilerException e) {
        try {
            if (!hasStopped()) {
                StringBuilder sb = new StringBuilder();
                StackTraceElement[] elements = e.getStackTrace();
                for (int i = 0; i < elements.length; i++) {
                    if (i != 0) {
                        sb.append("\tat ");
                    }
                    sb.append(elements[i]);
                    sb.append("\n");
                }

                // Print the stack trace for the cause
                if (e.getCause() != null) {
                    Throwable cause = e.getCause();
                    sb.append("Caused by: ");
                    sb.append(cause);
                    sb.append("\n");

                    StackTraceElement[] causeElements = cause.getStackTrace();
                    for (int i = 0; i < causeElements.length; i++) {
                        sb.append("\tat ");
                        sb.append(causeElements[i]);
                        sb.append("\n");
                    }
                }

                myErrorWriter.write(sb.toString());
                myErrorWriter.flush();
            } else {
                throw new RuntimeException("Error handler has been stopped.");
            }
        } catch (IOException ioe) {
            System.err.println("Error writing information to the specified output.");
            ioe.printStackTrace();
        }
    }

    /**
     * <p>
     * Stop logging anymore information.
     *
     * (Note: Should only be called when the compile process is over or has been aborted due to an error.)
     * </p>
     */
    @Override
    public synchronized void stopLogging() {
        stopLogging = true;

        try {
            myOutputWriter.close();
            myErrorWriter.close();
        } catch (IOException e) {
            System.err.println("Error closing the output stream.");
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * This method registers and displays compiler warning passed in.
     * </p>
     *
     * @param fault
     *            The warning to be registered and displayed
     */
    @Override
    public void registerAndStreamFault(Fault fault) {
        if (hasStopped()) {
            throw new RuntimeException("Error handler has been stopped.");
        }

        registerFault(fault);

        try {
            myErrorWriter.write(fault.toString());
            myErrorWriter.flush();
        } catch (IOException e) {
            System.err.println("Error writing information to the specified output.");
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * This method returns the number of warnings captured by this status handler.
     * </p>
     *
     * @return The number of captured warnings
     */
    public int retrieveFaultCount() {
        return faults.size();
    }

    /**
     * <p>
     * This method returns an ordered list of registered warnings on the system
     * </p>
     *
     * @return The ordered list of warnings
     */
    public List<Fault> getFaults() {
        return faults;
    }

    /**
     * <p>
     * This method registers a new inorder warning
     * </p>
     */
    public void registerFault(Fault fault) {
        faults.add(fault);

        if (fault.isCritical()) {
            try {
                myErrorWriter.write("CRITICAL FAULT: FLUSHING ALL FAULTS");
                myErrorWriter.flush();
                streamAllFaults();
                throw new RuntimeException("CRITICAL FAULT: ALL FAULTS FLUSHED TO ERROR STREAM");
            } catch (IOException e) {
                throw new RuntimeException("Error writing information to the specified output.");
            }
        }
    }

    /**
     * <p>
     * This method writes and flushes all registered warnings to the output
     * </p>
     */
    public void streamAllFaults() {
        for (Fault fault : faults) {
            try {
                myErrorWriter.write(fault.toString());
                myErrorWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
