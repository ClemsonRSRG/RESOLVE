/*
 * IterateExitStmt.java
 * ---------------------------------
 * Copyright (c) 2018
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

public class IterateExitStmt extends Statement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The test member. */
    private ProgramExp test;

    /** The statements member. */
    private List<Statement> statements;

    // ===========================================================
    // Constructors
    // ===========================================================

    public IterateExitStmt() {};

    public IterateExitStmt(ProgramExp test, List<Statement> statements) {
        this.test = test;
        this.statements = statements;
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

    /** Returns the value of the statements variable. */
    public List<Statement> getStatements() {
        return statements;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the test variable to the specified value. */
    public void setTest(ProgramExp test) {
        this.test = test;
    }

    /** Sets the statements variable to the specified value. */
    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitIterateExitStmt(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("IterateExitStmt\n");

        if (test != null) {
            sb.append(test.asString(indent + increment, increment));
        }

        if (statements != null) {
            sb.append(statements.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        sb.append("when (" + test.toString(0) + ")");

        //	printSpace(indent, sb);
        sb.append("do\n");
        Iterator<Statement> i = statements.iterator();
        while (i.hasNext()) {
            sb.append((i.next()).toString(indent + 4 * 2) + ";\n");
        }
        printSpace(indent, sb);
        sb.append("exit");

        return sb.toString();
    }

    String argumentsToString(List<ProgramExp> arguments) {
        String str = new String();
        Iterator<ProgramExp> i = arguments.iterator();
        while (i.hasNext()) {
            ProgramExp exp = (ProgramExp) i.next();
            str = str.concat(exp.toString(0));
            if (i.hasNext())
                str = str.concat(", ");
        }
        return str;
    }

}
