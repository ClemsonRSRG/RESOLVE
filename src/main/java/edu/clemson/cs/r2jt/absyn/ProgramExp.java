/*
 * ProgramExp.java
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

import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;

public abstract class ProgramExp extends Exp {

    private PTType myProgramType;

    public abstract void accept(ResolveConceptualVisitor v);

    public abstract String asString(int indent, int increment);

    public abstract String toString(int indent);

    /** Should be overridden by classes extending ProgramExp. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    public void setProgramType(PTType type) {
        if (type == null) {
            throw new IllegalArgumentException(
                    "Attempt to set program type to null.");
        }

        myProgramType = type;
    }

    public PTType getProgramType() {
        return myProgramType;
    }
}
