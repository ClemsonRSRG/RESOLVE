/**
 * FunctionArgList.java
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

public class FunctionArgList extends ResolveConceptualElement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The arguments member. */
    private List<Exp> arguments;

    // ===========================================================
    // Constructors
    // ===========================================================

    public FunctionArgList() {};

    public FunctionArgList(List<Exp> arguments) {
        this.arguments = arguments;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the arguments variable. */
    public List<Exp> getArguments() {
        return arguments;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the arguments variable to the specified value. */
    public void setArguments(List<Exp> arguments) {
        this.arguments = arguments;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Location getLocation() {
        return arguments.get(0).getLocation();
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitFunctionArgList(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("FunctionArgList\n");

        if (arguments != null) {
            sb.append(arguments.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public Object clone() {
        FunctionArgList clone = new FunctionArgList();
        Iterator<Exp> i = arguments.iterator();
        List<Exp> arg = new List<Exp>();
        while (i.hasNext()) {
            Exp tmp = i.next();
            if (tmp != null)
                arg.add((Exp) Exp.clone(tmp));
        }
        clone.setArguments(arg);
        return clone;
    }

    public void prettyPrint() {
        Iterator<Exp> it = arguments.iterator();
        if (it.hasNext()) {
            it.next().prettyPrint();
        }
        while (it.hasNext()) {
            System.out.print(", ");
            it.next().prettyPrint();
        }
    }

    public FunctionArgList copy() {
        Iterator<Exp> it = arguments.iterator();
        List<Exp> newArguments = new List<Exp>();
        while (it.hasNext()) {
            newArguments.add(Exp.copy(it.next()));
        }
        return new FunctionArgList(newArguments);
    }

}
