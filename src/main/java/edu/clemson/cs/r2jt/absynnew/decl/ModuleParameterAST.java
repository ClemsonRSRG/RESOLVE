/**
 * ModuleParameterAST.java
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

/**
 * <p>A <code>ModuleParameterAST</code> can be anything ranging from a
 * generic type, to an operation, constant, or definition. This class simply
 * wraps the specific, base <code>DeclAST</code> representation
 * of each.</p>
 */
public class ModuleParameterAST extends DeclAST {

    private final DeclAST myPayload;

    public <T extends DeclAST> ModuleParameterAST(T payload) {
        super(payload.getStart(), payload.getStop(), payload.getName());
        myPayload = payload;
    }

    public DeclAST getPayload() {
        return myPayload;
    }
}
