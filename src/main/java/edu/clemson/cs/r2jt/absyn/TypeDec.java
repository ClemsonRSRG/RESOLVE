/*
 * TypeDec.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.data.PosSymbol;

public class TypeDec extends Dec {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The model member. */
    private Ty model;

    /** The exemplar member. */
    private PosSymbol exemplar;

    /** The constraint member. */
    private Exp constraint;

    /** The initialization member. */
    private InitItem initialization;

    /** The finalization member. */
    private FinalItem finalization;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TypeDec() {};

    public TypeDec(PosSymbol name, Ty model, PosSymbol exemplar, Exp constraint,
            InitItem initialization, FinalItem finalization) {
        this.name = name;
        this.model = model;
        this.exemplar = exemplar;
        this.constraint = constraint;
        this.initialization = initialization;
        this.finalization = finalization;
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

    /** Returns the value of the model variable. */
    public Ty getModel() {
        return model;
    }

    /** Returns the value of the exemplar variable. */
    public PosSymbol getExemplar() {
        return exemplar;
    }

    /** Returns the value of the constraint variable. */
    public Exp getConstraint() {
        return constraint;
    }

    /** Returns the value of the initialization variable. */
    public InitItem getInitialization() {
        return initialization;
    }

    /** Returns the value of the finalization variable. */
    public FinalItem getFinalization() {
        return finalization;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the model variable to the specified value. */
    public void setModel(Ty model) {
        this.model = model;
    }

    /** Sets the exemplar variable to the specified value. */
    public void setExemplar(PosSymbol exemplar) {
        this.exemplar = exemplar;
    }

    /** Sets the constraint variable to the specified value. */
    public void setConstraint(Exp constraint) {
        this.constraint = constraint;
    }

    /** Sets the initialization variable to the specified value. */
    public void setInitialization(InitItem initialization) {
        this.initialization = initialization;
    }

    /** Sets the finalization variable to the specified value. */
    public void setFinalization(FinalItem finalization) {
        this.finalization = finalization;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitTypeDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("TypeDec\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (model != null) {
            sb.append(model.asString(indent + increment, increment));
        }

        if (exemplar != null) {
            sb.append(exemplar.asString(indent + increment, increment));
        }

        if (constraint != null) {
            sb.append(constraint.asString(indent + increment, increment));
        }

        if (initialization != null) {
            sb.append(initialization.asString(indent + increment, increment));
        }

        if (finalization != null) {
            sb.append(finalization.asString(indent + increment, increment));
        }

        return sb.toString();
    }
}
