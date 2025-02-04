/*
 * AntlrParserErrorListener.java
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
import edu.clemson.rsrg.parsing.data.ResolveToken;
import org.antlr.v4.runtime.*;

/**
 * <p>
 * A custom listener class for the compiler that adds carrot-pointer style reporting for syntax (and semantic) errors.
 * </p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 *
 * @version 1.0
 */
public class AntlrParserErrorListener extends BaseErrorListener {

    // ==========================================================
    // Member Fields
    // ==========================================================

    /**
     * <p>
     * This is the status handler for the RESOLVE compiler.
     * </p>
     */
    private final StatusHandler myStatusHandler;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>
     * This creates an ANTLR4 error listener with the current error handler deployed by the compiler.
     * </p>
     *
     * @param statusHandler
     *            An status handler to display debug or error messages.
     */
    public AntlrParserErrorListener(StatusHandler statusHandler) {
        myStatusHandler = statusHandler;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This is thrown when we encounter an syntax error when parsing the input string.
     * </p>
     *
     * @param recognizer
     *            A recognizer provided by the ANTLR4.
     * @param offendingSymbol
     *            The offending token.
     * @param line
     *            The line number where the error occurred.
     * @param charPositionInLine
     *            The position in the line where the error occurred.
     * @param msg
     *            The message to be displayed.
     * @param e
     *            The exception thrown.
     */
    @Override
    public final void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        ResolveToken offendingToken = (ResolveToken) offendingSymbol;
        String input;
        if (recognizer == null) {
            input = offendingToken.getTokenSource().getInputStream().toString();
        } else {
            CommonTokenStream src = (CommonTokenStream) recognizer.getInputStream();
            input = src.getTokenSource().getInputStream().toString();
        }
        String[] lines = input.split("\n");
        String errorLine = lines[line - 1].replaceAll("\t", " ");

        // Obtain the location from the token if it is not null
        Location location = null;
        if (offendingToken != null) {
            location = offendingToken.getLocation();
        }

        String errorMsg = buildErrorMsg(charPositionInLine, errorLine, msg);

        Fault fault = new Fault(FaultType.ANTLR_FAULT, location, errorMsg, false);
        myStatusHandler.registerAndStreamFault(fault);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * Uses the location, original line that caused the error and the ANTLR4 provided message, build the adequate error
     * message to be displayed to the user.
     * </p>
     *
     * @param charPositionInLine
     *            The error position in the line.
     * @param line
     *            The text from the line that caused the error.
     * @param msg
     *            The error message retrieved from ANTLR4.
     *
     * @return The formatted error message as a String.
     */
    private String buildErrorMsg(int charPositionInLine, String line, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(line);
        sb.append("\n");
        for (int i = 0; i < charPositionInLine; i++) {
            sb.append(" ");
        }
        sb.append("^\n");
        sb.append(msg);

        return sb.toString();
    }

}
