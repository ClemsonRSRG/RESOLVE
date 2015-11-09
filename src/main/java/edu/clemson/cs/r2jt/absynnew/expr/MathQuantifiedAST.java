/**
 * MathQuantifiedAST.java
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

import edu.clemson.cs.r2jt.absynnew.decl.MathVariableAST;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A {@code MathQuantifiedAST} encapsulates a mathematical assertion
 * whose members and subexpressions are bound to some number of quantified
 * {@link MathVariableAST}s.
 */
public class MathQuantifiedAST extends ExprAST {

    private SymbolTableEntry.Quantification myQuantification;
    private List<MathVariableAST> myQuantifiedVariables =
            new ArrayList<MathVariableAST>();

    private ExprAST myWhere, myAssertion;

    public MathQuantifiedAST(Token start, Token stop,
            SymbolTableEntry.Quantification q, List<MathVariableAST> vars,
            ExprAST where, ExprAST assertion) {
        super(start, stop);
        myQuantification = q;
        myQuantifiedVariables = vars;
        myWhere = where;
        myAssertion = assertion;
    }

    public SymbolTableEntry.Quantification getQuantification() {
        return myQuantification;
    }

    public ExprAST getAssertion() {
        return myAssertion;
    }

    public ExprAST getWhere() {
        return myWhere;
    }

    public List<MathVariableAST> getQuantifiedVariables() {
        return myQuantifiedVariables;
    }

    @Override
    public boolean isLiteral() {
        return false;
    }

    @Override
    public List<ExprAST> getSubExpressions() {
        List<ExprAST> result = new ArrayList<ExprAST>();
        result.add(myAssertion);
        result.add(myWhere);

        return result;
    }

    @Override
    public void setSubExpression(int index, ExprAST e) {
        if (index == 0) {
            myWhere = e;
        }
        else {
            myAssertion = e;
        }
    }

    @Override
    public ExprAST substituteChildren(Map<ExprAST, ExprAST> substitutions) {
        return new MathQuantifiedAST(getStart(), getStop(), myQuantification,
                myQuantifiedVariables, substitute(myWhere, substitutions),
                substitute(myAssertion, substitutions));
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder().append("For all ");

        for (MathVariableAST universalVar : myQuantifiedVariables) {
            result.append(universalVar).append(", ");
        }
        if (myWhere != null) {
            result.append(myWhere);
        }
        return result.append(myAssertion).toString();
    }
}
