/**
 * AbstractVarDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.variabledecl;

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;

/**
 * <p>This is the abstract base class for both the mathematical and
 * programming variable declaration objects that the compiler builds
 * using the ANTLR4 AST nodes.</p>
 *
 * @version 1.0
 */
public abstract class AbstractVarDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The object's raw type representation.</p> */
    protected final Ty myTy;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the location,
     * name and raw type of any objects created from a class
     * that inherits from {@code AbstractVarDec}.</p>
     *
     * @param l A {@link Location} representation object.
     * @param name A {@link PosSymbol} representing the variable's name.
     * @param ty A {@link Ty} representing the variable's raw type.
     */
    protected AbstractVarDec(Location l, PosSymbol name, Ty ty) {
        super(l, name);
        myTy = ty;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AbstractVarDec that = (AbstractVarDec) o;

        return myTy.equals(that.myTy);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return myTy.hashCode();
    }

    /**
     * <p>Returns the raw type representation
     * of this class.</p>
     *
     * @return The raw type in {@link Ty} format.
     */
    public final Ty getTy() {
        return myTy;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>A helper method to generate the special text formatted strings
     * for classes that inherit from {@code AbstractVarDec}</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentInc The additional indentation increment
     *                       for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    protected final String asStringVarDec(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        sb.append(myName.asString(indentSize, innerIndentInc));
        sb.append(" : ");
        sb.append(myTy.asString(0, innerIndentInc));

        return sb.toString();
    }

    /**
     * <p>Implemented by concrete subclasses of {@link AbstractVarDec}
     * to manufacture a copy of themselves.</p>
     *
     * @return A new {@link AbstractVarDec} that is a deep copy of the original.
     */
    protected AbstractVarDec copy() {
        throw new MiscErrorException("Shouldn't be calling copy()!  Type: "
                + this.getClass(), new CloneNotSupportedException());
    }

}