/*
 * TypeRepresentationDec.java
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
package edu.clemson.rsrg.absyn.declarations.typedecl;

import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.absyn.items.programitems.RealizInitFinalItem;
import edu.clemson.rsrg.absyn.rawtypes.Ty;
import edu.clemson.rsrg.parsing.data.PosSymbol;

/**
 * <p>
 * This is the class for all the type representation declaration objects that the compiler builds using the ANTLR4 AST
 * nodes.
 * </p>
 *
 * @version 2.0
 */
public class TypeRepresentationDec extends AbstractTypeRepresentationDec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The correspondence clause for the new type.
     * </p>
     */
    private final AssertionClause myCorrespondence;

    /**
     * <p>
     * The initialization block for the new type.
     * </p>
     */
    private final RealizInitFinalItem myTypeInitItem;

    /**
     * <p>
     * The finalization block for the new type.
     * </p>
     */
    private final RealizInitFinalItem myTypeFinalItem;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a type representation declaration.
     * </p>
     *
     * @param name
     *            Name of the new type.
     * @param ty
     *            Raw type used to implement this new type.
     * @param convention
     *            Type convention.
     * @param correspondence
     *            Type correspondence.
     * @param initItem
     *            Initialization block for this new type.
     * @param finalItem
     *            Finalization block for this new type.
     */
    public TypeRepresentationDec(PosSymbol name, Ty ty, AssertionClause convention, AssertionClause correspondence,
            RealizInitFinalItem initItem, RealizInitFinalItem finalItem) {
        super(name, ty, convention);
        myCorrespondence = correspondence;
        myTypeFinalItem = finalItem;
        myTypeInitItem = initItem;
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
        sb.append("Type ");
        sb.append(myName.asString(0, innerIndentInc));
        sb.append(formRepresentationTy(indentSize, innerIndentInc));

        // convention
        sb.append(myConvention.asString(indentSize + innerIndentInc, innerIndentInc));
        sb.append("\n");

        // correspondence
        sb.append(myCorrespondence.asString(indentSize + innerIndentInc, innerIndentInc));
        sb.append("\n");

        // initialization/finalization
        sb.append(myTypeInitItem.asString(indentSize + innerIndentInc, innerIndentInc));
        sb.append(myTypeFinalItem.asString(indentSize + innerIndentInc, innerIndentInc));

        printSpace(indentSize, sb);
        sb.append("end;");

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

        TypeRepresentationDec that = (TypeRepresentationDec) o;

        if (!myCorrespondence.equals(that.myCorrespondence))
            return false;
        if (!myTypeInitItem.equals(that.myTypeInitItem))
            return false;
        return myTypeFinalItem.equals(that.myTypeFinalItem);
    }

    /**
     * <p>
     * Returns the correspondence for this type representation.
     * </p>
     *
     * @return The type correspondence in {@link AssertionClause} format.
     */
    public final AssertionClause getCorrespondence() {
        return myCorrespondence;
    }

    /**
     * <p>
     * Returns the finalization block for this type representation.
     * </p>
     *
     * @return The code block used for finalization in {@link RealizInitFinalItem} format.
     */
    public final RealizInitFinalItem getTypeFinalItem() {
        return myTypeFinalItem;
    }

    /**
     * <p>
     * Returns the initialization block for this type representation.
     * </p>
     *
     * @return The code block used for initialization in {@link RealizInitFinalItem} format.
     */
    public final RealizInitFinalItem getTypeInitItem() {
        return myTypeInitItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myCorrespondence.hashCode();
        result = 31 * result + myTypeInitItem.hashCode();
        result = 31 * result + myTypeFinalItem.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final TypeRepresentationDec copy() {
        return new TypeRepresentationDec(myName.clone(), myTy.clone(), myConvention.clone(), myCorrespondence.clone(),
                myTypeInitItem.clone(), myTypeFinalItem.clone());
    }

}
