/*
 * FunctionValueExp.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import java.util.Map;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;

/**
 * <p>
 * A <code>FunctionValueExp</code> represents
 * </p>
 */
public class FunctionValueExp extends Exp {

    @Override
    public void accept(ResolveConceptualVisitor v) {
        // TODO Auto-generated method stub

    }

    @Override
    public String asString(int indent, int increment) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location getLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Exp> getSubExpressions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSubExpression(int index, Exp e) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Exp substituteChildren(Map<Exp, Exp> substitutions) {
        // TODO Auto-generated method stub
        return null;
    }

}
