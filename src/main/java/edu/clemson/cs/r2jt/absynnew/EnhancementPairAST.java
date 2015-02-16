/**
 * EnhancementPairAST.java
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

import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class EnhancementPairAST extends ResolveAST {

    private final Token mySpecificationName, myBodyName;

    private final List<ModuleArgumentAST> mySpecificationArgs =
            new ArrayList<ModuleArgumentAST>();

    private final List<ModuleArgumentAST> myBodyArgs =
            new ArrayList<ModuleArgumentAST>();

    public EnhancementPairAST(Token start, Token stop, Token specName,
            Token bodyName) {
        super(start, stop);
        mySpecificationName = specName;
        myBodyName = bodyName;
    }

    public Token getSpecificationName() {
        return mySpecificationName;
    }

    public Token getBodyName() {
        return myBodyName;
    }

    public List<ModuleArgumentAST> getSpecificationArguments() {
        return mySpecificationArgs;
    }

    public List<ModuleArgumentAST> getBodyArguments() {
        return myBodyArgs;
    }
}
