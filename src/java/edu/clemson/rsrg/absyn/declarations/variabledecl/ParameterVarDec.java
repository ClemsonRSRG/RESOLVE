/*
 * ParameterVarDec.java
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
package edu.clemson.rsrg.absyn.declarations.variabledecl;

import edu.clemson.rsrg.absyn.rawtypes.Ty;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.typeandpopulate.entry.ProgramParameterEntry;

/**
 * <p>
 * This is the class for all the programming parameter variable declaration objects that the compiler builds using the
 * ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class ParameterVarDec extends AbstractVarDec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The object's mode representation.
     * </p>
     */
    private final ProgramParameterEntry.ParameterMode myMode;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a program parameter variable.
     * </p>
     *
     * @param mode
     *            A {@link ProgramParameterEntry.ParameterMode} representing the parameter mode.
     * @param name
     *            A {@link PosSymbol} representing the variable's name.
     * @param ty
     *            A {@link Ty} representing the variable's raw type.
     */
    public ParameterVarDec(ProgramParameterEntry.ParameterMode mode, PosSymbol name, Ty ty) {
        super(name.getLocation(), name, ty);
        myMode = mode;
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
        sb.append(myMode.name());
        sb.append(" ");
        sb.append(super.asStringVarDec(indentSize, innerIndentInc));

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

        ParameterVarDec that = (ParameterVarDec) o;

        return myMode == that.myMode;
    }

    /**
     * <p>
     * Returns the mode of this parameter variable.
     * </p>
     *
     * @return The mode in {@link ProgramParameterEntry.ParameterMode} format.
     */
    public final ProgramParameterEntry.ParameterMode getMode() {
        return myMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myMode.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final ParameterVarDec copy() {
        return new ParameterVarDec(myMode, myName.clone(), myTy.clone());
    }

}
