/**
 * MathLambdaAST.java
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
package edu.clemson.cs.r2jt.absynnew.expr;

import edu.clemson.cs.r2jt.absyn.MathVarDec;
import edu.clemson.cs.r2jt.absynnew.TreeUtil;
import edu.clemson.cs.r2jt.absynnew.decl.MathVariableAST;
import edu.clemson.cs.r2jt.typeandpopulate2.MTFunction;
import org.antlr.v4.runtime.Token;
import org.stringtemplate.v4.ST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MathLambdaAST extends ExprAST {

    private final List<MathVariableAST> myParameters;
    private ExprAST myBody;

    public MathLambdaAST(Token start, Token stop,
            List<MathVariableAST> parameters, ExprAST body) {
        super(start, stop);

        if (parameters == null) {
            throw new IllegalArgumentException("null MathLambdaAST params");
        }
        myParameters = parameters;
        myBody = body;
    }

    public ExprAST getBody() {
        return myBody;
    }

    public List<MathVariableAST> getParameters() {
        return myParameters;
    }

    @Override
    public List<? extends ExprAST> getSubExpressions() {
        return Collections.singletonList(myBody);
    }

    @Override
    public void setSubExpression(int index, ExprAST e) {
        myBody = e;
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    protected ExprAST substituteChildren(Map<ExprAST, ExprAST> substitutions) {
        return new MathLambdaAST(getStart(), getStop(),
                new ArrayList<MathVariableAST>(myParameters), substitute(
                        myBody, substitutions));
    }

    @Override
    public String toString() {
        return "lambda(" + TreeUtil.join(myParameters, ", ") + ").(" + myBody
                + ")";
    }

    @Override
    public MTFunction getMathType() {
        return (MTFunction) super.getMathType();
    }
}
