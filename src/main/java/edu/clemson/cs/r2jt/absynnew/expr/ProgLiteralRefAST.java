/**
 * ProgLiteralRefAST.java
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
package edu.clemson.cs.r2jt.absynnew.expr;

import org.antlr.v4.runtime.Token;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>This class represents a program literal such as a <code>String</code>,
 * <code>Character</code>, or <code>Integer</code>.</p>
 *
 * @param <T>
 * @author dtwelch <dtw.welch@gmail.com>
 */
public class ProgLiteralRefAST<T> extends ProgExprAST {

    private final T myLiteral;

    public ProgLiteralRefAST(Token start, Token stop, T literal) {
        super(start, stop);
        myLiteral = literal;
    }

    public T getLiteral() {
        return myLiteral;
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    @Override
    public List<ExprAST> getSubExpressions() {
        return Collections.emptyList();
    }

    @Override
    public void setSubExpression(int index, ExprAST e) {}

    @Override
    protected ExprAST substituteChildren(Map<ExprAST, ExprAST> substitutions) {
        return new ProgLiteralRefAST<T>(getStart(), getStop(), myLiteral);
    }

    public static class ProgCharacterRefAST
            extends
                ProgLiteralRefAST<Character> {

        public ProgCharacterRefAST(Token start, Token stop,
                Character characterLiteral) {
            super(start, stop, characterLiteral);
        }
    }

    public static class ProgIntegerRefAST extends ProgLiteralRefAST<Integer> {

        public ProgIntegerRefAST(Token start, Token stop, Integer integerLiteral) {
            super(start, stop, integerLiteral);
        }
    }

    public static class ProgStringRefAST extends ProgLiteralRefAST<String> {

        public ProgStringRefAST(Token start, Token stop, String stringLiteral) {
            super(start, stop, stringLiteral);
        }
    }
}