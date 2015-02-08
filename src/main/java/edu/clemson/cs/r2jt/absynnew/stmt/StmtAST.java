/**
 * StmtAST.java
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

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import org.antlr.v4.runtime.Token;

public abstract class StmtAST<D extends ResolveAST> extends ResolveAST {

    public StmtAST(Token start, Token stop) {
        super(start, stop);
    }
}
