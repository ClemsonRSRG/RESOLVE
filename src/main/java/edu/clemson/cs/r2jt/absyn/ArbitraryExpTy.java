/**
 * ArbitraryExpTy.java
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
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.type.Type;

/**
 * <p>A syntactic type based on an arbitrary mathematical <code>Exp</code>.  All
 * math expressions should have this <code>Ty</code>.  Ultimately their 
 * interfaces should be changed to reflect this fact, or this class should
 * be unwrapped and math types should simply be represented by 
 * <code>Exp</code>s.</p>
 */
public class ArbitraryExpTy extends Ty {

    private final Exp myArbitraryExp;

    public ArbitraryExpTy(Exp arbitraryExp) {
        myArbitraryExp = arbitraryExp;
    }

    public Exp getArbitraryExp() {
        return myArbitraryExp;
    }

    @Override
    public void accept(ResolveConceptualVisitor v) {
        v.visitArbitraryExpTy(this);
    }

    @Override
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        return v.getArbitraryExpType(this);
    }

    @Override
    public String asString(int indent, int increment) {
        return myArbitraryExp.toString();
    }

    @Override
    public String toString() {
        return myArbitraryExp.toString();
    }

    public Location getLocation() {
        return myArbitraryExp.getLocation();
    }

    @Override
    public ArbitraryExpTy copy() {
        return new ArbitraryExpTy(Exp.copy(myArbitraryExp));
    }
}
