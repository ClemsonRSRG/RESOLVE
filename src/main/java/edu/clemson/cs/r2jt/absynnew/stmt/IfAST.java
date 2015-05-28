/**
 * IfAST.java
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
package edu.clemson.cs.r2jt.absynnew.stmt;

import edu.clemson.cs.r2jt.absynnew.expr.ProgExprAST;
import edu.emory.mathcs.backport.java.util.Collections;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class IfAST extends StmtAST {

    private final ProgExprAST myCondition;
    private List<StmtAST> myThenBlock, myElseBlock;

    public IfAST(Token start, Token stop, ProgExprAST condition,
            List<StmtAST> ifThenBlock, List<StmtAST> elseBlock) {
        super(start, stop);
        myCondition = condition;

        if (elseBlock == null) {
            ifThenBlock = new ArrayList<StmtAST>();
        }
        myThenBlock = ifThenBlock;

        if (elseBlock == null) {
            elseBlock = new ArrayList<StmtAST>();
        }
        myElseBlock = elseBlock;
    }

    public ProgExprAST getCondition() {
        return myCondition;
    }

    public List<StmtAST> getThenBlock() {
        return myThenBlock;
    }

    public List<StmtAST> getElseBlock() {
        return myElseBlock;
    }

    @Override
    public String toString() {
        String result = "If " + myCondition + " then ";
        for (StmtAST stmt : myThenBlock) {
            result += stmt;
        }
        if (!myElseBlock.isEmpty()) {
            result += " else ";
            for (StmtAST stmt : myElseBlock) {
                result += stmt;
            }
        }
        else {
            result += " end;";
        }
        return result;
    }
}
