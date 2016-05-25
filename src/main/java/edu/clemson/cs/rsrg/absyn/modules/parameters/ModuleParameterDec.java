/**
 * ModuleParameterDec.java
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
package edu.clemson.cs.rsrg.absyn.modules.parameters;

import edu.clemson.cs.rsrg.absyn.Dec;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is a wrapper class for all the different module parameter
 * declaration objects that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class ModuleParameterDec<T extends Dec & ModuleParameter> extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The actual module parameter declaration</p> */
    private final T myWrappedDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Given a declaration, we wrap it to indicate that this
     * is a module level parameter declaration.</p>
     *
     * @param dec The declaration to be wrapped.
     */
    public ModuleParameterDec(T dec) {
        super(dec.getLocation(), dec.getName());
        myWrappedDec = dec;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        return myWrappedDec.asString(indentSize, innerIndentInc);
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link ModuleParameterDec} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    @SuppressWarnings("unchecked")
    public final ModuleParameterDec clone() {
        return new ModuleParameterDec(myWrappedDec.clone());
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

        ModuleParameterDec<?> that = (ModuleParameterDec<?>) o;

        return myWrappedDec.equals(that.myWrappedDec);

    }

    /**
     * <p>Returns the symbol representation of this class.</p>
     *
     * @return A {@link PosSymbol} representation of the name.
     */
    @Override
    public final PosSymbol getName() {
        return myWrappedDec.getName();
    }

    /**
     * <p>Returns the inner wrapped declaration.</p>
     *
     * @return A {@link Dec} object.
     */
    public final Dec getWrappedDec() {
        return myWrappedDec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myWrappedDec.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return myWrappedDec.toString();
    }

}