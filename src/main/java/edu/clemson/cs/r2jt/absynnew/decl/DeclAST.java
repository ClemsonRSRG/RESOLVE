/**
 * DeclAST.java
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
package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import org.antlr.v4.runtime.Token;

public abstract class DeclAST extends ResolveAST {

    private final Token myName;
    protected MTType myMathType = null;

    public DeclAST(Token start, Token stop, Token name) {
        super(start, stop);
        myName = name;
    }

    public MTType getMathType() {
        return myMathType;
    }

    public void setMathType(MTType mt) {
        if (mt == null) {
            throw new RuntimeException("trying to set null type on "
                    + this.getClass().getSimpleName());
        }
        myMathType = mt;
    }

    public Token getName() {
        return myName;
    }
}
