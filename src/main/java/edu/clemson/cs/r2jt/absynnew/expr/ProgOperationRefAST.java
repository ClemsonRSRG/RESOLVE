/**
 * ProgOperationRefAST.java
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
 * <p>A <code>ProgOperationRefAST</code> represents a reference within an
 * expression or subexpression to some operation.</p>
 *
 * <p>Realize that every call in the ast, including the 'official'
 * {@link edu.clemson.cs.r2jt.absynnew.stmt.CallAST}s should reference this
 * class. Ultimately even the primitive operations <code>+, -, *</code> get
 * converted into <code>ProgOperationRefAST</code>s
 * (by {@link edu.clemson.cs.r2jt.absynnew.TreeBuildingVisitor}) with names
 * appropriate for referencing their corresponding, formally specified template
 * operations.</p>
 */
public class ProgOperationRefAST extends ProgExprAST {

    private final Token myQualifier, myName;
    private final List<ProgExprAST> myArguments;

    public ProgOperationRefAST(Token start, Token stop, Token qualifier,
            Token name, List<ProgExprAST> arguments) {
        super(start, stop);
        myName = name;
        myQualifier = qualifier;
        myArguments = arguments;
    }

    public Token getName() {
        return myName;
    }

    public Token getQualifier() {
        return myQualifier;
    }

    public List<ProgExprAST> getArguments() {
        return myArguments;
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    public List<ProgExprAST> getSubExpressions() {
        return myArguments;
    }

    @Override
    public void setSubExpression(int index, ExprAST e) {
        myArguments.set(index, (ProgExprAST) e);
    }

    @Override
    public ProgOperationRefAST copy() {
        ProgOperationRefAST result =
                new ProgOperationRefAST(getStart(), getStop(), myQualifier,
                        myName, myArguments);

        result.setMathType(myMathType);
        result.setMathTypeValue(myMathTypeValue);
        return result;
    }

    @Override
    public ExprAST substituteChildren(Map<ExprAST, ExprAST> substitutions) {
        List<ProgExprAST> newArguments = new ArrayList<ProgExprAST>();

        for (ProgExprAST a : myArguments) {
            newArguments.add((ProgExprAST) substitute(a, substitutions));
        }
        return new ProgOperationRefAST(getStart(), getStop(), myQualifier,
                myName, newArguments);
    }
}