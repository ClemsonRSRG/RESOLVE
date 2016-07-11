/**
 * ProgramExp.java
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
package edu.clemson.cs.rsrg.absyn.expressions.programexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;

/**
 * <p>This is the abstract base class for all the programming expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public abstract class ProgramExp extends Exp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The program type representation for this programming expression.</p> */
    private PTType myProgramType;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the location
     * of any objects created from a class that inherits from
     * {@code ProgramExp}.</p>
     *
     * @param l A {@link Location} representation object.
     */
    protected ProgramExp(Location l) {
        super(l);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default clone method implementation
     * for all the classes that extend from {@link ProgramExp}.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final ProgramExp clone() {
        ProgramExp newExp = (ProgramExp) super.clone();
        newExp.setProgramType(myProgramType);

        return newExp;
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
        if (!super.equals(o))
            return false;

        ProgramExp that = (ProgramExp) o;

        return myProgramType != null ? myProgramType.equals(that.myProgramType)
                : that.myProgramType == null;

    }

    /**
     * <p>This method gets the programming type associated
     * with this object.</p>
     *
     * @return The {@link PTType} type object.
     */
    public final PTType getProgramType() {
        return myProgramType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result =
                31
                        * result
                        + (myProgramType != null ? myProgramType.hashCode() : 0);
        return result;
    }

    /**
     * <p>This method sets the programming type associated
     * with this object.</p>
     *
     * @param progType The {@link PTType} type object.
     */
    public final void setProgramType(PTType progType) {
        myProgramType = progType;
    }

}