/*
 * Ty.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;

/**
 * <p>
 * A <code>Ty</code> represents the <em>description</em> of a <code>Type</code>,
 * as it is found in
 * the RESOLVE source code. That is, it is representation of a type in the
 * abstract syntax tree
 * before it is translated into a true <code>Type</code>.
 * </p>
 * 
 * <p>
 * It can be converted into a <code>Type</code> by a type.TypeConverter.
 * </p>
 */
public abstract class Ty extends ResolveConceptualElement implements Cloneable {

    protected MTType myMathType = null;
    protected MTType myMathTypeValue = null;
    protected PTType myProgramTypeValue = null;

    public abstract void accept(ResolveConceptualVisitor v);

    public abstract String asString(int indent, int increment);

    public MTType getMathType() {
        return myMathType;
    }

    public void setMathType(MTType t) {
        this.myMathType = t;
    }

    public MTType getMathTypeValue() {
        return myMathTypeValue;
    }

    public void setMathTypeValue(MTType mathTypeValue) {
        this.myMathTypeValue = mathTypeValue;
    }

    public PTType getProgramTypeValue() {
        return myProgramTypeValue;
    }

    public void setProgramTypeValue(PTType programTypeValue) {
        myProgramTypeValue = programTypeValue;
    }

    public static Ty copy(Ty t) {
        MTType mathType = t.getMathType();
        MTType mathTypeValue = t.getMathTypeValue();

        Ty result = t.copy();

        result.setMathType(mathType);
        result.setMathTypeValue(mathTypeValue);

        return result;
    }

    protected Ty copy() {
        throw new RuntimeException(
                "Shouldn't be calling Ty.copy()!  Type: " + this.getClass());
    }

    public void prettyPrint() {
        System.out.println("Shouldn't be calling Ty.prettyPrint()!");
    }

    public String toString(int indent) {
        return this.asString(0, 0);
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError("But we are Cloneable!!!");
        }

    }
}
