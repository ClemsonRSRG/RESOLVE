/*
 * ModuleParameterDec.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.absyn.declarations.paramdecl;

import edu.clemson.rsrg.absyn.declarations.Dec;
import edu.clemson.rsrg.parsing.data.PosSymbol;

/**
 * <p>
 * This is a wrapper class for all the different module parameter declaration objects that the compiler builds using the
 * ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class ModuleParameterDec<T extends Dec & ModuleParameter> extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The actual module parameter declaration
     * </p>
     */
    private final T myWrappedDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Given a declaration, we wrap it to indicate that this is a module level parameter declaration.
     * </p>
     *
     * @param dec
     *            The declaration to be wrapped.
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
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(myWrappedDec.asString(indentSize + innerIndentInc, innerIndentInc));

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

        ModuleParameterDec<?> that = (ModuleParameterDec<?>) o;

        return myWrappedDec.equals(that.myWrappedDec);
    }

    /**
     * <p>
     * Returns the symbol representation of this class.
     * </p>
     *
     * @return A {@link PosSymbol} representation of the name.
     */
    @Override
    public final PosSymbol getName() {
        return myWrappedDec.getName();
    }

    /**
     * <p>
     * Returns the inner wrapped declaration.
     * </p>
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
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myWrappedDec.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected final ModuleParameterDec copy() {
        return new ModuleParameterDec(myWrappedDec.clone());
    }

}
