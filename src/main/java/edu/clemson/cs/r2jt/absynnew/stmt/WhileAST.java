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

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.absynnew.expr.ProgExprAST;
import edu.clemson.cs.r2jt.absynnew.expr.ProgNameRefAST;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class WhileAST extends StmtAST {

    private final ProgExprAST myCondition;

    /**
     * <p>The following variables taken together allow loops to be annotated
     * with an invariant.</p>
     */
    private final List<ProgExprAST> myChangingVariables =
            new ArrayList<ProgExprAST>();
    private final ExprAST myMaintaining, myDecreasing;

    private final List<StmtAST> myStatements = new ArrayList<StmtAST>();

    public WhileAST(Token start, Token stop, ProgExprAST condition,
            List<ProgExprAST> changingVars, ExprAST maintaining,
            ExprAST decreasing, List<StmtAST> statements) {
        super(start, stop);

        myCondition = condition;
        myMaintaining = maintaining;
        myDecreasing = decreasing;
        myChangingVariables.addAll(changingVars);
        myStatements.addAll(statements);
    }

    public ProgExprAST getCondition() {
        return myCondition;
    }

    public List<ProgExprAST> getChangingVariables() {
        return myChangingVariables;
    }

    public ExprAST getMaintainingClause() {
        return myMaintaining;
    }

    public ExprAST getDecreasingClause() {
        return myDecreasing;
    }

    public String toString() {
        String result = "While " + myCondition + "";
        return result;
    }
}
