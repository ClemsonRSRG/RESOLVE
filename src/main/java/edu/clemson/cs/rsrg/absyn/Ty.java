/**
 * Ty.java
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
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.errorhandling.exception.NullMathTypeException;
import edu.clemson.cs.rsrg.errorhandling.exception.NullProgramTypeException;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>A {@link Ty} represents the <em>description</em> of a
 * <code>Type</code>, as it is found in the RESOLVE source code. That is, it is
 * representation of a type in the abstract syntax tree before it is translated 
 * into a true <code>Type</code>.</p>
 * 
 * <p>It can be converted into a <code>Type</code> by a type.TypeConverter.</p>
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
     * <p>A helper constructor that allow us to store the location
     * of the created object directly in the this class.</p>
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
     * <p>This method gets the mathematical type associated
     * with this object.</p>
     *
     * @return The {link MTType} type object.
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
     * @return The {link PTType} type object.
     */
    public final PTType getProgramType() {
        return myProgramTypeValue;
    }

    /**
     * <p>This method sets the mathematical type associated
     * with this object.</p>
     *
     * @param mathType The {@link MTType} type object.
     */
    public final void setMathType(MTType mathType) {
        if (mathType == null) {
            throw new NullMathTypeException("Null Math Type on: "
                    + this.getClass() + ". The causing raw type is: "
                    + this.toString());
        }

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
        if (progType == null) {
            throw new NullProgramTypeException("Null Program Type on: "
                    + this.getClass());
        }

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