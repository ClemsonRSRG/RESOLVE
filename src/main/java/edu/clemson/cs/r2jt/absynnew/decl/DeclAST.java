/**
 * DeclAST.java
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
package edu.clemson.cs.r2jt.absynnew.decl;

import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import org.antlr.v4.runtime.Token;

public abstract class DeclAST extends ResolveAST {

    private final Token myName;
    protected MTType myMathType = null;

    public DeclAST(Token start, Token stop, Token name) {
        super(start, stop);
        myName = name;
    }

    /**
     * Returns the {@link MTType} for this {@code DeclAST}.
     * @return
     */
    public MTType getMathType() {
        return myMathType;
    }

    public void setMathType(MTType mt) {
        if (mt == null) {
            throw new RuntimeException("trying to set null type on "
                    + this.getClass().getSimpleName());
        }
        myMathType = mt;
    }

    /**
     * Returns the name of this {@code DeclAST}.
     *
     * @return The {@link Token} containing the name of this construct.
     */
    public Token getName() {
        return myName;
    }

    @Override
    public String toString() {
        if (getStart() == null || getStop() == null) {
            return this.getClass().getSimpleName() + "@-1:-1{name=" + getName()
                    + "}";
        }
        return this.getClass().getSimpleName() + "@" + getStart().getLine()
                + ":" + getStart().getCharPositionInLine() + "{" + getName()
                + "}";
    }
}
