/**
 * UnderliningErrorListener.java
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
package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.misc.SrcErrorException;
import org.antlr.v4.runtime.*;

/**
 * A custom listener class for the compiler that adds carrot-pointer style
 * reporting for syntax (and semantic) errors.
 */
public class UnderliningErrorListener extends BaseErrorListener {

    public static final UnderliningErrorListener INSTANCE =
            new UnderliningErrorListener();

    private UnderliningErrorListener() {}

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
            Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {
        String fileName =
                ((Token) offendingSymbol).getTokenSource().getSourceName();
        System.err.println(groomFileName(fileName) + ":" + line + ":"
                + charPositionInLine + ": " + msg);
        underlineError(recognizer, (Token) offendingSymbol, line,
                charPositionInLine);
    }

    /**
     * This is called mainly when an {@link SrcErrorException} is raised
     * or caught.
     *
     * @param offendingSymbol The token indicating a problem site.
     * @param msg The error message.
     */
    public void semanticError(Token offendingSymbol, String msg) {
        if (offendingSymbol == null) {
            System.err.println("-1:-1:-1: " + msg);
        }
        else if (offendingSymbol.getTokenSource() == null) {
            System.err.println("-1:-1:-1: " + msg);
        }
        else {
            String fileName = offendingSymbol.getTokenSource().getSourceName();
            System.err.println(groomFileName(fileName) + ":"
                    + offendingSymbol.getLine() + ":"
                    + offendingSymbol.getCharPositionInLine() + ": " + msg);

            underlineError(null, offendingSymbol, offendingSymbol.getLine(),
                    offendingSymbol.getCharPositionInLine());
        }
    }

    /**
     * Internal compiler errors for which there is no line or location
     * information available.
     */
    public static void fatalInternalError(String error, Throwable e) {
        internalError(error, e);
        throw new RuntimeException(error, e);
    }

    public static void internalError(String error, Throwable e) {
        StackTraceElement location = getLastNonErrorManagerCodeLocation(e);
        internalError("Exception " + e + "@" + location + ": " + error);
    }

    public static void internalError(String error) {
        StackTraceElement location =
                getLastNonErrorManagerCodeLocation(new Exception());
        String msg = location + ": " + error;
        System.err.println("internal error: " + msg);
    }

    private static StackTraceElement getLastNonErrorManagerCodeLocation(
            Throwable e) {
        StackTraceElement[] stack = e.getStackTrace();
        int i = 0;
        for (; i < stack.length; i++) {
            StackTraceElement t = stack[i];
            if (!t.toString().contains("AntlrErrorListener")) {
                break;
            }
        }
        StackTraceElement location = stack[i];
        return location;
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