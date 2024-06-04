/*
 * TypeFamilyDec.java
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
package edu.clemson.rsrg.absyn.declarations.typedecl;

import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.absyn.declarations.Dec;
import edu.clemson.rsrg.absyn.declarations.mathdecl.MathDefVariableDec;
import edu.clemson.rsrg.absyn.items.mathitems.SpecInitFinalItem;
import edu.clemson.rsrg.absyn.rawtypes.Ty;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This is the class for all the type family declaration objects that the compiler builds using the ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class TypeFamilyDec extends Dec {

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
     * The exemplar for the new type family.
     * </p>
     */
    private final PosSymbol myExemplar;

    /**
     * <p>
     * The list of mathematical definition variables for the new type family.
     * </p>
     */
    private final List<MathDefVariableDec> myDefVarList;

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
    private final SpecInitFinalItem myTypeInitItem;

    /**
     * <p>
     * The finalization block for the new type family.
     * </p>
     */
    private final SpecInitFinalItem myTypeFinalItem;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a type family declaration.
     * </p>
     *
     * @param name
     *            Name of the new type family.
     * @param ty
     *            Model for the new type family.
     * @param exemplar
     *            Exemplar variable name.
     * @param defVariableDecs
     *            List of definition variables.
     * @param constraint
     *            Type constraint.
     * @param initItem
     *            Initialization information for verification.
     * @param finalItem
     *            Finalization information for verification.
     */
    public TypeFamilyDec(PosSymbol name, Ty ty, PosSymbol exemplar, List<MathDefVariableDec> defVariableDecs,
            AssertionClause constraint, SpecInitFinalItem initItem, SpecInitFinalItem finalItem) {
        super(name.getLocation(), name);
        myConstraint = constraint;
        myExemplar = exemplar;
        myTy = ty;
        myDefVarList = defVariableDecs;
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
        sb.append(myTy.asString(0, indentSize + innerIndentInc));
        sb.append(";\n");

        // exemplar
        printSpace(indentSize + innerIndentInc, sb);
        sb.append("exemplar ");
        sb.append(myExemplar.asString(0, innerIndentInc));
        sb.append(";\n");

        // definition variables (if any)
        if (myDefVarList.size() > 0) {
            for (MathDefVariableDec variableDec : myDefVarList) {
                sb.append(variableDec.asString(indentSize + innerIndentInc, innerIndentInc));
                sb.append("\n");
            }
        }

        // constraint
        sb.append(myConstraint.asString(indentSize + innerIndentInc, innerIndentInc));
        sb.append("\n");

        // initialization/finalization
        sb.append(myTypeInitItem.asString(indentSize + innerIndentInc, innerIndentInc));
        sb.append("\n");
        sb.append(myTypeFinalItem.asString(indentSize + innerIndentInc, innerIndentInc));
        sb.append("\n");

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

        TypeFamilyDec that = (TypeFamilyDec) o;

        if (!myTy.equals(that.myTy))
            return false;
        if (!myExemplar.equals(that.myExemplar))
            return false;
        if (!myDefVarList.equals(that.myDefVarList))
            return false;
        if (!myConstraint.equals(that.myConstraint))
            return false;
        if (!myTypeInitItem.equals(that.myTypeInitItem))
            return false;
        return myTypeFinalItem.equals(that.myTypeFinalItem);
    }

    /**
     * <p>
     * Returns the type constraint for this type family.
     * </p>
     *
     * @return The type constraint in {@link AssertionClause} format.
     */
    public final AssertionClause getConstraint() {
        return myConstraint;
    }

    /**
     * <p>
     * Returns the list of definition variables for this type family.
     * </p>
     *
     * @return A list of {@link MathDefVariableDec} representation objects.
     */
    public final List<MathDefVariableDec> getDefinitionVarList() {
        return myDefVarList;
    }

    /**
     * <p>
     * Returns the exemplar for this type family.
     * </p>
     *
     * @return The exemplar in {@link PosSymbol} format.
     */
    public final PosSymbol getExemplar() {
        return myExemplar;
    }

    /**
     * <p>
     * Returns the finalization block for this type family.
     * </p>
     *
     * @return All relevant verification for finalization in {@link SpecInitFinalItem} format.
     */
    public final SpecInitFinalItem getFinalization() {
        return myTypeFinalItem;
    }

    /**
     * <p>
     * Returns the initialization block for this type family.
     * </p>
     *
     * @return All relevant verification for initialization in {@link SpecInitFinalItem} format.
     */
    public final SpecInitFinalItem getInitialization() {
        return myTypeInitItem;
    }

    /**
     * <p>
     * Returns the raw type model representation of this type family.
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
        result = 31 * result + myExemplar.hashCode();
        result = 31 * result + myDefVarList.hashCode();
        result = 31 * result + myConstraint.hashCode();
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
    protected final TypeFamilyDec copy() {
        List<MathDefVariableDec> newDefVars = new ArrayList<>(myDefVarList.size());
        for (MathDefVariableDec variableDec : myDefVarList) {
            newDefVars.add((MathDefVariableDec) variableDec.clone());
        }

        return new TypeFamilyDec(myName.clone(), myTy.clone(), myExemplar.clone(), newDefVars, myConstraint.clone(),
                myTypeInitItem.clone(), myTypeFinalItem.clone());
    }
}
