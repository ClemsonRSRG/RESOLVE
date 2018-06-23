/*
 * Ty.java
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
package edu.clemson.cs.rsrg.absyn.rawtypes;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;

/**
 * <p>A {@link Ty} represents the <em>description</em> of a
 * {@code Type}, as it is found in the RESOLVE source code. That is, it is
 * representation of a type in the abstract syntax tree before it is translated 
 * into a true {@code Type}.</p>
 *
 * @version 2.0
 */
public abstract class Ty extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The object's mathematical type.</p> */
    protected MTType myMathType = null;

    /** <p>The object's mathematical type value.</p> */
    protected MTType myMathTypeValue = null;

    /** <p>The program type representation for this programming expression.</p> */
    protected PTType myProgramTypeValue = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the location
     * of any objects created from a class that inherits from
     * {@code Ty}.</p>
     *
     * @param l A {@link Location} representation object.
     */
    protected Ty(Location l) {
        super(l);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default clone method implementation
     * for all the classes that extend from {@link Ty}.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final Ty clone() {
        Ty result = this.copy();
        result.setMathType(myMathType);
        result.setMathTypeValue(myMathTypeValue);
        result.setProgramType(myProgramTypeValue);

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

        Ty ty = (Ty) o;

        if (myMathType != null ? !myMathType.equals(ty.myMathType)
                : ty.myMathType != null)
            return false;
        if (myMathTypeValue != null ? !myMathTypeValue
                .equals(ty.myMathTypeValue) : ty.myMathTypeValue != null)
            return false;
        return myProgramTypeValue != null ? myProgramTypeValue
                .equals(ty.myProgramTypeValue) : ty.myProgramTypeValue == null;

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
     * <p>This method gets the mathematical type value associated
     * with this object.</p>
     *
     * @return The {@link MTType} type object.
     */
    public final MTType getMathTypeValue() {
        return myMathTypeValue;
    }

    /**
     * <p>This method gets the programming type associated
     * with this object.</p>
     *
     * @return The {@link PTType} type object.
     */
    public final PTType getProgramType() {
        return myProgramTypeValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = myMathType != null ? myMathType.hashCode() : 0;
        result =
                31
                        * result
                        + (myMathTypeValue != null ? myMathTypeValue.hashCode()
                                : 0);
        result =
                31
                        * result
                        + (myProgramTypeValue != null ? myProgramTypeValue
                                .hashCode() : 0);
        return result;
    }

    /**
     * <p>This method sets the mathematical type associated
     * with this object.</p>
     *
     * @param mathType The {@link MTType} type object.
     */
    public final void setMathType(MTType mathType) {
        myMathType = mathType;
    }

    /**
     * <p>This method sets the mathematical type value associated
     * with this object.</p>
     *
     * @param mathTypeValue The {@link MTType} type object.
     */
    public final void setMathTypeValue(MTType mathTypeValue) {
        myMathTypeValue = mathTypeValue;
    }

    /**
     * <p>This method sets the programming type associated
     * with this object.</p>
     *
     * @param progType The {@link PTType} type object.
     */
    public final void setProgramType(PTType progType) {
        myProgramTypeValue = progType;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by concrete subclasses of {@link Ty} to manufacture
     * a copy of themselves.</p>
     *
     * @return A new {@link Ty} that is a deep copy of the original.
     */
    protected Ty copy() {
        throw new MiscErrorException("Shouldn't be calling copy()!  Type: "
                + this.getClass(), new CloneNotSupportedException());
    }

}