/**
 * Statement.java
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

import edu.clemson.cs.r2jt.data.Location;

public abstract class Statement extends ResolveConceptualElement
        implements
            Cloneable {

    public abstract void accept(ResolveConceptualVisitor v);

    public abstract String asString(int indent, int increment);

    public abstract Location getLocation();

    public String toString(int indent) {
        return new String();
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
