/**
 * ConstructedTy.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
/*
 * ConstructedTy.java
 * 
 * The Resolve Software Composition Workbench Project
 * 
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Location;

public class ConstructedTy extends Ty {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The qualifier member. */
    private PosSymbol qualifier;

    /** The name member. */
    private PosSymbol name;

    /** The args member. */
    private List<Ty> args;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ConstructedTy() {};

    public ConstructedTy(PosSymbol qualifier, PosSymbol name, List<Ty> args) {
        this.qualifier = qualifier;
        this.name = name;
        this.args = args;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    public Location getLocation() {
        return name.getLocation();
    }

    /** Returns the value of the qualifier variable. */
    public PosSymbol getQualifier() {
        return qualifier;
    }

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the args variable. */
    public List<Ty> getArgs() {
        return args;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the qualifier variable to the specified value. */
    public void setQualifier(PosSymbol qualifier) {
        this.qualifier = qualifier;
    }

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the args variable to the specified value. */
    public void setArgs(List<Ty> args) {
        this.args = args;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitConstructedTy(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ConstructedTy\n");

        if (qualifier != null) {
            sb.append(qualifier.asString(indent + increment, increment));
        }

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (args != null) {
            sb.append(args.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public void prettyPrint() {
        if (qualifier != null)
            System.out.print(qualifier.getName() + ".");
        System.out.print(name.getName());
        Iterator<Ty> it = args.iterator();
        System.out.print("(");
        if (it.hasNext()) {
            it.next().prettyPrint();
        }
        while (it.hasNext()) {
            System.out.print(", ");
            it.next().prettyPrint();
        }
        System.out.print(")");
    }

    public String toString(int indent) {
        StringBuffer sb = new StringBuffer();
        if (qualifier != null)
            sb.append(qualifier.getName() + ".");
        sb.append(name.getName());
        Iterator<Ty> it = args.iterator();
        sb.append("(");
        if (it.hasNext()) {
            sb.append(it.next().toString(0));
        }
        while (it.hasNext()) {
            sb.append(", ");
            sb.append(it.next().toString(0));
        }
        sb.append(")");
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (qualifier != null)
            sb.append(qualifier.getName() + ".");
        sb.append(name.getName());
        Iterator<Ty> it = args.iterator();
        sb.append("(");
        if (it.hasNext()) {
            sb.append(it.next().toString(0));
        }
        while (it.hasNext()) {
            sb.append(", ");
            sb.append(it.next().toString(0));
        }
        sb.append(")");
        return sb.toString();
    }

    public Ty copy() {
        PosSymbol newQualifier = null;
        if (qualifier != null)
            newQualifier = qualifier.copy();
        PosSymbol newName = name.copy();
        Iterator<Ty> it = args.iterator();
        List<Ty> newArgs = new List<Ty>();
        while (it.hasNext()) {
            newArgs.add(Ty.copy(it.next()));
        }
        return new ConstructedTy(newQualifier, newName, newArgs);
    }
}
