/**
 * TypeParameterAST.java
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

import org.antlr.v4.runtime.Token;

/**
 * <p>A <code>TypeParameterAST</code> represents some generic type
 * parameterizing a module (e.g. <code>T</code>, <code>Entry</code>, etc)</p>.
 */
public class TypeParameterAST extends DeclAST {

    public TypeParameterAST(Token start, Token stop, Token name) {
        super(start, stop, name);
    }
}