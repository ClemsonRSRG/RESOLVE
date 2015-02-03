/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;

/**
 * <p>A <code>Ty</code> represents the <em>description</em> of a 
 * <code>Type</code>, as it is found in the RESOLVE source code.  That is, it is
 * representation of a type in the abstract syntax tree before it is translated 
 * into a true <code>Type</code>.</p>
 * 
 * <p>It can be converted into a <code>Type</code> by a type.TypeConverter.</p>
 */
public abstract class Ty extends ResolveConceptualElement implements Cloneable {

    protected MTType myMathType = null;
    protected MTType myMathTypeValue = null;
    protected PTType myProgramTypeValue = null;

    public abstract void accept(ResolveConceptualVisitor v);

    public abstract Type accept(TypeResolutionVisitor v)
            throws TypeResolutionException;

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
        throw new RuntimeException("Shouldn't be calling Ty.copy()!  Type: "
                + this.getClass());
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
