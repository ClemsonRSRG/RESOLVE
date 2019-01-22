/*
 * CartProdTy.java
 * ---------------------------------
 * Copyright (c) 2019
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

public class CartProdTy extends Ty {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The fields member. */
    private List<MathVarDec> fields;

    // ===========================================================
    // Constructors
    // ===========================================================

    public CartProdTy() {};

    public CartProdTy(Location location, List<MathVarDec> fields) {
        this.location = location;
        this.fields = fields;
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

    /** Returns the value of the fields variable. */
    public List<MathVarDec> getFields() {
        return fields;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the fields variable to the specified value. */
    public void setFields(List<MathVarDec> fields) {
        this.fields = fields;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitCartProdTy(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("CartProdTy\n");

        if (fields != null) {
            sb.append(fields.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public void prettyPrint() {
        Iterator<MathVarDec> it = fields.iterator();
        if (it.hasNext()) {
            it.next().prettyPrint();
        }
        while (it.hasNext()) {
            System.out.print(" x ");
            it.next().prettyPrint();
        }
    }

    public String toString(int indent) {
        Iterator<MathVarDec> it = fields.iterator();
        StringBuffer sb = new StringBuffer();
        if (it.hasNext()) {
            sb.append((it.next()).toString(0));
        }
        while (it.hasNext()) {
            sb.append(" x ");
            sb.append((it.next()).toString(0));
        }
        return sb.toString();
    }

    public Ty copy() {
        Iterator<MathVarDec> it = fields.iterator();
        List<MathVarDec> newFields = new List<MathVarDec>();
        while (it.hasNext()) {
            newFields.add(it.next().copy());
        }
        return new CartProdTy(null, newFields);
    }

}
