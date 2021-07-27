/*
 * ProgramVariableExp.java
 * ---------------------------------
 * Copyright (c) 2021
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
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>
 * This is the abstract base class for all the programming variable expression
 * objects that the
 * compiler builds using the ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public abstract class ProgramVariableExp extends ProgramExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The expression's qualifier
     * </p>
     */
    private PosSymbol myQualifier;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * An helper constructor that allow us to store the location and qualifier
     * (if any) of any objects
     * created from a class that inherits from {@code ProgramVariableExp}.
     * </p>
     *
     * @param l A {@link Location} representation object.
     * @param qual A {@link PosSymbol} representing the expression's qualifier.
     */
    protected ProgramVariableExp(Location l, PosSymbol qual) {
        super(l);
        myQualifier = qual;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        return false;
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

        ProgramVariableExp that = (ProgramVariableExp) o;

        return myQualifier != null ? myQualifier.equals(that.myQualifier)
                : that.myQualifier == null;
    }

    /**
     * <p>
     * This method returns the qualifier name.
     * </p>
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
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result
                + (myQualifier != null ? myQualifier.hashCode() : 0);
        return result;
    }

    /**
     * <p>
     * Sets the qualifier for this expression.
     * </p>
     *
     * @param qualifier The qualifier for this expression.
     */
    public final void setQualifier(PosSymbol qualifier) {
        myQualifier = qualifier;
    }

}
