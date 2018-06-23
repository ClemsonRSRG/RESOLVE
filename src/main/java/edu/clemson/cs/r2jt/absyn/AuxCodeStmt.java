/*
 * AuxCodeStmt.java
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

public class AuxCodeStmt extends Statement {

    // Variables

    /** The statements member. */
    private List<Statement> statements;

    // Constructors

    public AuxCodeStmt() {};

    public AuxCodeStmt(List<Statement> statements) {
        this.statements = statements;
    }

    // Accessor Methods

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    public Location getLocation() {
        return statements.get(0).getLocation();
    }

    /** Returns the value of the elseclause variable. */
    public List<Statement> getStatements() {
        return statements;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the elseclause variable to the specified value. */
    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    // Public Methods

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitAuxCodeStmt(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("AuxCode\n");

        if (statements != null) {
            sb.append(statements.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public String toString(int indent) {
        StringBuffer sb = new StringBuffer();

        int next_indent = 4;
        List<Statement> stmts = statements;
        Iterator<Statement> i = stmts.iterator();
        if (i.hasNext()) {
            printSpace(indent, sb);
            sb.append("Aux_Code\n");
        }
        while (i.hasNext()) {
            sb.append((i.next()).toString(next_indent + indent) + ";\n");
        }
        ;
        printSpace(indent, sb);
        sb.append("end");
        return sb.toString();
    }
}
