/**
 * ModuleArgumentAST.java
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
package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import org.antlr.v4.runtime.Token;

public class ModuleArgumentAST extends ResolveAST {

    private final ExprAST myArgumentExpr;

    private MTType myMathType;
    private PTType myProgramTypeValue;

    public ModuleArgumentAST(Token start, Token stop, ExprAST expr) {
        super(start, stop);
        myArgumentExpr = expr;
    }

    public ModuleArgumentAST(ExprAST expr) {
        this(expr.getStart(), expr.getStop(), expr);
    }

    public ExprAST getArgumentExpr() {
        return myArgumentExpr;
    }

    public PTType getProgramTypeValue() {
        return myProgramTypeValue;
    }

    public MTType getMathType() {
        return myMathType;
    }

    public void setProgramTypeValue(PTType t) {
        myProgramTypeValue = t;
    }

    public void setMathType(MTType t) {
        myMathType = t;
    }

}
