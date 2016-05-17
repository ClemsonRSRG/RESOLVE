/**
 * ConditionItem.java
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
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;

public class ConditionItem extends ResolveConceptualElement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The test member. */
    private ProgramExp test;

    /** The thenclause member. */
    private List<Statement> thenclause;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ConditionItem() {};

    public ConditionItem(ProgramExp test, List<Statement> thenclause) {
        this.test = test;
        this.thenclause = thenclause;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    public Location getLocation() {
        return test.getLocation();
    }

    /** Returns the value of the test variable. */
    public ProgramExp getTest() {
        return test;
    }

    /** Returns the value of the thenclause variable. */
    public List<Statement> getThenclause() {
        return thenclause;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the test variable to the specified value. */
    public void setTest(ProgramExp test) {
        this.test = test;
    }

    /** Sets the thenclause variable to the specified value. */
    public void setThenclause(List<Statement> thenclause) {
        this.thenclause = thenclause;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitConditionItem(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ConditionItem\n");

        if (test != null) {
            sb.append(test.asString(indent + increment, increment));
        }

        if (thenclause != null) {
            sb.append(thenclause.asString(indent + increment, increment));
        }

        return sb.toString();
    }
}
