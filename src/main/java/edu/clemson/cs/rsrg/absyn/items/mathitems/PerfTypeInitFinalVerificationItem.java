/**
 * PerfTypeInitFinalVerificationItem.java
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
public class PerfTypeInitFinalVerificationItem extends ResolveConceptualElement {

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

    /** <p>The type of clause</p> */
    private final ItemType myItemType;

    /** <p>The duration expression</p> */
    private final AssertionClause myDuration;

    /** <p>The manipulative displacement expression</p> */
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
     *                   manipulative displacement clause.
     */
    public PerfTypeInitFinalVerificationItem(Location l, ItemType type,
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
        sb.append(myDuration.asString(indentSize + innerIndentInc,
                innerIndentInc));

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
    public final PerfTypeInitFinalVerificationItem clone() {
        AssertionClause newManipDisp = null;
        if (myManipDisp != null) {
            newManipDisp = myManipDisp.clone();
        }

        return new PerfTypeInitFinalVerificationItem(new Location(myLoc),
                myItemType, myDuration.clone(), newManipDisp);
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

        PerfTypeInitFinalVerificationItem that =
                (PerfTypeInitFinalVerificationItem) o;

        if (myItemType != that.myItemType)
            return false;
        if (!myDuration.equals(that.myDuration))
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
     * <p>This method returns the manipulative displacement clause
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
        int result = myItemType.hashCode();
        result = 31 * result + myDuration.hashCode();
        result =
                31 * result
                        + (myManipDisp != null ? myManipDisp.hashCode() : 0);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(myItemType.toString());
        sb.append("\n");

        // duration clause
        sb.append("\t");
        sb.append(myDuration.toString());

        // manipulative displacement clause
        if (myManipDisp != null) {
            sb.append("\t");
            sb.append(myManipDisp.toString());
        }

        return sb.toString();
    }

}