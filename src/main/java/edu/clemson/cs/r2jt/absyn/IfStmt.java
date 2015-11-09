/**
 * IfStmt.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;

public class IfStmt extends Statement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The test member. */
    private ProgramExp test;

    /** The thenclause member. */
    private List<Statement> thenclause;

    /** The elseifpairs member. */
    private List<ConditionItem> elseifpairs;

    /** The elseclause member. */
    private List<Statement> elseclause;

    // ===========================================================
    // Constructors
    // ===========================================================

    public IfStmt() {};

    public IfStmt(ProgramExp test, List<Statement> thenclause,
            List<ConditionItem> elseifpairs, List<Statement> elseclause) {
        this.test = test;
        this.thenclause = thenclause;
        this.elseifpairs = elseifpairs;
        this.elseclause = elseclause;
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

    /** Returns the value of the elseifpairs variable. */
    public List<ConditionItem> getElseifpairs() {
        return elseifpairs;
    }

    /** Returns the value of the elseclause variable. */
    public List<Statement> getElseclause() {
        return elseclause;
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

    /** Sets the elseifpairs variable to the specified value. */
    public void setElseifpairs(List<ConditionItem> elseifpairs) {
        this.elseifpairs = elseifpairs;
    }

    /** Sets the elseclause variable to the specified value. */
    public void setElseclause(List<Statement> elseclause) {
        this.elseclause = elseclause;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitIfStmt(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("IfStmt\n");

        if (test != null) {
            sb.append(test.asString(indent + increment, increment));
        }

        if (thenclause != null) {
            sb.append(thenclause.asString(indent + increment, increment));
        }

        if (elseifpairs != null) {
            sb.append(elseifpairs.asString(indent + increment, increment));
        }

        if (elseclause != null) {
            sb.append(elseclause.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public String toString(int indent) {
        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("If ");
        sb.append(test.toString(0));
        sb.append(" then\n");

        List<Statement> list = thenclause;
        Iterator<Statement> i = list.iterator();
        int next_indent = 4;
        while (i.hasNext()) {
            if (indent == 0)
                indent = 4;
            sb.append((i.next()).toString(indent + next_indent) + ";\n");
        }

        List<Statement> statements = elseclause;
        if (statements != null && !statements.isEmpty()) {
            i = statements.iterator();
            if (i.hasNext()) {
                printSpace(indent, sb);
                sb.append("else\n");
            }
            while (i.hasNext()) {
                sb.append((i.next()).toString(next_indent + indent) + ";\n");
            }
        }
        printSpace(indent, sb);
        sb.append("end");
        return sb.toString();
    }
}
