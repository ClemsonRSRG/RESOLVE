/**
 * UsesItem.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.declarations.moduledecl;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class that functions as a reference to the name of the
 * imported module used by a module the compiler is currently compiling.</p>
 *
 * @version 2.0
 */
public class UsesItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Name of this imported module.</p> */
    private final PosSymbol myName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Constructs an item that represents a imported module.</p>
     *
     * @param name Name of the imported module.
     */
    public UsesItem(PosSymbol name) {
        super(name.getLocation());
        myName = name;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append(myName.asString(indentSize + innerIndentInc, innerIndentInc));

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final UsesItem clone() {
        return new UsesItem(myName.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UsesItem usesItem = (UsesItem) o;

        return myName.equals(usesItem.myName);

    }

    /**
     * <p>Returns the symbol representation of this class.</p>
     *
     * @return A {@link PosSymbol} representation of the name.
     */
    public final PosSymbol getName() {
        return myName.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return myName.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return myName.toString();
    }

}