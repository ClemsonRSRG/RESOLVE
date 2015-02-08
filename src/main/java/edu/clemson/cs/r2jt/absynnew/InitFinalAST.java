/**
 * InitFinalAST.java
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

import edu.clemson.cs.r2jt.absynnew.decl.VariableAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.absynnew.stmt.StmtAST;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>An <code>InitFinalAST</code> provides a general scope for
 * users for specifications and code that deals with module (or type level)
 * initialization or finalization.</p>
 */
public abstract class InitFinalAST extends ResolveAST {

    private final ExprAST myRequires, myEnsures;

    private final List<VariableAST> myVariables;
    private final List<StmtAST> myStatements;

    public InitFinalAST(Token start, Token stop, ExprAST requires,
            ExprAST ensures, List<VariableAST> vars, List<StmtAST> stmts) {
        super(start, stop);
        myRequires = requires;
        myEnsures = ensures;

        myVariables = vars;
        myStatements = stmts;
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

    public static class TypeInitAST extends InitFinalAST {

        public TypeInitAST(Token start, Token stop, ExprAST req, ExprAST ens) {
            super(start, stop, req, ens, new ArrayList<VariableAST>(),
                    new ArrayList<StmtAST>());
        }
    }

    public static class TypeFinalAST extends InitFinalAST {

        public TypeFinalAST(Token start, Token stop, ExprAST req, ExprAST ens) {
            super(start, stop, req, ens, new ArrayList<VariableAST>(),
                    new ArrayList<StmtAST>());
        }
    }

    public static class ModuleInitAST extends InitFinalAST {

        public ModuleInitAST(Token start, Token stop, ExprAST requires,
                ExprAST ensures, List<VariableAST> vars, List<StmtAST> stmts) {
            super(start, stop, requires, ensures, vars, stmts);
        }
    }

    public static class ModuleFinalAST extends InitFinalAST {

        public ModuleFinalAST(Token start, Token stop, ExprAST requires,
                ExprAST ensures, List<VariableAST> vars, List<StmtAST> stmts) {
            super(start, stop, requires, ensures, vars, stmts);
        }
    }
}