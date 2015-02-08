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

import org.antlr.v4.runtime.*;

/**
 * <p>A custom listener class for the compiler that adds carrot-pointer style
 * reporting for syntax (and semantic) errors.</p>
 */
public class UnderliningErrorListener extends BaseErrorListener {

    public static final UnderliningErrorListener INSTANCE =
            new UnderliningErrorListener();

    /**
     * <p>This automatically gets called everytime <tt>Antlr</tt> detects a
     * syntax or <em>recognition error</em>.</p>
     */
    public void syntaxError(Recognizer<?, ?> recognizer,
            Object offendingSymbol, int line, int charPositionInLine,
            String msg, RecognitionException e) {

        System.err.println("line " + line + ":" + charPositionInLine + ", "
                + ((Token) offendingSymbol).getTokenSource().getSourceName()
                + "\n" + msg);
        underlineError(recognizer, (Token) offendingSymbol, line,
                charPositionInLine);
    }

    public void reportError(Token offendingSymbol, String msg) {
        if (offendingSymbol.getTokenSource() == null || offendingSymbol == null) {
            System.err.println(msg);
        }
        else {
            System.err.println("line " + offendingSymbol.getLine() + ":"
                    + offendingSymbol.getCharPositionInLine() + ", "
                    + offendingSymbol.getTokenSource().getSourceName() + "\n"
                    + msg);

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
}