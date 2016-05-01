/**
 * MathSegmentsAST.java
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
package edu.clemson.cs.r2jt.absynnew.expr;

import edu.clemson.cs.r2jt.misc.Utils;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MathSegmentsAST extends ExprAST {

    private final List<MathSymbolAST> mySegments =
            new ArrayList<MathSymbolAST>();

    public MathSegmentsAST(Token start, Token stop, List<MathSymbolAST> segments) {
        super(start, stop);
        mySegments.addAll(segments);
    }

    public List<MathSymbolAST> getSegments() {
        return mySegments;
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
        retval = new MathSegmentsAST(getStart(), getStop(), newSegments);
        return retval;
    }

    @Override
    public String toString() {
        return Utils.join(mySegments, ".");
    }
}
