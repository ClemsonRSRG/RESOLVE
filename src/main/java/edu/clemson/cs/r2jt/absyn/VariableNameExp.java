/*
 * VariableNameExp.java
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
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;

public class VariableNameExp extends VariableExp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The qualifier member. */
    private PosSymbol qualifier;

    /** The name member. */
    private PosSymbol name;

    // ===========================================================
    // Constructors
    // ===========================================================

    public VariableNameExp() {
        this(null, null, null);
    }

    public VariableNameExp(Location location, PosSymbol qualifier,
            PosSymbol name) {
        this.location = location;
        this.qualifier = qualifier;
        this.name = name;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new VariableNameExp(location, qualifier, name);
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

    /** Returns the value of the qualifier variable. */
    public PosSymbol getQualifier() {
        return qualifier;
    }

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the qualifier variable to the specified value. */
    public void setQualifier(PosSymbol qualifier) {
        this.qualifier = qualifier;
    }

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitVariableNameExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("VariableNameExp\n");

        if (qualifier != null) {
            sb.append(qualifier.asString(indent + increment, increment));
        }

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a text string of the Variable */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (qualifier != null) {
            sb.append(qualifier.toString() + ".");
        }

        if (name != null) {
            sb.append(name.toString());
        }

        return sb.toString();
    }

    public String toString() {
        return name.getName();
    }

    /** Returns true if the variable is found in any sub expression
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    public Object clone() {
        VariableNameExp clone = new VariableNameExp();
        clone.setName(createPosSymbol(this.getName().toString()));
        clone.setQualifier(this.getQualifier());
        clone.setLocation(this.getLocation());
        return clone;
    }

    private PosSymbol createPosSymbol(String name) {
        PosSymbol posSym = new PosSymbol();
        posSym.setSymbol(Symbol.symbol(name));
        return posSym;

    }

    public Exp replace(Exp old, Exp replacement) {
        if (name != null) {
            if (old instanceof VarExp) {
                if (((VarExp) old).getName().toString().equals(name.toString())) {
                    return (Exp) Exp.clone(replacement);
                }
            }
        }
        return null;
    }

    public List<Exp> getSubExpressions() {
        return new List<Exp>();
    }

    public void setSubExpression(int index, Exp e) {}

    public Exp copy() {
        Exp result = new VariableNameExp(location, qualifier, name);
        result.setMathType(myMathType);
        result.setMathTypeValue(myMathTypeValue);

        return result;
    }

    public boolean equivalent(Exp e) {
        boolean retval = e instanceof VariableNameExp;

        if (retval) {
            VariableNameExp eAsVNE = (VariableNameExp) e;

            retval =
                    posSymbolEquivalent(qualifier, eAsVNE.qualifier)
                            && posSymbolEquivalent(name, eAsVNE.name);
        }

        return retval;
    }

}
