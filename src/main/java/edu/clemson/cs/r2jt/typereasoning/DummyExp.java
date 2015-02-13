/**
 * DummyExp.java
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
package edu.clemson.cs.r2jt.typereasoning;

import java.util.Collections;
import java.util.Map;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualVisitor;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;

/**
 * <p>A <code>DummyExp</code> is an <code>Exp</code> guaranteed not to arise
 * from any actual RESOLVE source code.  Its only property is that it has an
 * <code>MTType</code>.  It can be bound normally to quantified variables whose
 * declared type it inhabits (i.e., a <code>DummyExp</code> of type 
 * <code>N</code> is acceptable for a quantified variable of type 
 * <code>Z</code>,) but nothing will bind to it.</p>
 * 
 * <p>Mostly useful for representing "a unique variable of type X" without 
 * having to worry if its name is truly unique.</p>
 */
public class DummyExp extends Exp {

    public DummyExp(MTType t) {
        myMathType = t;
    }

    public DummyExp(DummyExp original) {
        myMathType = original.myMathType;
    }

    @Override
    public void accept(ResolveConceptualVisitor v) {
        throw new UnsupportedOperationException("Cannot visit a DummyExp.");
    }

    @Override
    public String asString(int indent, int increment) {
        return "" + this.getClass();
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Exp> getSubExpressions() {
        return (List<Exp>) Collections.EMPTY_LIST;
    }

    @Override
    public void setSubExpression(int index, Exp e) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    protected Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new DummyExp(this);
    }

    @Override
    public String toString() {
        return "(some " + getMathType() + ")";
    }
}
