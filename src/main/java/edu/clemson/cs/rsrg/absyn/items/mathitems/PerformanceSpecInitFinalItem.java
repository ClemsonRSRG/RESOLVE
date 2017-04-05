/**
 * PerformanceSpecInitFinalItem.java
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

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the class for all the performance profile's type initialization/finalization
 * verification block objects that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class PerformanceSpecInitFinalItem extends ResolveConceptualElement {

    // ===========================================================
    // ItemType
    // ===========================================================

    /**
     * <p>This defines the various different performance item types.</p>
     *
     * @version 2.0
     */
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

    /** <p>The type of clause</p> */
    private final ItemType myItemType;

    /** <p>The duration expression</p> */
    private final AssertionClause myDuration;

    /** <p>The manipulation displacement expression</p> */
    private final AssertionClause myManipDisp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a type initialization/finalization block that happens
     * when a variable of this type is initialized/finalized.</p>
     *
     * @param l A {@link Location} representation object.
     * @param type Indicates if it is an initialization or finalization block.
     * @param duration A {@link AssertionClause} representing the initialization's/finalization's
     *                 duration clause.
     * @param manip_disp A {@link AssertionClause} representing the initialization's/finalization's
     *                   manipulation displacement clause.
     */
    public PerformanceSpecInitFinalItem(Location l, ItemType type,
            AssertionClause duration, AssertionClause manip_disp) {
        super(l);
        myItemType = type;
        myDuration = duration;
        myManipDisp = manip_disp;
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

        // duration clause
        if (myDuration != null) {
            sb.append(myDuration.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("\n");
        }

        // manipulative displacement clause
        if (myManipDisp != null) {
            sb.append(myManipDisp.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PerformanceSpecInitFinalItem clone() {
        AssertionClause newDuration = null;
        if (myDuration != null) {
            newDuration = myDuration.clone();
        }

        AssertionClause newManipDisp = null;
        if (myManipDisp != null) {
            newManipDisp = myManipDisp.clone();
        }

        return new PerformanceSpecInitFinalItem(cloneLocation(), myItemType,
                newDuration, newManipDisp);
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

        PerformanceSpecInitFinalItem that = (PerformanceSpecInitFinalItem) o;

        if (myItemType != that.myItemType)
            return false;
        if (myDuration != null ? !myDuration.equals(that.myDuration)
                : that.myDuration != null)
            return false;
        return myManipDisp != null ? myManipDisp.equals(that.myManipDisp)
                : that.myManipDisp == null;

    }

    /**
     * <p>This method returns the type that indicates
     * if this is a performance profile's
     * initialization or finalization verification block.</p>
     *
     * @return The {@link ItemType} object.
     */
    public final ItemType getClauseType() {
        return myItemType;
    }

    /**
     * <p>This method returns the duration clause
     * in this performance profile's
     * type initialization/finalization verification block.</p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getDuration() {
        return myDuration;
    }

    /**
     * <p>This method returns the manipulation displacement clause
     * in this performance profile's
     * type initialization/finalization verification block.</p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getManipDisp() {
        return myManipDisp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myItemType != null ? myItemType.hashCode() : 0;
        result = 31 * result + (myDuration != null ? myDuration.hashCode() : 0);
        result =
                31 * result
                        + (myManipDisp != null ? myManipDisp.hashCode() : 0);
        return result;
    }

}