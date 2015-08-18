/**
 * AntlrErrorListener.java
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
import org.antlr.v4.runtime.*;

/**
 * <p>A custom listener class for the compiler that adds carrot-pointer style
 * reporting for syntax (and semantic) errors.</p>
 *
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 1.0
 */
public class AntlrErrorListener extends BaseErrorListener {

    // ==========================================================
    // Member Fields
    // ==========================================================

    /**
     * <p>This is the error handler for the RESOLVE compiler.</p>
     */
    private final ErrorHandler myErrorHandler;

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>This creates an ANTLR4 error listener with the current
     * error handler deployed by the compiler.</p>
     *
     * @param errorHandler An error handler to display debug or error messages.
     */
    public AntlrErrorListener(ErrorHandler errorHandler) {
        myErrorHandler = errorHandler;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This is thrown when we encounter an syntax error when
     * parsing the input string.</p>
     *
     * @param recognizer A recognizer provided by the ANTLR4.
     * @param offendingSymbol The offending token.
     * @param line The line number where the error occurred.
     * @param charPositionInLine The position in the line where the error occurred.
     * @param msg The message to be displayed.
     * @param e The exception thrown.
     */
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
            Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        ResolveToken offendingToken = (ResolveToken) offendingSymbol;
        String input;
        if (recognizer == null) {
            input = offendingToken.getTokenSource().getInputStream().toString();
        }
        else {
            CommonTokenStream src =
                    (CommonTokenStream) recognizer.getInputStream();
            input = src.getTokenSource().getInputStream().toString();
        }
        String[] lines = input.split("\n");
        String errorLine = lines[line - 1].replaceAll("\t", " ");

        myErrorHandler.error(offendingToken, errorLine);
        for (int i = 0; i < charPositionInLine; i++) {
            myErrorHandler.error(null, " ");
        }
        myErrorHandler.error(null, "^\n");
        myErrorHandler.error(null, msg);
    }

}