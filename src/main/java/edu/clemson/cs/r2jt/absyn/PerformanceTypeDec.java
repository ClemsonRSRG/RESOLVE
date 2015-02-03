/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
    public void setPerf_Initialization(PerformanceInitItem perf_initialization) {
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
            sb
                    .append(perf_finalization.asString(indent + increment,
                            increment));
        }

        return sb.toString();
    }
}
