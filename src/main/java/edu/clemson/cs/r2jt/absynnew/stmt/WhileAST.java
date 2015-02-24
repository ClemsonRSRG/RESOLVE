/**
 * WhileAST.java
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

import org.antlr.v4.runtime.Token;

public class WhileAST extends StmtAST {

    public WhileAST(Token start, Token stop) {
        super(start, stop);
    }
}
