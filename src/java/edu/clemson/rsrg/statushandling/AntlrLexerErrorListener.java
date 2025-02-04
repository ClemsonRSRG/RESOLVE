/*
 * AntlrLexerErrorListener.java
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
import edu.clemson.rsrg.parsing.data.ResolveTokenFactory;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

/**
 * <p>
 * A custom listener class for the compiler that reports lexer errors.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class AntlrLexerErrorListener extends BaseErrorListener {

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
    public AntlrLexerErrorListener(StatusHandler statusHandler) {
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
        // Only do this if we have a ResolveTokenFactory
        if (recognizer.getTokenFactory() != null && recognizer.getTokenFactory() instanceof ResolveTokenFactory) {
            // Build a location
            ResolveTokenFactory tokenFactory = (ResolveTokenFactory) recognizer.getTokenFactory();
            Fault fault = new Fault(FaultType.TOKEN_FAULT,
                    new Location(tokenFactory.getFile(), line, charPositionInLine), msg, false);
            myStatusHandler.registerAndStreamFault(fault);
        }
        // Otherwise simply return the raw string.
        else {
            Fault fault = new Fault(FaultType.TOKEN_FAULT, null, "Line " + line + ":" + charPositionInLine + " " + msg,
                    false);
            myStatusHandler.registerAndStreamFault(fault);
        }
    }

}
