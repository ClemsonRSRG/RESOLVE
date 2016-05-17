/**
 * InitFinalAST.java
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
package edu.clemson.cs.r2jt.absynnew;

import edu.clemson.cs.r2jt.absynnew.decl.VariableAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.absynnew.stmt.StmtAST;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@code InitFinalAST} provides a general scope for specifications
 * and code that deals with module (or type level) initialization or
 * finalization.
 */
public class InitFinalAST extends ResolveAST {

    public static enum Type {
        TYPE_INIT, TYPE_FINAL, MODULE_INIT, MODULE_FINAL
    }

    private final ExprAST myRequires, myEnsures;

    private final List<VariableAST> myVariables = new ArrayList<VariableAST>();
    private final List<StmtAST> myStatements = new ArrayList<StmtAST>();

    private final Type myType;

    public InitFinalAST(Token start, Token stop, ExprAST requires,
            ExprAST ensures, List<VariableAST> vars, List<StmtAST> stmts, Type t) {
        super(start, stop);
        myRequires = requires;
        myEnsures = ensures;

        myVariables.addAll(vars);
        myStatements.addAll(stmts);
        myType = t;
    }

    public InitFinalAST(Token start, Token stop, ExprAST requires,
            ExprAST ensures, Type t) {
        this(start, stop, requires, ensures, new ArrayList<VariableAST>(),
                new ArrayList<StmtAST>(), t);
    }

    public InitFinalAST(Token start, Token stop, Type t) {
        this(start, stop, null, null, t);
    }

    public Type getType() {
        return myType;
    }

    public ExprAST getRequires() {
        return myRequires;
    }

    public ExprAST getEnsures() {
        return myEnsures;
    }

    public List<VariableAST> getVariables() {
        return myVariables;
    }

    public List<StmtAST> getStatements() {
        return myStatements;
    }

}