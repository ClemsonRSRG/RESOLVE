/*
 * FieldExp.java
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

public class FieldExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The structure member. */
    private Exp structure;

    /** The field member. */
    private Exp field;

    // ===========================================================
    // Constructors
    // ===========================================================

    public FieldExp() {};

    public FieldExp(Location location, Exp structure, Exp field) {
        this.location = location;
        this.structure = structure;
        this.field = field;
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

    /** Returns the value of the structure variable. */
    public Exp getStructure() {
        return structure;
    }

    /** Returns the value of the field variable. */
    public Exp getField() {
        return field;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the structure variable to the specified value. */
    public void setStructure(Exp structure) {
        this.structure = structure;
    }

    /** Sets the field variable to the specified value. */
    public void setField(Exp field) {
        this.field = field;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new FieldExp(location, substitute(structure, substitutions),
                substitute(field, substitutions));
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitFieldExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("FieldExp\n");

        if (structure != null) {
            sb.append(structure.asString(indent + increment, increment));
        }

        if (field != null) {
            sb.append(field.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression   
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Boolean found = false;
        if (structure != null) {
            found = structure.containsVar(varName, IsOldExp);
        }
        if (!found && field != null) {
            found = structure.containsVar(varName, IsOldExp);
        }
        return found;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(structure);
        list.add(field);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        switch (index) {
        case 0:
            structure = e;
            break;
        case 1:
            field = e;
            break;
        }
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof FieldExp)) {
            return false;
        }
        return true;
    }

}
