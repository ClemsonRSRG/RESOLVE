/*
 * TypeDefinitionDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.typedecl;

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the type definition declaration objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class TypeDefinitionDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The type model for the new type definition.</p> */
    private final Ty myTy;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a type definition declaration.</p>
     *
     * @param name Name of the new type definition.
     * @param ty Model for the new type definition.
     */
    public TypeDefinitionDec(PosSymbol name, Ty ty) {
        super(name.getLocation(), name);
        myTy = ty;
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
        sb.append("Definition ");
        sb.append(myName.asString(0, innerIndentInc));
        sb.append(" = ");
        sb.append(myTy.asString(0, indentSize + innerIndentInc));
        sb.append(";");

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

        TypeDefinitionDec that = (TypeDefinitionDec) o;

        return myTy.equals(that.myTy);
    }

    /**
     * <p>Returns the raw type model representation
     * of this type definition.</p>
     *
     * @return The raw type in {@link Ty} format.
     */
    public final Ty getModel() {
        return myTy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myTy.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final TypeDefinitionDec copy() {
        return new TypeDefinitionDec(myName.clone(), myTy.clone());
    }
}