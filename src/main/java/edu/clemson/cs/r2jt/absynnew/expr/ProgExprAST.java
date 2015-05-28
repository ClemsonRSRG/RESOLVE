/**
 * ProgExprAST.java
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
package edu.clemson.cs.r2jt.absynnew.expr;

import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import org.antlr.v4.runtime.Token;

public abstract class ProgExprAST extends ExprAST {

    private PTType myProgramType;

    public ProgExprAST(Token start, Token stop) {
        super(start, stop);
    }

    public void setProgramType(PTType type) {
        if (type == null) {
            throw new IllegalArgumentException(
                    "attempt to set program type to null");
        }
        myProgramType = type;
    }

    public PTType getProgramType() {
        return myProgramType;
    }
}