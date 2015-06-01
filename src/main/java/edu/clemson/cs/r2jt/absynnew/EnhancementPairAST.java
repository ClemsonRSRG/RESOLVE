/**
 * EnhancementPairAST.java
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
package edu.clemson.cs.r2jt.absynnew;

import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@code EnhancementPairAST} pairs a possibly parameterized
 * specification with a possibly parameterized implementation.
 */
public class EnhancementPairAST extends ResolveAST {

    private final Token mySpecificationName, myBodyName;

    private final List<ModuleArgumentAST> mySpecArgs =
            new ArrayList<ModuleArgumentAST>();

    private final List<ModuleArgumentAST> myBodyArgs =
            new ArrayList<ModuleArgumentAST>();

    public EnhancementPairAST(Token start, Token stop, Token specName,
            List<ModuleArgumentAST> specArgs, Token bodyName,
            List<ModuleArgumentAST> bodyArgs) {
        super(start, stop);
        mySpecificationName = specName;
        myBodyName = bodyName;
        mySpecArgs.addAll(specArgs);
        myBodyArgs.addAll(bodyArgs);
    }

    public Token getSpecName() {
        return mySpecificationName;
    }

    public Token getImplName() {
        return myBodyName;
    }

    public List<ModuleArgumentAST> getSpecArguments() {
        return mySpecArgs;
    }

    public List<ModuleArgumentAST> getImplArguments() {
        return myBodyArgs;
    }
}
