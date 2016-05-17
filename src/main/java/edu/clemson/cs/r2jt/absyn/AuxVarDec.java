/**
 * AuxVarDec.java
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
 * VarDec.java
 * 
 * The Resolve Software Composition Workbench Project
 * 
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;

public class AuxVarDec extends Dec {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The ty member. */
    private Ty ty;

    // ===========================================================
    // Constructors
    // ===========================================================

    public AuxVarDec() {};

    public AuxVarDec(PosSymbol name, Ty ty) {
        this.name = name;
        this.ty = ty;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the ty variable. */
    public Ty getTy() {
        return ty;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the ty variable to the specified value. */
    public void setTy(Ty ty) {
        this.ty = ty;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitAuxVarDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("VarDec\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (ty != null) {
            sb.append(ty.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public Object clone() {
        AuxVarDec clone = new AuxVarDec();
        clone.setName(createPosSymbol(this.getName().toString()));
        clone.setTy(Ty.copy(getTy()));
        return clone;
    }

    private PosSymbol createPosSymbol(String name) {
        PosSymbol posSym = new PosSymbol();
        posSym.setSymbol(Symbol.symbol(name));
        return posSym;

    }

    public VarDec toVarDec() {
        VarDec var = new VarDec();
        var.setName(this.name);
        var.setTy(this.ty);
        return var;
    }

}
