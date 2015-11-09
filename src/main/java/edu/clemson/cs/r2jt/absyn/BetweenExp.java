/**
 * BetweenExp.java
 * ---------------------------------
 * Copyright (c) 2015
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
import edu.clemson.cs.r2jt.collections.Iterator;

public class BetweenExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The lessExps member. */
    private List<Exp> lessExps;

    // ===========================================================
    // Constructors
    // ===========================================================

    public BetweenExp() {
    // Empty
    }

    public BetweenExp(Location location, List<Exp> lessExps) {
        this.location = location;
        this.lessExps = lessExps;
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

    /** Returns the value of the lessExps variable. */
    public List<Exp> getLessExps() {
        return lessExps;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the lessExps variable to the specified value. */
    public void setLessExps(List<Exp> lessExps) {
        this.lessExps = lessExps;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        List<Exp> newLessExps = new List<Exp>();
        for (Exp e : lessExps) {
            newLessExps.add(substitute(e, substitutions));
        }

        return new BetweenExp(location, newLessExps);
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitBetweenExp(this);
    }

    public Object clone() {
        BetweenExp clone = new BetweenExp();

        if (lessExps != null) {
            Iterator<Exp> i = lessExps.iterator();
            List<Exp> newLessExps = new List<Exp>();
            while (i.hasNext()) {
                newLessExps.add((Exp) Exp.clone(i.next()));
            }
            clone.setLessExps(newLessExps);
        }

        clone.setLocation(this.getLocation());
        return clone;
    }

    public Exp remember() {
        if (lessExps != null) {
            List<Exp> list = new List<Exp>();
            Iterator<Exp> i = this.lessExps.iterator();
            while (i.hasNext()) {
                list.add(i.next().remember());
            }
            this.lessExps = list;
        }

        return this;
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("BetweenExp\n");

        if (lessExps != null) {
            sb.append(lessExps.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public String toString(int indent) {
        // 	Environment   env	= Environment.getInstance();
        // 	if(env.isabelle()){return toIsabelleString(indent);};    	
        StringBuffer sb = new StringBuffer();
        printSpace(indent, sb);
        List<Exp> list = lessExps;
        Iterator<Exp> i = list.iterator();
        while (i.hasNext()) {
            sb.append(i.next().toString(0));
            if (i.hasNext()) {
                sb.append(" and ");
            }
        }
        return sb.toString();
    }

    public String toIsabelleString(int indent) {
        StringBuffer sb = new StringBuffer();
        printSpace(indent, sb);
        List<Exp> list = lessExps;
        Iterator<Exp> i = list.iterator();
        while (i.hasNext()) {
            sb.append(i.next().toString(0));
            if (i.hasNext()) {
                sb.append(" & ");
            }

        }
        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression   
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Iterator<Exp> i = lessExps.iterator();
        while (i.hasNext()) {
            Exp temp = i.next();
            if (temp != null) {
                if (temp.containsVar(varName, IsOldExp)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Exp> getSubExpressions() {
        return lessExps;
    }

    public void setSubExpression(int index, Exp e) {
        lessExps.set(index, e);
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof BetweenExp)) {
            return false;
        }
        return true;
    }

    /**
     * <p>I don't really understand what a "BetweenExp" is, so for now its
     * 'equivalent' implementation just checks to see if all subexpressions
     * exist as a subexpression in <code>e</code>.  -HwS</p>
     */
    public boolean equivalent(Exp e) {
        boolean retval = (e instanceof BetweenExp);

        if (retval) {
            BetweenExp eAsBetweenExp = (BetweenExp) e;
            Iterator<Exp> eSubexpressions =
                    eAsBetweenExp.getSubExpressions().iterator();
            Iterator<Exp> mySubexpressions;
            Exp curExp;
            while (retval && eSubexpressions.hasNext()) {
                curExp = eSubexpressions.next();
                mySubexpressions = lessExps.iterator();
                retval = false;
                while (!retval && mySubexpressions.hasNext()) {
                    retval = curExp.equivalent(mySubexpressions.next());
                }
            }
        }

        return retval;
    }

    public Exp replace(Exp old, Exp replace) {
        if (old instanceof BetweenExp) {
            return null;
        }
        else {
            lessExps =
                    replaceVariableInExpListWithExp(this.lessExps, old, replace);
            return this;
        }
    }

    private List<Exp> replaceVariableInExpListWithExp(List<Exp> list, Exp old,
            Exp replacement) {
        // 	AssertiveCode assertion = new AssertiveCode();
        Iterator<Exp> i = list.iterator();
        while (i.hasNext()) {
            Exp exp = (Exp) i.next();
            Exp tmp = null;
            if (exp != null)
                tmp = Exp.replace(exp, old, replacement);
            i.previous();
            i.remove();
            if (tmp != null)
                i.add(tmp);
            else
                i.add(exp);
        }

        return list;
    }

    public void prettyPrint() {
        Iterator<Exp> it = lessExps.iterator();
        if (it.hasNext()) {
            it.next().prettyPrint();
        }
        while (it.hasNext()) {
            System.out.print(" and ");
            it.next().prettyPrint();
        }
    }

    public Exp copy() {
        Iterator<Exp> it = lessExps.iterator();
        List<Exp> newLessExps = new List<Exp>();
        while (it.hasNext()) {
            newLessExps.add(Exp.copy(it.next()));
        }
        Exp result = new BetweenExp(null, newLessExps);
        return result;
    }

}
