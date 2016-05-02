/**
 * MathTypeAST.java
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

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;

/**
 * A syntactic type based on an arbitrary mathematical {@link ExprAST}.  All
 * fields referencing a "math type" should be wrapped with this
 * {@code MathTypeAST}.  Ultimately their interfaces should be changed to
 * reflect this fact, or this class should be unwrapped and math types should
 * simply be represented by {@link ExprAST}s.
 *
 * Addendum: having an event in the walker indicating when we're within some
 * mathematical type's tree (essentially what this node buys us) has proven
 * useful in population.. unwrapping as posed above does not seem especially
 * helpful at this point.
 */
public final class MathTypeAST extends TypeAST {

    private final ExprAST myArbitraryTypeExpr;

    public MathTypeAST(ExprAST type) {
        super(type.getStart(), type.getStop());
        myArbitraryTypeExpr = type;
    }

    public ExprAST getUnderlyingExpr() {
        return myArbitraryTypeExpr;
    }

    @Override
    public MathTypeAST copy() {
        return new MathTypeAST(ExprAST.copy(myArbitraryTypeExpr));
    }

    @Override
    public String toString() {
        return myArbitraryTypeExpr.toString();
    }
}