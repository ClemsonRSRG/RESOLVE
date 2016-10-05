/**
 * FacilitySharedStateRealizationDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.items.programitems.FacilityTypeInitFinalItem;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.List;

/**
 * <p>This is the class for all the facility shared state representation declaration objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class FacilitySharedStateRealizationDec
        extends
            AbstractSharedStateRealizationDec {

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
     * <p>This constructs a facility shared state representation declaration.</p>
     *
     * @param name Name of the new shared state.
     * @param stateVarDecs The list of {@link VarDec}s that are in the new shared state.
     * @param convention Shared state convention.
     * @param initItem Initialization block for this new shared state.
     * @param finalItem Finalization block for this new shared state.
     */
    public FacilitySharedStateRealizationDec(PosSymbol name,
            List<VarDec> stateVarDecs, AssertionClause convention,
            FacilityTypeInitFinalItem initItem,
            FacilityTypeInitFinalItem finalItem) {
        super(name, stateVarDecs, convention);
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
        sb.append("Shared State ");
        sb.append(myName.asString(0, innerIndentInc));
        sb.append(" =\n");

        // shared state variables
        for (VarDec varDec : myStateVars) {
            printSpace(indentSize + innerIndentInc, sb);
            sb.append("Var ");
            sb.append(varDec.asString(0, innerIndentInc));
            sb.append(";\n");
        }

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

        FacilitySharedStateRealizationDec that =
                (FacilitySharedStateRealizationDec) o;

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
    protected final FacilitySharedStateRealizationDec copy() {
        return new FacilitySharedStateRealizationDec(myName.clone(),
                copyStateVars(), myConvention.clone(), myTypeInitItem.clone(),
                myTypeFinalItem.clone());
    }

}