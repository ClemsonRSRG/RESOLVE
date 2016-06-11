/**
 * SpecInitFinalItem.java
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
package edu.clemson.cs.rsrg.absyn.items.mathitems;

import edu.clemson.cs.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the class for all the specification initialization/finalization
 * block objects that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class SpecInitFinalItem extends ResolveConceptualElement {

    // ===========================================================
    // ItemType
    // ===========================================================

    public enum ItemType {
        INITIALIZATION {

            @Override
            public String toString() {
                return "initialization";
            }

        },
        FINALIZATION {

            @Override
            public String toString() {
                return "finalization";
            }

        }
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The affects clause.</p> */
    private final AffectsClause myAffects;

    /** <p>The type of clause</p> */
    private final ItemType myItemType;

    /** <p>The ensures expression</p> */
    private final AssertionClause myEnsures;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a specification initialization/finalization block.</p>
     *
     * @param l A {@link Location} representation object.
     * @param type Indicates if it is an initialization or finalization block.
     * @param affects A {@link AffectsClause} representing the initialization's/finalization's
     *                affects clause.
     * @param ensures A {@link AssertionClause} representing the initialization's/finalization's
     *                ensures clause.
     */
    public SpecInitFinalItem(Location l, ItemType type, AffectsClause affects,
            AssertionClause ensures) {
        super(l);
        myAffects = affects;
        myItemType = type;
        myEnsures = ensures;
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
        sb.append(myItemType.toString());
        sb.append("\n");

        // affects clause
        if (myAffects != null) {
            sb.append(myAffects.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        // ensures clause
        sb.append(myEnsures.asString(indentSize + innerIndentInc,
                innerIndentInc));

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final SpecInitFinalItem clone() {
        AffectsClause newAffects = null;
        if (myAffects != null) {
            newAffects = myAffects.clone();
        }

        return new SpecInitFinalItem(new Location(myLoc), myItemType,
                newAffects, myEnsures.clone());
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

        SpecInitFinalItem that = (SpecInitFinalItem) o;

        if (myAffects != null ? !myAffects.equals(that.myAffects)
                : that.myAffects != null)
            return false;
        if (myItemType != that.myItemType)
            return false;
        return myEnsures.equals(that.myEnsures);

    }

    /**
     * <p>This method returns the affects clause
     * in this type initialization/finalization verification block.</p>
     *
     * @return The {@link AffectsClause} representation object.
     */
    public final AffectsClause getAffectedVars() {
        return myAffects;
    }

    /**
     * <p>This method returns the type that indicates
     * if this is an initialization or finalization verification block.</p>
     *
     * @return The {@link ItemType} object.
     */
    public final ItemType getClauseType() {
        return myItemType;
    }

    /**
     * <p>This method returns the ensures clause
     * in this type initialization/finalization verification block.</p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getEnsures() {
        return myEnsures;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myAffects != null ? myAffects.hashCode() : 0;
        result = 31 * result + myItemType.hashCode();
        result = 31 * result + myEnsures.hashCode();
        return result;
    }

}