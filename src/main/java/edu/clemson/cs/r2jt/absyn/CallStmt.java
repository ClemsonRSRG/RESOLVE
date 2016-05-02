/**
 * CallStmt.java
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

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;

public class CallStmt extends Statement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The qualifier member. */
    private PosSymbol qualifier;

    /** The name member. */
    private PosSymbol name;

    /** The arguments member. */
    private List<ProgramExp> arguments;

    // ===========================================================
    // Constructors
    // ===========================================================

    public CallStmt() {};

    public CallStmt(PosSymbol qualifier, PosSymbol name,
            List<ProgramExp> arguments) {
        this.qualifier = qualifier;
        this.name = name;
        this.arguments = arguments;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    public Location getLocation() {
        return name.getLocation();
    }

    /** Returns the value of the qualifier variable. */
    public PosSymbol getQualifier() {
        return qualifier;
    }

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the arguments variable. */
    public List<ProgramExp> getArguments() {
        return arguments;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the qualifier variable to the specified value. */
    public void setQualifier(PosSymbol qualifier) {
        this.qualifier = qualifier;
    }

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the arguments variable to the specified value. */
    public void setArguments(List<ProgramExp> arguments) {
        this.arguments = arguments;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitCallStmt(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("CallStmt\n");

        if (qualifier != null) {
            sb.append(qualifier.asString(indent + increment, increment));
        }

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (arguments != null) {
            sb.append(arguments.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (qualifier != null) {
            sb.append(qualifier.getName().toString() + ".");
        }

        if (name != null) {
            sb.append(name.toString() + "(");
        }

        if (arguments != null) {
            sb.append(argumentsToString(arguments) + ")");
        }

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
