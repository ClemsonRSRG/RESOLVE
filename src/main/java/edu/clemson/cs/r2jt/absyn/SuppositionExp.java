/*
 * SuppositionExp.java
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

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;

public class SuppositionExp extends LineNumberedExp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The exp member. */
    private Exp exp;

    private List<MathVarDec> vars;

    // ===========================================================
    // Constructors
    // ===========================================================

    public SuppositionExp() {
        super(null);
    }

    public SuppositionExp(Location location, PosSymbol lineNum, Exp exp,
            List<MathVarDec> vars) {
        super(lineNum);
        this.location = location;
        this.exp = exp;
        this.vars = vars;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new SuppositionExp(location, this.getLineNum(), substitute(exp,
                substitutions), vars);
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

    /** Returns the value of the exp variable. */
    public Exp getExp() {
        return exp;
    }

    public List<MathVarDec> getVars() {
        return vars;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the exp variable to the specified value. */
    public void setExp(Exp exp) {
        this.exp = exp;
    }

    public void setVars(List<MathVarDec> vars) {
        this.vars = vars;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitSuppositionExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("SuppositionExp\n");

        sb.append("");

        if (myLineNumber != null) {
            printSpace(indent + increment, sb);
            sb.append("Line: " + myLineNumber.asString(0, increment));
        }

        if (exp != null) {
            sb.append(exp.asString(indent + increment, increment));
        }

        if (vars != null) {
            sb.append("Vars: ");
            Iterator<MathVarDec> it = vars.iterator();
            while (it.hasNext()) {
                sb.append(it.next().asString(indent, increment));
            }
        }

        return sb.toString();
    }

    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(exp);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        exp = e;
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof SuppositionExp)) {
            return false;
        }
        return true;
    }

    public void prettyPrint() {
        if (myLineNumber != null)
            System.out.print(myLineNumber.getName() + ": ");
        System.out.print("Supposition ");
        Iterator<MathVarDec> it = vars.iterator();
        boolean printed = false;
        if (it.hasNext()) {
            it.next().prettyPrint();
            printed = true;
        }
        while (it.hasNext()) {
            System.out.print(", ");
            it.next().prettyPrint();
            printed = true;
        }
        if (exp != null) {
            if (printed)
                System.out.print(" and ");
            exp.prettyPrint();
        }
    }

    public Exp copy() {
        PosSymbol newLineNum = null;
        if (myLineNumber != null)
            newLineNum = myLineNumber.copy();
        Exp newExp = Exp.copy(exp);
        Iterator<MathVarDec> it = vars.iterator();
        List<MathVarDec> newVars = new List<MathVarDec>();
        while (it.hasNext()) {
            newVars.add(it.next().copy());
        }
        return new SuppositionExp(null, newLineNum, newExp, newVars);
    }

}
