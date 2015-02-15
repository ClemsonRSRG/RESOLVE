/**
 * UnderliningErrorListener.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.misc.SrcErrorException;
import org.antlr.v4.runtime.*;

/**
 * <p>A custom listener class for the compiler that adds carrot-pointer style
 * reporting for syntax (and semantic) errors.</p>
 */
public class UnderliningErrorListener extends BaseErrorListener {

    public static final UnderliningErrorListener INSTANCE =
            new UnderliningErrorListener();

    public void syntaxError(Recognizer<?, ?> recognizer,
            Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        String fileName =
                ((Token) offendingSymbol).getTokenSource().getSourceName();
        System.err.println(groomFileName(fileName).toLowerCase() + ":"
                + line + ":" + charPositionInLine + ": " + msg);
        underlineError(recognizer, (Token) offendingSymbol, line,
                charPositionInLine);
    }

    /**
     * <p>Internal compiler errors for which there is no line or location
     * information available.</p>
     * @param msg The error message.
     */
    public void compilerError(String msg) {
        System.err.println("error: " + msg);
    }

    /**
     * <p>This is called mainly when an {@link SrcErrorException} is raised
     * or caught.</p>
     * @param offendingSymbol The token indicating a problem site
     * @param msg The error message.
     */
    public void semanticError(Token offendingSymbol, String msg) {
        if (offendingSymbol.getTokenSource() == null || offendingSymbol == null) {
            System.err.println("-1:-1:-1: " + msg);
        }
        else {
            String fileName = offendingSymbol.getTokenSource().getSourceName();
            System.err.println(groomFileName(fileName).toLowerCase() + ":"
                    + offendingSymbol.getLine() + ":"
                    + offendingSymbol.getCharPositionInLine() + ": " + msg);

            underlineError(null, offendingSymbol, offendingSymbol.getLine(),
                    offendingSymbol.getCharPositionInLine());
        }
    }

    protected void underlineError(Recognizer recognizer, Token offendingToken,
            int line, int charPositionInLine) {
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

        System.err.println(errorLine);

        for (int i = 0; i < charPositionInLine; i++) {
            System.err.print(" ");
        }
        System.err.print("^");
        System.exit(1);
    }

    protected static String groomFileName(String fileName) {
        int start = fileName.lastIndexOf("/");
        if (start == -1) {
            return fileName;
        }
        return fileName.substring(start + 1, fileName.length());
    }
}