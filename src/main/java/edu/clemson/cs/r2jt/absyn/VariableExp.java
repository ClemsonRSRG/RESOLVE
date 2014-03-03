/**
 * VariableExp.java
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

import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;

public abstract class VariableExp extends ProgramExp {

    public abstract void accept(ResolveConceptualVisitor v);

    public abstract Type accept(TypeResolutionVisitor v)
            throws TypeResolutionException;

    public abstract String asString(int indent, int increment);

    public abstract String toString(int indent);

    /** Should be overridden by inheritors of this class. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

}
