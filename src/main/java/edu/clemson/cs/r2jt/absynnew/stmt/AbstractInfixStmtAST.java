/**
 * AbstractInfixStmtAST.java
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
package edu.clemson.cs.r2jt.absynnew.stmt;

import edu.clemson.cs.r2jt.absynnew.expr.ProgExprAST;
import org.antlr.v4.runtime.Token;

public abstract class AbstractInfixStmtAST extends StmtAST {

    private final ProgExprAST myLeft, myRight;

    public AbstractInfixStmtAST(Token start, Token stop, ProgExprAST left,
            ProgExprAST right) {
        super(start, stop);
        myLeft = left;
        myRight = right;
    }

    public ProgExprAST getLeft() {
        return myLeft;
    }

    public ProgExprAST getRight() {
        return myRight;
    }

    public abstract String getOperand();

    @Override
    public String toString() {
        return myLeft + " " + getOperand() + " " + myRight + ";";
    }
}
