/*
 * PerformanceTypeDec.java
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

public class PerformanceTypeDec extends Dec {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The model member. */
    private Ty model;

    /** The constraint member. */
    private Exp constraint;

    /** The initialization member. */
    private PerformanceInitItem perf_initialization;

    /** The finalization member. */
    private PerformanceFinalItem perf_finalization;

    // ===========================================================
    // Constructors
    // ===========================================================

    public PerformanceTypeDec() {};

    public PerformanceTypeDec(PosSymbol name, Ty model, Exp constraint,
            PerformanceInitItem perf_initialization,
            PerformanceFinalItem perf_finalization) {
        this.name = name;
        this.model = model;
        this.constraint = constraint;
        this.perf_initialization = perf_initialization;
        this.perf_finalization = perf_finalization;
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

    /** Returns the value of the constraint variable. */
    public Exp getConstraint() {
        return constraint;
    }

    /** Returns the value of the perf_initialization variable. */
    public PerformanceInitItem getPerf_Initialization() {
        return perf_initialization;
    }

    /** Returns the value of the perf_finalization variable. */
    public PerformanceFinalItem getPerf_Finalization() {
        return perf_finalization;
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

    /** Sets the constraint variable to the specified value. */
    public void setConstraint(Exp constraint) {
        this.constraint = constraint;
    }

    /** Sets the initialization variable to the specified value. */
    public void
            setPerf_Initialization(PerformanceInitItem perf_initialization) {
        this.perf_initialization = perf_initialization;
    }

    /** Sets the finalization variable to the specified value. */
    public void setPerf_Finalization(PerformanceFinalItem perf_finalization) {
        this.perf_finalization = perf_finalization;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitPerformanceTypeDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("PerformanceTypeDec\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (model != null) {
            sb.append(model.asString(indent + increment, increment));
        }

        if (constraint != null) {
            sb.append(constraint.asString(indent + increment, increment));
        }

        if (perf_initialization != null) {
            sb.append(perf_initialization.asString(indent + increment,
                    increment));
        }

        if (perf_finalization != null) {
            sb.append(
                    perf_finalization.asString(indent + increment, increment));
        }

        return sb.toString();
    }
}
