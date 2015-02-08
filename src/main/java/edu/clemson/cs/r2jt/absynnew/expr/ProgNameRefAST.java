/**
 * ProgNameRefAST.java
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

public class ProgNameRefAST extends ProgExprAST {

    private final Token myQualifier, myName;

    public ProgNameRefAST(Token start, Token stop, Token qualifier, Token name) {
        super(start, stop);
        myName = name;
        myQualifier = qualifier;
    }

    public Token getQualifier() {
        return myQualifier;
    }

    public Token getName() {
        return myName;
    }

    @Override
    public List<ExprAST> getSubExpressions() {
        return Collections.emptyList();
    }

    @Override
    public void setSubExpression(int index, ExprAST e) {}

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    protected ExprAST substituteChildren(Map<ExprAST, ExprAST> substitutions) {
        return new ProgNameRefAST(getStart(), getStop(), getQualifier(),
                getName());
    }
}
