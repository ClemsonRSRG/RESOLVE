/*
 * RepresentationDec.java
 * ---------------------------------
 * Copyright (c) 2020
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

public class RepresentationDec extends Dec {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The representation member. */
    private Ty representation;

    /** The convention member. */
    private Exp convention;

    /** The correspondence member. */
    private Exp correspondence;

    /** The initialization member. */
    private InitItem initialization;

    /** The finalization member. */
    private FinalItem finalization;

    // ===========================================================
    // Constructors
    // ===========================================================

    public RepresentationDec() {};

    public RepresentationDec(PosSymbol name, Ty representation, Exp convention,
            Exp correspondence, InitItem initialization,
            FinalItem finalization) {
        this.name = name;
        this.representation = representation;
        this.convention = convention;
        this.correspondence = correspondence;
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

    /** Returns the value of the representation variable. */
    public Ty getRepresentation() {
        return representation;
    }

    /** Returns the value of the convention variable. */
    public Exp getConvention() {
        return convention;
    }

    /** Returns the value of the correspondence variable. */
    public Exp getCorrespondence() {
        return correspondence;
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

    /** Sets the representation variable to the specified value. */
    public void setRepresentation(Ty representation) {
        this.representation = representation;
    }

    /** Sets the convention variable to the specified value. */
    public void setConvention(Exp convention) {
        this.convention = convention;
    }

    /** Sets the correspondence variable to the specified value. */
    public void setCorrespondence(Exp correspondence) {
        this.correspondence = correspondence;
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
        v.visitRepresentationDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("RepresentationDec\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (representation != null) {
            sb.append(representation.asString(indent + increment, increment));
        }

        if (convention != null) {
            sb.append(convention.asString(indent + increment, increment));
        }

        if (correspondence != null) {
            sb.append(correspondence.asString(indent + increment, increment));
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
