/*
 * CharExp.java
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

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;

public class CharExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The c member. */
    private Character c;

    // ===========================================================
    // Constructors
    // ===========================================================

    public CharExp() {};

    public CharExp(Location location, Character c) {
        this.location = location;
        this.c = c;
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

    /** Returns the value of the c variable. */
    public Character getValue() {
        return c;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the c variable to the specified value. */
    public void setValue(Character c) {
        this.c = c;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new CharExp(location, new Character(c));
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitCharExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("CharExp\n");

        if (c != null) {
            printSpace(indent + increment, sb);
            sb.append(c.toString() + "\n");
        }

        return sb.toString();
    }

    public String toString(int indent) {
        StringBuffer sb = new StringBuffer();
        printSpace(indent, sb);
        if (c != null) {
            sb.append(c.toString());
        }
        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression   
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    public List<Exp> getSubExpressions() {
        return new List<Exp>();
    }

    public void setSubExpression(int index, Exp e) {}

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof CharExp)) {
            return false;
        }
        if (c.charValue() != ((CharExp) e2).getValue().charValue()) {
            return false;
        }
        return true;
    }

    public void prettyPrint() {
        System.out.print(c.toString());
    }

    public Exp copy() {
        Character newC = c.charValue();
        return new CharExp(null, newC);
    }

    public Exp replace(Exp old, Exp replace) {
        if (!(old instanceof CharExp)) {
            return null;
        }
        else if (((CharExp) old).getValue().equals(c))
            return replace;
        else
            return null;
    }
}
