/*
 * AlternativeExp.java
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
import edu.clemson.cs.r2jt.collections.Iterator;

public class AlternativeExp extends Exp {

    // Variables

    /** The location member. */
    private Location location;

    /** The alternatives member. */
    private List<AltItemExp> alternatives;

    // Constructors

    public AlternativeExp() {};

    public AlternativeExp(Location location, List<AltItemExp> alternatives) {
        this.location = location;
        this.alternatives = alternatives;

        boolean foundOtherwise = false;
        for (AltItemExp e : alternatives) {
            foundOtherwise = foundOtherwise || (e.getTest() == null);
        }
        if (!foundOtherwise) {
            throw new IllegalArgumentException("Must have otherwise.");
        }
    }

    // Accessor Methods

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the location variable. */
    public Location getLocation() {
        return location;
    }

    /** Returns the value of the alternatives variable. */
    public List<AltItemExp> getAlternatives() {
        return alternatives;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the alternatives variable to the specified value. */
    public void setAlternatives(List<AltItemExp> alternatives) {
        this.alternatives = alternatives;
    }

    // Public Methods

    public boolean equivalent(Exp e) {
        boolean result = e instanceof AlternativeExp;

        if (result) {
            AlternativeExp eAsAlternativeExp = (AlternativeExp) e;

            Iterator<AltItemExp> thisAltItems = alternatives.iterator();
            Iterator<AltItemExp> eAltItems =
                    eAsAlternativeExp.alternatives.iterator();

            while (result && thisAltItems.hasNext() && eAltItems.hasNext()) {
                result &= thisAltItems.next().equivalent(eAltItems.next());
            }

            //Both had better have run out at the same time
            result &= (!thisAltItems.hasNext()) && (!eAltItems.hasNext());
        }

        return result;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        AlternativeExp retval = new AlternativeExp();
        List<AltItemExp> newAlternatives = new List<AltItemExp>();

        for (Exp e : alternatives) {
            newAlternatives.add((AltItemExp) substitute(e, substitutions));
        }

        retval.setAlternatives(newAlternatives);
        retval.setLocation(location);

        return retval;
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitAlternativeExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("AlternativeExp\n");

        if (alternatives != null) {
            sb.append(alternatives.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Iterator<AltItemExp> i = alternatives.iterator();
        while (i.hasNext()) {
            AltItemExp temp = i.next();
            if (temp != null) {
                if (temp.containsVar(varName, IsOldExp)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        Iterator<AltItemExp> altIt = alternatives.iterator();
        while (altIt.hasNext()) {
            list.add((Exp) (altIt.next()));
        }
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        alternatives.set(index, (AltItemExp) e);
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof AlternativeExp)) {
            return false;
        }
        return true;
    }

    public void prettyPrint() {
        System.out.print("{{");
        Iterator<AltItemExp> it = alternatives.iterator();
        while (it.hasNext()) {
            it.next().prettyPrint();
            System.out.println();
        }
        System.out.print("}}");
    }

    public String toString(int index) {
        StringBuffer sb = new StringBuffer();
        sb.append("{{");
        Iterator<AltItemExp> it = alternatives.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
            sb.append("\n");

        }
        sb.append("}}");
        return sb.toString();
    }

    public Exp replace(Exp old, Exp replace) {
        AlternativeExp result = (AlternativeExp) Exp.copy(this);

        List<AltItemExp> itemsCopy = new List<AltItemExp>();
        for (AltItemExp item : alternatives) {
            itemsCopy.add((AltItemExp) Exp.replace(item, old, replace));
        }
        result.alternatives = itemsCopy;

        return result;
    }

    public Object clone() {
        List<AltItemExp> newAlternatives = new List<AltItemExp>();
        Iterator<AltItemExp> it = alternatives.iterator();
        while (it.hasNext()) {
            newAlternatives.add((AltItemExp) (Exp.clone(it.next())));
        }

        Exp result = new AlternativeExp(null, newAlternatives);

        return result;
    }

    public Exp copy() {
        List<AltItemExp> newAlternatives = new List<AltItemExp>();
        Iterator<AltItemExp> it = alternatives.iterator();
        while (it.hasNext()) {
            newAlternatives.add((AltItemExp) (Exp.copy(it.next())));
        }

        Exp result = new AlternativeExp(null, newAlternatives);

        return result;
    }

    public Exp remember() {
        AlternativeExp result = (AlternativeExp) Exp.copy(this);

        List<AltItemExp> itemsCopy = new List<AltItemExp>();
        for (AltItemExp item : alternatives) {
            itemsCopy.add((AltItemExp) item.remember());
        }
        result.alternatives = itemsCopy;

        return result;
    }

}
