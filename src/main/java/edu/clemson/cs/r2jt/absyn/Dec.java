/*
 * Dec.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;

public abstract class Dec extends ResolveConceptualElement implements Cloneable {

    protected MTType myMathType = null;

    //protected MTType myMathTypeValue = null;

    public abstract void accept(ResolveConceptualVisitor v);

    public abstract String asString(int indent, int increment);

    public abstract PosSymbol getName();

    public String toString(int indent) {
        return new String();
    }

    public Location getLocation() {
        return getName().getLocation();
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError("But we are Cloneable!!!");
        }
    }

    public MTType getMathType() {
        return myMathType;
    }

    public void setMathType(MTType mt) {
        if (mt == null) {
            throw new RuntimeException("Trying to set null type on "
                    + this.getClass());
        }

        this.myMathType = mt;
    }
    //	public MTType getMathTypeValue() {
    //		return myMathTypeValue;
    //	}
    //	public void setMathTypeValue(MTType mathTypeValue) {
    //		this.myMathTypeValue = mathTypeValue;
    //	}
}
