/**
 * FacilityTypeRepresentationDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.typedecl;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.items.programitems.FacilityTypeInitFinalItem;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the facility type representation declaration objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class FacilityTypeRepresentationDec
        extends
            AbstractTypeRepresentationDec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The initialization block for the new type.</p> */
    private final FacilityTypeInitFinalItem myTypeInitItem;

    /** <p>The finalization block for the new type.</p> */
    private final FacilityTypeInitFinalItem myTypeFinalItem;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a facility type representation declaration.</p>
     *
     * @param name Name of the new type.
     * @param ty Raw type used to implement this new type.
     * @param convention Type convention.
     * @param initItem Initialization block for this new type.
     * @param finalItem Finalization block for this new type.
     */
    public FacilityTypeRepresentationDec(PosSymbol name, Ty ty,
            AssertionClause convention, FacilityTypeInitFinalItem initItem,
            FacilityTypeInitFinalItem finalItem) {
        super(name, ty, convention);
        myTypeInitItem = initItem;
        myTypeFinalItem = finalItem;
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
        sb.append(myConvention.asString(indentSize + innerIndentInc,
                innerIndentInc));
        sb.append("\n");

        // initialization/finalization
        sb.append(myTypeInitItem.asString(indentSize + innerIndentInc,
                innerIndentInc));
        sb.append(myTypeFinalItem.asString(indentSize + innerIndentInc,
                innerIndentInc));

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

        FacilityTypeRepresentationDec that = (FacilityTypeRepresentationDec) o;

        if (!myTypeInitItem.equals(that.myTypeInitItem))
            return false;
        return myTypeFinalItem.equals(that.myTypeFinalItem);

    }

    /**
     * <p>Returns the finalization block for this type representation.</p>
     *
     * @return The code block used for finalization
     * in {@link FacilityTypeInitFinalItem} format.
     */
    public final FacilityTypeInitFinalItem getTypeFinalItem() {
        return myTypeFinalItem;
    }

    /**
     * <p>Returns the initialization block for this type representation.</p>
     *
     * @return The code block used for initialization
     * in {@link FacilityTypeInitFinalItem} format.
     */
    public final FacilityTypeInitFinalItem getTypeInitItem() {
        return myTypeInitItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
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
    protected final FacilityTypeRepresentationDec copy() {
        return new FacilityTypeRepresentationDec(myName.clone(), myTy.clone(),
                myConvention.clone(), myTypeInitItem.clone(), myTypeFinalItem
                        .clone());
    }

}