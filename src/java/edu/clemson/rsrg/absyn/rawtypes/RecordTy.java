/*
 * RecordTy.java
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
package edu.clemson.rsrg.absyn.rawtypes;

import edu.clemson.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This is the class for all the raw record type objects that the compiler builds using the ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class RecordTy extends Ty {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The raw type's fields
     * </p>
     */
    private final List<VarDec> myInnerFields;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a raw record type.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param fields
     *            A list of {@link VarDec} representing the fields inside this raw record type.
     */
    public RecordTy(Location l, List<VarDec> fields) {
        super(l);
        myInnerFields = fields;
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

        for (VarDec v : myInnerFields) {
            sb.append(v.asString(indentSize + innerIndentInc, innerIndentInc));
            sb.append(";\n");
        }

        printSpace(indentSize, sb);
        sb.append("end;\n");

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

        RecordTy recordTy = (RecordTy) o;

        return myInnerFields.equals(recordTy.myInnerFields);

    }

    /**
     * <p>
     * This method returns list containing all the field elements.
     * </p>
     *
     * @return A list of {@link VarDec} representation objects.
     */
    public final List<VarDec> getFields() {
        return myInnerFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myInnerFields.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Ty copy() {
        List<VarDec> newFields = new ArrayList<>();
        for (VarDec varDec : myInnerFields) {
            newFields.add((VarDec) varDec.clone());
        }

        return new RecordTy(cloneLocation(), newFields);
    }
}
