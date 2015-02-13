/**
 * UnaryMinusExp.java
 * ---------------------------------
 * Copyright (c) 2014
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

public class UnaryMinusExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The argument member. */
    private Exp argument;

    // ===========================================================
    // Constructors
    // ===========================================================

    public UnaryMinusExp() {};

    public UnaryMinusExp(Location location, Exp argument) {
        this.location = location;
        this.argument = argument;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new UnaryMinusExp(location, substitute(argument, substitutions));
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the location variable. */
    public Location getLocation() {
        return location;
    }

    /** Returns the value of the argument variable. */
    public Exp getArgument() {
        return argument;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the argument variable to the specified value. */
    public void setArgument(Exp argument) {
        this.argument = argument;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitUnaryMinusExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("UnaryMinusExp\n");

        if (argument != null) {
            sb.append(argument.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        if (argument != null) {
            return argument.containsVar(varName, IsOldExp);
        }
        return false;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(argument);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        argument = e;
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof UnaryMinusExp)) {
            return false;
        }
        return true;
    }

    public void prettyPrint() {
        System.out.print("-(");
        argument.prettyPrint();
        System.out.print(")");
    }

    public Exp copy() {
        Exp newArgument = Exp.copy(argument);
        return new UnaryMinusExp(null, newArgument);
    }

}
