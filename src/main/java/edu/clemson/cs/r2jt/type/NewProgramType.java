/**
 * NewProgramType.java
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.type;

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ScopeID;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;

/**
 *
 * @author hamptos
 */
public class NewProgramType extends Type {

    private final PTType myPTType;

    public NewProgramType(PTType mtType) {
        myPTType = mtType;
    }

    public PTType getWrappedType() {
        return myPTType;
    }

    @Override
    public NewProgramType instantiate(ScopeID sid, Binding binding) {
        return this;
    }

    @Override
    public TypeName getProgramName() {
        throw new UnsupportedOperationException(
                "NewType doesn't support this operation.");
    }

    @Override
    public String getRelativeName(Location loc) {
        throw new UnsupportedOperationException(
                "NewType doesn't support this operation.");
    }

    @Override
    public NewMathType toMath() {
        return new NewMathType(myPTType.toMath());
    }

    @Override
    public String asString() {
        return "" + myPTType;
    }

    @Override
    public String toString() {
        return "" + myPTType;
    }
}
