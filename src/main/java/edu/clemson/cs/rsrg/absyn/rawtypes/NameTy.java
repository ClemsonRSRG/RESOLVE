/*
 * NameTy.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.rawtypes;

import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the raw named type objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class NameTy extends Ty {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The raw type's qualifier</p> */
    private PosSymbol myQualifier;

    /** <p>The raw type's name</p> */
    private final PosSymbol myName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a raw name type.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qualifier A {@link PosSymbol} qualifier object.
     * @param name A {@link PosSymbol} name object.
     */
    public NameTy(Location l, PosSymbol qualifier, PosSymbol name) {
        super(l);
        myQualifier = qualifier;
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

        if (myQualifier != null) {
            sb.append(myQualifier.asString(0, innerIndentInc));
            sb.append("::");
        }
        sb.append(myName.asString(0, innerIndentInc));

        return sb.toString();
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
        if (!super.equals(o))
            return false;

        NameTy nameTy = (NameTy) o;

        if (myQualifier != null ? !myQualifier.equals(nameTy.myQualifier)
                : nameTy.myQualifier != null)
            return false;
        return myName.equals(nameTy.myName);

    }

    /**
     * <p>This method returns the name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public final PosSymbol getName() {
        return myName;
    }

    /**
     * <p>This method returns the qualifier name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public final PosSymbol getQualifier() {
        return myQualifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result =
                31 * result
                        + (myQualifier != null ? myQualifier.hashCode() : 0);
        result = 31 * result + myName.hashCode();
        return result;
    }

    /**
     * <p>Sets the qualifier for this raw type.</p>
     *
     * @param qualifier The {@link PosSymbol} representation object.
     */
    public final void setQualifier(PosSymbol qualifier) {
        myQualifier = qualifier;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Ty copy() {
        PosSymbol newQualifier = null;
        if (myQualifier != null) {
            newQualifier = myQualifier.clone();
        }
        PosSymbol newName = myName.clone();

        return new NameTy(cloneLocation(), newQualifier, newName);
    }

}