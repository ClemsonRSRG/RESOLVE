/**
 * OperationAST.java
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
package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.NamedTypeAST;
import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import org.antlr.v4.runtime.Token;

import java.util.List;

/**
 * <p>The base class for all 'operation-like-constructs' in <tt>RESOLVE</tt>.
 * For operations arising in more specific contexts,</p>
 * 
 * @see edu.clemson.cs.r2jt.absynnew.decl.OperationImplAST
 * @see edu.clemson.cs.r2jt.absynnew.decl.OperationSigAST
 */
public abstract class OperationAST extends DeclAST {

    protected final List<ParameterAST> myParameters;
    protected final NamedTypeAST myReturnType;

    protected final ExprAST myRequires, myEnsures;

    protected OperationAST(Token start, Token stop, Token name,
            List<ParameterAST> params, NamedTypeAST type, ExprAST requires,
            ExprAST ensures) {
        super(start, stop, name);
        myParameters = params;

        myReturnType = type;
        myRequires = requires;
        myEnsures = ensures;
    }

    public List<ParameterAST> getParameters() {
        return myParameters;
    }

    public ExprAST getRequires() {
        return myRequires;
    }

    public ExprAST getEnsures() {
        return myEnsures;
    }

    public NamedTypeAST getReturnType() {
        return myReturnType;
    }
}