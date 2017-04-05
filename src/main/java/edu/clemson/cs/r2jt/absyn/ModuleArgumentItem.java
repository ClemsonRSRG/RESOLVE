/*
 * ModuleArgumentItem.java
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

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;

public class ModuleArgumentItem extends ResolveConceptualElement {

    // ===========================================================
    // Variables
    // ===========================================================

    /**
     * <p>If this argument names a program type, this will be set by the 
     * populator to point to the correct type.</p>
     */
    private PTType myTypeValue;

    /**
     * <p>So if the expression to be passed happens to just be a name, rather
     * than setting evalExp with a normal VarExp so that all the usual 
     * mechanisms can be used, we just set the name parameter.  I have no words
     * for how angry that makes me, but I have no time to fix it.  This field
     * will hold the type of the "expression" represented by </p>
     */
    private MTType myMathType;

    /** The qualifier member. */
    private PosSymbol qualifier;

    /** The name member. */
    private PosSymbol name;

    /** The evalExp member. */
    private ProgramExp evalExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ModuleArgumentItem() {};

    public ModuleArgumentItem(PosSymbol qualifier, PosSymbol name,
            ProgramExp evalExp) {
        this.qualifier = qualifier;
        this.name = name;
        this.evalExp = evalExp;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    public PTType getProgramTypeValue() {
        return myTypeValue;
    }

    public void setMathType(MTType t) {
        myMathType = t;
    }

    public MTType getMathType() {
        return myMathType;
    }

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

    /** Returns the value of the evalExp variable. */
    public ProgramExp getEvalExp() {
        return evalExp;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    public void setProgramTypeValue(PTType t) {
        myTypeValue = t;
    }

    /** Sets the qualifier variable to the specified value. */
    public void setQualifier(PosSymbol qualifier) {
        this.qualifier = qualifier;
    }

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the evalExp variable to the specified value. */
    public void setEvalExp(ProgramExp evalExp) {
        this.evalExp = evalExp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitModuleArgumentItem(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ModuleArgumentItem\n");

        if (qualifier != null) {
            sb.append(qualifier.asString(indent + increment, increment));
        }

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (evalExp != null) {
            sb.append(evalExp.asString(indent + increment, increment));
        }

        return sb.toString();
    }
}
