/**
 * MathTheoremAST.java
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
package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import org.antlr.v4.runtime.Token;

/**
 * Represents a mathematical theorem or corollary, as would be found within
 * the body of an {@link edu.clemson.cs.r2jt.absynnew.ModuleAST.PrecisAST}.
 */
public class MathTheoremAST extends DeclAST {

    private final ExprAST myAssertion;

    public MathTheoremAST(Token start, Token stop, Token name, ExprAST assertion) {
        super(start, stop, name);
        myAssertion = assertion;
    }

    public ExprAST getAssertion() {
        return myAssertion;
    }
}