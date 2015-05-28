/**
 * MathSetAST.java
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
package edu.clemson.cs.r2jt.absynnew.expr;

import edu.clemson.cs.r2jt.absynnew.TreeUtil;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>A <code>MathSetAST</code> allows users to construct mathematical sets
 * using <tt>RESOLVE</tt>s builtin curly brace <code>{ ... }</code>
 * set-notation.</p>
 */
public class MathSetAST extends ExprAST {

    private final List<ExprAST> myElements = new ArrayList<ExprAST>();

    public MathSetAST(Token start, Token stop, List<ExprAST> elements) {
        super(start, stop);
        myElements.addAll(elements);
    }

    public List<ExprAST> getElements() {
        return myElements;
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    public List<ExprAST> getSubExpressions() {
        return new ArrayList<ExprAST>(myElements);
    }

    @Override
    public void setSubExpression(int index, ExprAST e) {
        if (e instanceof MathSymbolAST) {
            myElements.set(index, (MathSymbolAST) e);
        }
    }

    @Override
    protected ExprAST substituteChildren(Map<ExprAST, ExprAST> substitutions) {
        return new MathSetAST(getStart(), getStop(), myElements);
    }

    @Override
    public String toString() {
        return "{ " + TreeUtil.join(myElements, ", ") + " }";
    }
}
