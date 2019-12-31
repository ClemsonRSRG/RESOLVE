/*
 * ModuleDec.java
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

import edu.clemson.cs.r2jt.collections.List;

public abstract class ModuleDec extends Dec {

    /** The usesItems member. */
    protected List<UsesItem> usesItems;

    public abstract void accept(ResolveConceptualVisitor v);

    public abstract String asString(int indent, int increment);

    /** Returns the value of the usesItems variable. */
    public List<UsesItem> getUsesItems() {
        return usesItems;
    }

    /** Sets the usesItems variable to the specified value. */
    public void setUsesItems(List<UsesItem> usesItems) {
        this.usesItems = usesItems;
    }

}
