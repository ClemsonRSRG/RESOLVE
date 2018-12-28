/*
 * StructureExp.java
 * ---------------------------------
 * Copyright (c) 2019
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
 * <p>A <code>StructureExp</code> is simply a marker that you can build into
 * a <code>ResolveConceptualElement</code> so that walkers can get the heads up
 * about something.  For example, two structure expressions exist called
 * PROGRAM_WORLD_START and PROGRAM_WORLD_END.  Since the tree-walker walks the
 * fields of a node in order, one could create a field containing 
 * PROGRAM_WORLD_START before any legitimate fields that should be considered
 * programmatic (rather than mathematical) and PROGRAM_WORLD_END afterward.
 * Walkers will then get a <code>pre/postStructureExp</code> call before walking
 * the legitimate fields.</p>
 */
public class StructureExp extends Exp {

    public static final StructureExp PROGRAM_WORLD_START = new StructureExp();
    public static final StructureExp PROGRAM_WORLD_END = new StructureExp();

    private StructureExp() {

    }

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
