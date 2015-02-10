/**
 * IntegerExp.java
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
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;

public class IntegerExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    private PosSymbol qualifier;

    /** The value member. */
    private int value;

    // ===========================================================
    // Constructors
    // ===========================================================

    public IntegerExp() {};

    public IntegerExp(Location location, PosSymbol qualifier, int value) {
        this.location = location;
        this.qualifier = qualifier;
        this.value = value;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        Exp retval = new IntegerExp(location, qualifier, value);
        retval.setMathType(getMathType());
        retval.setMathTypeValue(getMathTypeValue());
        return retval;
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

    public PosSymbol getQualifier() {
        return qualifier;
    }

    /** Returns the value of the value variable. */
    public int getValue() {
        return value;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    public void setQualifier(PosSymbol qualifier) {
        this.qualifier = qualifier;
    }

    /** Sets the value variable to the specified value. */
    public void setValue(int value) {
        this.value = value;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitIntegerExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("IntegerExp\n");

        if (qualifier != null) {
            printSpace(indent + increment, sb);
            sb.append(qualifier + "\n");
        }

        printSpace(indent + increment, sb);
        sb.append(value + "\n");

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        if (qualifier != null) {
            sb.append(qualifier + ".");
        }

        sb.append(value);

        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    public Object clone() {
        IntegerExp clone = new IntegerExp();
        clone.setQualifier(this.qualifier);
        clone.setValue(this.value);
        clone.setLocation(this.getLocation());
        clone.setMathType(getMathType());
        clone.setMathTypeValue(getMathTypeValue());
        return clone;
    }

    public List<Exp> getSubExpressions() {
        return new List<Exp>();
    }

    public void setSubExpression(int index, Exp e) {

    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof IntegerExp)) {
            return false;
        }
        if (qualifier != null && ((IntegerExp) (e2)).getQualifier() != null) {
            if (!(qualifier.equals(((IntegerExp) e2).getQualifier().getName()))) {
                return false;
            }
        }
        if (value != ((IntegerExp) e2).getValue()) {
            return false;
        }
        return true;
    }

    public Exp replace(Exp old, Exp replace) {
        if (!(old instanceof IntegerExp)) {
            return null;
        }
        else if (((IntegerExp) old).getValue() == value)
            return replace;
        else
            return null;
    }

    public void prettyPrint() {
        if (qualifier != null) {
            System.out.print(qualifier.getName() + ".");
        }
        System.out.print(value);
    }

    public Exp copy() {
        Exp retval = new IntegerExp(null, qualifier, value);
        retval.setMathType(getMathType());
        retval.setMathTypeValue(getMathTypeValue());
        return retval;
    }

    public boolean equivalent(Exp e) {
        boolean retval = e instanceof IntegerExp;

        if (retval) {
            IntegerExp eAsIntegerExp = (IntegerExp) e;
            retval = (value == eAsIntegerExp.value);
        }

        return retval;
    }
}
