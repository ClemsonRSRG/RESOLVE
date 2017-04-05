/*
 * SetExp.java
 * ---------------------------------
 * Copyright (c) 2017
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

public class SetExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The var member. */
    private MathVarDec var;

    /** The where member. */
    private Exp where;

    /** The body member. */
    private Exp body;

    // ===========================================================
    // Constructors
    // ===========================================================

    public SetExp() {};

    public SetExp(Location location, MathVarDec var, Exp where, Exp body) {
        this.location = location;
        this.var = var;
        this.where = where;
        this.body = body;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new SetExp(location, var, substitute(where, substitutions),
                substitute(body, substitutions));
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

    /** Returns the value of the var variable. */
    public MathVarDec getVar() {
        return var;
    }

    /** Returns the value of the where variable. */
    public Exp getWhere() {
        return where;
    }

    /** Returns the value of the body variable. */
    public Exp getBody() {
        return body;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the var variable to the specified value. */
    public void setVar(MathVarDec var) {
        this.var = var;
    }

    /** Sets the where variable to the specified value. */
    public void setWhere(Exp where) {
        this.where = where;
    }

    /** Sets the body variable to the specified value. */
    public void setBody(Exp body) {
        this.body = body;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitSetExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("SetExp\n");

        if (var != null) {
            sb.append(var.asString(indent + increment, increment));
        }

        if (where != null) {
            sb.append(where.asString(indent + increment, increment));
        }

        if (body != null) {
            sb.append(body.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Boolean found = false;
        if (where != null) {
            found = where.containsVar(varName, IsOldExp);
        }
        if (!found && body != null) {
            found = body.containsVar(varName, IsOldExp);
        }
        return found;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(where);
        list.add(body);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        switch (index) {
        case 0:
            where = e;
            break;
        case 1:
            body = e;
            break;
        }
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof SetExp)) {
            return false;
        }
        return true;
    }

    public void prettyPrint() {
        System.out.print("{ ");
        var.prettyPrint();
        System.out.print(", ");
        if (where != null) {
            where.prettyPrint();
            System.out.print(", ");
        }
        body.prettyPrint();
        System.out.print(" }");
    }

    public Exp copy() {
        MathVarDec newVar = var.copy();
        List<VarExp> newVars = new List<VarExp>();

        Exp newWhere = null;
        if (where != null)
            newWhere = Exp.copy(where);
        Exp newBody = Exp.copy(body);
        return new SetExp(null, newVar, newWhere, newBody);
    }

}
