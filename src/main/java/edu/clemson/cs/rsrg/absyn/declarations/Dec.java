/*
 * Dec.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.declarations;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;

/**
 * <p>This is the abstract base class for all the declaration objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public abstract class Dec extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The object's mathematical type.</p> */
    protected MTType myMathType = null;

    /** <p>The object's name representation.</p> */
    protected final PosSymbol myName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the location
     * and name of any objects created from a class that inherits
     * from {@code Dec}.</p>
     *
     * @param l A {@link Location} representation object.
     * @param name The name in {@link PosSymbol} format.
     */
    protected Dec(Location l, PosSymbol name) {
        super(l);
        myName = name;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default clone method implementation
     * for all the classes that extend from {@link Dec}.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final Dec clone() {
        Dec result = this.copy();
        result.setMathType(myMathType);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Dec dec = (Dec) o;

        if (myMathType != null ? !myMathType.equals(dec.myMathType)
                : dec.myMathType != null)
            return false;
        return myName.equals(dec.myName);
    }

    /**
     * <p>This method gets the mathematical type associated
     * with this object.</p>
     *
     * @return The {@link MTType} type object.
     */
    public final MTType getMathType() {
        return myMathType;
    }

    /**
     * <p>Returns the symbol representation
     * of this class.</p>
     *
     * @return The name in {@link PosSymbol} format.
     */
    public PosSymbol getName() {
        return myName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = myMathType != null ? myMathType.hashCode() : 0;
        result = 31 * result + myName.hashCode();
        return result;
    }

    /**
     * <p>This method sets the mathematical type associated
     * with this object.</p>
     *
     * @param mt The {@link MTType} type object.
     */
    public final void setMathType(MTType mt) {
        myMathType = mt;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by concrete subclasses of {@link Dec}
     * to manufacture a copy of themselves.</p>
     *
     * @return A new {@link Dec} that is a deep copy of the original.
     */
    protected Dec copy() {
        throw new MiscErrorException("Shouldn't be calling copy()!  Type: "
                + this.getClass(), new CloneNotSupportedException());
    }

}