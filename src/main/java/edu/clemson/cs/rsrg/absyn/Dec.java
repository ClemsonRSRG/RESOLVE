/**
 * Dec.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn;

import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.rsrg.errorhandling.exception.NullMathTypeException;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the abstract base class for all the declaration type
 * intermediate objects that the compiler builds from the ANTLR4 AST tree.</p>
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
     * <p>A helper constructor that allow us to store the location
     * of the created object directly in the this class.</p>
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
     * <p>This method must be implemented by all inherited classes
     * to override the default clone method implementation.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public abstract Dec clone();

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
        return myName.clone();
    }

    /**
     * <p>This method sets the mathematical type associated
     * with this object.</p>
     *
     * @param mt The {@link MTType} type object.
     */
    public final void setMathType(MTType mt) {
        if (mt == null) {
            throw new NullMathTypeException("Trying to set null type on "
                    + this.getClass());
        }

        myMathType = mt;
    }

    /**
     * <p>Returns this object in string format.</p>
     *
     * @return This class as a string.
     */
    @Override
    public String toString() {
        return "<" + this.getClass().getSimpleName() + "@" + myLoc + ">:"
                + getName();
    }

}