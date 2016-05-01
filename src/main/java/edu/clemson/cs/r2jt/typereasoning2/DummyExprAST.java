/**
 * DummyExprAST.java
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
package edu.clemson.cs.r2jt.typereasoning2;

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>A <code>DummyExp</code> is an <code>Exp</code> guaranteed not to arise
 * from any actual RESOLVE source code.  Its only property is that it has an
 * <code>MTType</code>.  It can be bound normally to quantified variables whose
 * declared type it inhabits (i.e., a <code>DummyExp</code> of type 
 * <code>N</code> is acceptable for a quantified variable of type 
 * <code>Z</code>,) but nothing will bind to it.</p>
 * 
 * <p>Mostly useful for representing "a unique variable of type X" without 
 * having to worry if its name is truly unique.</p>
 */
public class DummyExprAST extends ExprAST {

    public DummyExprAST(MTType t) {
        super(null, null);
        myMathType = t;
    }

    public DummyExprAST(DummyExprAST original) {
        super(null, null);
        myMathType = original.myMathType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ExprAST> getSubExpressions() {
        return (List<ExprAST>) Collections.EMPTY_LIST;
    }

    @Override
    public void setSubExpression(int index, ExprAST e) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    protected ExprAST substituteChildren(Map<ExprAST, ExprAST> substitutions) {
        return new DummyExprAST(this);
    }

    @Override
    public String toString() {
        return "(some " + getMathType() + ")";
    }
}
