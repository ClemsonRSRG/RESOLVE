/**
 * SuppositionDeductionExp.java
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

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;

public class SuppositionDeductionExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The supposition member. */
    private SuppositionExp supposition;

    /** The body member. */
    private List<Exp> body;

    /** The deduction member. */
    private DeductionExp deduction;

    // ===========================================================
    // Constructors
    // ===========================================================

    public SuppositionDeductionExp() {};

    public SuppositionDeductionExp(Location location,
            SuppositionExp supposition, List<Exp> body, DeductionExp deduction) {
        this.location = location;
        this.supposition = supposition;
        this.body = body;
        this.deduction = deduction;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        List<Exp> newBody = new List<Exp>();
        for (Exp e : body) {
            newBody.add(substitute(e, substitutions));
        }

        return new SuppositionDeductionExp(location,
                (SuppositionExp) substitute(supposition, substitutions),
                newBody, (DeductionExp) substitute(deduction, substitutions));
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

    /** Returns the value of the supposition variable. */
    public SuppositionExp getSupposition() {
        return supposition;
    }

    /** Returns the value of the body variable. */
    public List<Exp> getBody() {
        return body;
    }

    /** Returns the value of the deduction variable. */
    public DeductionExp getDeduction() {
        return deduction;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the value of the location variable. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the value of the supposition variable. */
    public void setSupposition(SuppositionExp supposition) {
        this.supposition = supposition;
    }

    /** Sets the value of the body variable. */
    public void setBody(List<Exp> body) {
        this.body = body;
    }

    /** Sets the value of the deduction variable. */
    public void setDeduction(DeductionExp deduction) {
        this.deduction = deduction;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitSuppositionDeductionExp(this);
    }

    /** Accepts a TypeResolutionVisitor. */
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        return v.getSuppositionDeductionExpType(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("SuppositionDeductionExp\n");

        if (supposition != null) {
            printSpace(indent + increment, sb);
            sb.append("SUPPOSITION\n");
            sb.append(supposition.asString(indent + increment, increment));
        }

        if (body != null) {
            printSpace(indent + increment, sb);
            sb.append("BODY of SUPDEDUC\n");
            sb.append(body.asString(indent + increment, increment));
        }

        if (deduction != null) {
            printSpace(indent + increment, sb);
            sb.append("DEDUCTION\n");
            sb.append(deduction.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(supposition);
        list.addAll(body);
        list.add(deduction);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        if (index == 0) {
            supposition = (SuppositionExp) e;
        }
        else if (index < body.size()) {
            body.set(index - 1, e);
        }
        else {
            deduction = (DeductionExp) e;
        }
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof SuppositionDeductionExp)) {
            return false;
        }
        return true;
    }

    public void prettyPrint() {
        supposition.prettyPrint();
        System.out.println();
        Iterator<Exp> it = body.iterator();
        while (it.hasNext()) {
            it.next().prettyPrint();
            System.out.println();
        }
        deduction.prettyPrint();
    }

    public Exp copy() {
        SuppositionExp newSupposition =
                (SuppositionExp) (Exp.copy(supposition));
        DeductionExp newDeduction = (DeductionExp) (Exp.copy(deduction));
        Iterator<Exp> it = body.iterator();
        List<Exp> newBody = new List<Exp>();
        while (it.hasNext()) {
            newBody.add(Exp.copy(it.next()));
        }
        return new SuppositionDeductionExp(null, newSupposition, newBody,
                newDeduction);
    }

}
