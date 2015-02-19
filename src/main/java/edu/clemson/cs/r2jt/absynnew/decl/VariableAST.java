/**
 * VariableAST.java
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
import org.antlr.v4.runtime.Token;

public class VariableAST extends DeclAST {

    private final NamedTypeAST myType;

    public VariableAST(Token start, Token stop, Token name, NamedTypeAST type) {
        super(start, stop, name);
        myType = type;
    }

    public NamedTypeAST getType() {
        return myType;
    }
}