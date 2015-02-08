/**
 * MathTupleAST.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>A <code>MathTupleAST</code> represents an ordered mathematical list of
 * elements of length at least two.</p>
 *
 * <p>Rolling <code>MathTupleAST</code> into <code>MathSymbolAST</code> was
 * considered and explicitly decided against during the great-math-type-overhaul
 * of 2012. If we chose to admit the presence of some function that builds
 * tuples for us, how would we pass it its parameters if not via a tuple? Thus,
 * <code>MathTupleAST</code> is now a built-in notion, and not imagined as the
 * result of the application of a function.</p>
 */
public class MathTupleAST extends ExprAST {

    private List<ExprAST> myFields = new ArrayList<ExprAST>();
    private int mySize;

    public MathTupleAST(Token start, Token stop, List<ExprAST> fields) {
        this(start, stop, fields.toArray(new ExprAST[0]), fields.size());
    }

    public MathTupleAST(Token start, Token stop, ExprAST[] fields) {
        this(start, stop, fields, fields.length);
    }

    private MathTupleAST(Token start, Token stop, ExprAST[] fields,
            int elementCount) {
        super(start, stop);
        if (elementCount < 2) {
            throw new IllegalArgumentException("Unexpected cartesian product "
                    + "size.");
        }

        int workingSize = 0;

        ExprAST first;
        if (elementCount == 2) {
            first = fields[0];
        }
        else {
            first =
                    new MathTupleAST(getStart(), getStop(), fields,
                            elementCount - 1);
        }

        if (first instanceof MathTupleAST) {
            workingSize += ((MathTupleAST) first).getSize();
        }
        else {
            workingSize += 1;
        }

        ExprAST second = fields[elementCount - 1];
        workingSize += 1;

        myFields.add(first);
        myFields.add(second);

        mySize = workingSize;
    }

    public int getSize() {
        return mySize;
    }

    public List<ExprAST> getFields() {
        return myFields;
    }

    public ExprAST getField(int index) {
        ExprAST result;

        if (index < 0 || index >= mySize) {
            throw new IndexOutOfBoundsException("" + index);
        }

        if (index == (mySize - 1)) {
            result = myFields.get(1);
        }
        else {
            if (mySize == 2) {
                if (index != 0) {
                    throw new IndexOutOfBoundsException("" + index);
                }
                result = myFields.get(0);
            }
            else {
                result = ((MathTupleAST) myFields.get(0)).getField(index);
            }
        }
        return result;
    }

    public boolean isUniversallyQuantified() {
        throw new UnsupportedOperationException("not yet moved over...");
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    public List<ExprAST> getSubExpressions() {
        return myFields;
    }

    @Override
    public void setSubExpression(int index, ExprAST e) {
        myFields.set(index, e);
    }

    @Override
    public ExprAST substituteChildren(Map<ExprAST, ExprAST> substitutions) {
        List<ExprAST> newFields = new ArrayList<ExprAST>();

        for (ExprAST f : myFields) {
            newFields.add(substitute(f, substitutions));
        }
        ExprAST result = new MathTupleAST(getStart(), getStop(), newFields);
        result.setMathType(getMathType());
        result.setMathTypeValue(getMathTypeValue());

        return result;
    }

    @Override
    public ExprAST copy() {
        List<ExprAST> newFields = new ArrayList<ExprAST>();

        for (ExprAST e : myFields) {
            newFields.add(copy(e));
        }
        ExprAST result = new MathTupleAST(getStart(), getStop(), newFields);

        result.setMathType(getMathType());
        result.setMathTypeValue(getMathTypeValue());
        return result;
    }
}