/**
 * ConfirmAST.java
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
package edu.clemson.cs.r2jt.absynnew.stmt;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import org.antlr.v4.runtime.Token;

public class ConfirmAST extends StmtAST {

    private final ExprAST myAssertion;

    public ConfirmAST(Token start, Token stop, ExprAST assertion) {
        super(start, stop);
        myAssertion = assertion;
    }

    public ExprAST getAssertion() {
        return myAssertion;
    }

    @Override
    public String toString() {
        return "confirm " + myAssertion + ";";
    }
}
