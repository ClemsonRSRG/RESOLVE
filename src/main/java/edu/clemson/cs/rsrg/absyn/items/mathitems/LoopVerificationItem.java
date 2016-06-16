/**
 * LoopVerificationItem.java
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
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramVariableExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the loop verification block objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class LoopVerificationItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The changing clause</p> */
    private final List<ProgramVariableExp> myChangingVars;

    /** <p>The maintaining clause</p> */
    private final AssertionClause myMaintainingClause;

    /** <p>The decreasing clause</p> */
    private final AssertionClause myDecreasingClause;

    /** <p>The elapsed time clause</p> */
    private final AssertionClause myElapsedTimeClause;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a verification block for loops.</p>
     *
     * @param l A {@link Location} representation object.
     * @param changingVars The list of program variables that are changing.
     * @param maintaining The loop invariant.
     * @param decreasing The termination metric.
     * @param elapsedTime The total elapsed time for this loop.
     */
    public LoopVerificationItem(Location l,
            List<ProgramVariableExp> changingVars, AssertionClause maintaining,
            AssertionClause decreasing, AssertionClause elapsedTime) {
        super(l);
        myChangingVars = changingVars;
        myMaintainingClause = maintaining;
        myDecreasingClause = decreasing;
        myElapsedTimeClause = elapsedTime;
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

        // Changing clause
        printSpace(indentSize, sb);
        sb.append("changing ");
        Iterator<ProgramVariableExp> it = myChangingVars.iterator();
        while (it.hasNext()) {
            ProgramVariableExp exp = it.next();
            sb.append(exp.asString(0, innerIndentInc));

            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(";\n");

        // Maintaining clause
        sb.append(myMaintainingClause.asString(indentSize, innerIndentInc));
        sb.append("\n");

        // Decreasing clause
        sb.append(myDecreasingClause.asString(indentSize, innerIndentInc));

        // Elapsed time clause (if any)
        if (myElapsedTimeClause != null) {
            sb.append("\n");
            sb.append(myElapsedTimeClause.asString(indentSize, innerIndentInc));
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final LoopVerificationItem clone() {
        List<ProgramVariableExp> newChangingVars = new ArrayList<>();
        for (ProgramVariableExp exp : myChangingVars) {
            newChangingVars.add((ProgramVariableExp) exp.clone());
        }

        AssertionClause newElapsedTimeClause = null;
        if (myElapsedTimeClause != null) {
            newElapsedTimeClause = myElapsedTimeClause.clone();
        }

        return new LoopVerificationItem(new Location(myLoc),
                newChangingVars,
                myMaintainingClause.clone(),
                myDecreasingClause.clone(),
                newElapsedTimeClause);
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

        LoopVerificationItem that = (LoopVerificationItem) o;

        if (!myChangingVars.equals(that.myChangingVars))
            return false;
        if (!myMaintainingClause.equals(that.myMaintainingClause))
            return false;
        if (!myDecreasingClause.equals(that.myDecreasingClause))
            return false;
        return myElapsedTimeClause != null ? myElapsedTimeClause
                .equals(that.myElapsedTimeClause)
                : that.myElapsedTimeClause == null;

    }

    /**
     * <p>This method returns the list of changing variables
     * in this loop verification block.</p>
     *
     * @return The list of {@link ProgramVariableExp}s.
     */
    public final List<ProgramVariableExp> getChangingVars() {
        return myChangingVars;
    }

    /**
     * <p>This method returns the decreasing
     * clause in this loop verification block.</p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getDecreasingClause() {
        return myDecreasingClause;
    }

    /**
     * <p>This method returns the elapsed time
     * clause in this loop verification block.</p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getElapsedTimeClause() {
        return myElapsedTimeClause;
    }

    /**
     * <p>This method returns the maintaining
     * clause in this loop verification block.</p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getMaintainingClause() {
        return myMaintainingClause;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myChangingVars.hashCode();
        result = 31 * result + myMaintainingClause.hashCode();
        result = 31 * result + myDecreasingClause.hashCode();
        result =
                31
                        * result
                        + (myElapsedTimeClause != null ? myElapsedTimeClause
                                .hashCode() : 0);
        return result;
    }

}
