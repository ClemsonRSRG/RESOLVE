/**
 * CallAST.java
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

import edu.clemson.cs.r2jt.absynnew.expr.ProgOperationRefAST;

public class CallAST extends StmtAST {

    private final ProgOperationRefAST myOpReferenceExpr;

    public CallAST(ProgOperationRefAST expr) {
        super(expr.getStart(), expr.getStop());
        myOpReferenceExpr = expr;
    }

    public ProgOperationRefAST getWrappedOpReferenceExpr() {
        return myOpReferenceExpr;
    }

    @Override
    public String toString() {
        return myOpReferenceExpr + ";";
    }
}
