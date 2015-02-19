/**
 * NamedTypeAST.java
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

import edu.clemson.cs.r2jt.parsing.ResolveParser;
import org.antlr.v4.runtime.Token;

/**
 * <p>A <code>NamedTypeAST</code> refers to any type represented by a
 * possibly qualified identifier/name.</p>
 */
public final class NamedTypeAST extends TypeAST {

    private final Token myQualifier, myName;

    public NamedTypeAST(Token start, Token stop, Token qualifier, Token name) {
        super(start, stop);
        myQualifier = qualifier;
        myName = name;
    }

    public NamedTypeAST(ResolveParser.TypeContext ctx) {
        this(ctx.getStart(), ctx.getStop(), ctx.qualifier, ctx.name);
    }

    public Token getName() {
        return myName;
    }

    public Token getQualifier() {
        return myQualifier;
    }
}