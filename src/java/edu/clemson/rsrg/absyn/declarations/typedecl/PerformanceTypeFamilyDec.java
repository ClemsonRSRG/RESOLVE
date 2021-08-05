/*
 * PerformanceTypeFamilyDec.java
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
import edu.clemson.rsrg.absyn.declarations.Dec;
import edu.clemson.rsrg.absyn.items.mathitems.PerformanceSpecInitFinalItem;
import edu.clemson.rsrg.absyn.rawtypes.Ty;
import edu.clemson.rsrg.parsing.data.PosSymbol;

/**
 * <p>
 * This is the class for all the performance type family declaration objects that the compiler builds using the ANTLR4
 * AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class PerformanceTypeFamilyDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The type model for the new type family.
     * </p>
     */
    private final Ty myTy;

    /**
     * <p>
     * The constraint clause for the new type family.
     * </p>
     */
    private final AssertionClause myConstraint;

    /**
     * <p>
     * The initialization block for the new type family.
     * </p>
     */
    private final PerformanceSpecInitFinalItem myTypeInitItem;

    /**
     * <p>
     * The finalization block for the new type family.
     * </p>
     */
    private final PerformanceSpecInitFinalItem myTypeFinalItem;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a performance type family declaration.
     * </p>
     *
     * @param name
     *            Name of the new type family.
     * @param ty
     *            Model for the new type family.
     * @param constraint
     *            Type constraint.
     * @param initItem
     *            Initialization information for verification.
     * @param finalItem
     *            Finalization information for verification.
     */
    public PerformanceTypeFamilyDec(PosSymbol name, Ty ty, AssertionClause constraint,
            PerformanceSpecInitFinalItem initItem, PerformanceSpecInitFinalItem finalItem) {
        super(name.getLocation(), name);
        myConstraint = constraint;
        myTy = ty;
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
        sb.append("Type Family ");
        sb.append(myName.asString(0, innerIndentInc));
        sb.append(" is modeled by ");
        sb.append(myTy.asString(0, innerIndentInc));
        sb.append(";\n");

        // constraint
        sb.append(myConstraint.asString(indentSize + innerIndentInc, innerIndentInc));
        sb.append("\n");

        // initialization/finalization
        sb.append(myTypeInitItem.asString(indentSize + innerIndentInc, innerIndentInc));
        sb.append("\n");
        sb.append(myTypeFinalItem.asString(indentSize + innerIndentInc, innerIndentInc));
        sb.append("\n");

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

        PerformanceTypeFamilyDec that = (PerformanceTypeFamilyDec) o;

        if (!myTy.equals(that.myTy))
            return false;
        if (!myConstraint.equals(that.myConstraint))
            return false;
        if (myTypeInitItem != null ? !myTypeInitItem.equals(that.myTypeInitItem) : that.myTypeInitItem != null)
            return false;
        return myTypeFinalItem != null ? myTypeFinalItem.equals(that.myTypeFinalItem) : that.myTypeFinalItem == null;
    }

    /**
     * <p>
     * Returns the type constraint for this performance type family.
     * </p>
     *
     * @return The type constraint in {@link AssertionClause} format.
     */
    public final AssertionClause getConstraint() {
        return myConstraint;
    }

    /**
     * <p>
     * Returns the finalization block for this performance type family.
     * </p>
     *
     * @return All relevant verification for finalization in {@link PerformanceSpecInitFinalItem} format.
     */
    public final PerformanceSpecInitFinalItem getFinalization() {
        return myTypeFinalItem;
    }

    /**
     * <p>
     * Returns the initialization block for this performance type family.
     * </p>
     *
     * @return All relevant verification for initialization in {@link PerformanceSpecInitFinalItem} format.
     */
    public final PerformanceSpecInitFinalItem getInitialization() {
        return myTypeInitItem;
    }

    /**
     * <p>
     * Returns the raw type model representation of this performance type family.
     * </p>
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
        result = 31 * result + myConstraint.hashCode();
        result = 31 * result + (myTypeInitItem != null ? myTypeInitItem.hashCode() : 0);
        result = 31 * result + (myTypeFinalItem != null ? myTypeFinalItem.hashCode() : 0);
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final PerformanceTypeFamilyDec copy() {
        return new PerformanceTypeFamilyDec(myName.clone(), myTy.clone(), myConstraint.clone(), myTypeInitItem.clone(),
                myTypeFinalItem.clone());
    }

}
