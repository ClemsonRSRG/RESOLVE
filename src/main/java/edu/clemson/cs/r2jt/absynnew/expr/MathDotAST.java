/**
 * MathDotAST.java
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

import edu.clemson.cs.r2jt.misc.SrcErrorException;
import org.antlr.v4.runtime.Token;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MathDotAST extends ExprAST {

    private final List<MathSymbolAST> mySegments;

    public MathDotAST(Token start, Token stop, List<MathSymbolAST> segments) {
        super(start, stop);
        mySegments = segments;
    }

    @Override
    public List<? extends ExprAST> getSubExpressions() {
        return mySegments;
    }

    @Override
    public void setSubExpression(int index, ExprAST e) {
        mySegments.set(index, (MathSymbolAST) e);
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    protected ExprAST substituteChildren(Map<ExprAST, ExprAST> substitutions) {
        ExprAST retval;

        List<MathSymbolAST> newSegments = new ArrayList<MathSymbolAST>();
        for (ExprAST e : mySegments) {
            newSegments.add((MathSymbolAST) substitute(e, substitutions));
        }
        retval = new MathDotAST(getStart(), getStop(), newSegments);
        return retval;
    }

    @Override
    public String toString() {
        ST x = new ST("<segments; separator = {.}>");
        return x.add("segments", mySegments).render();
    }
}
