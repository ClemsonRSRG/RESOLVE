/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer. 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution. 
 *   * Neither the name of the Clemson University nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University.  Contributors to the initial version are:
 * 
 *     Steven Atkinson
 *     Greg Kulczycki
 *     Kunal Chopra
 *     John Hunt
 *     Heather Keown
 *     Ben Markle
 *     Kim Roche
 *     Murali Sitaraman
 */
/*
 * TypeDec.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Mode;
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

    public TypeDec(
            PosSymbol name,
            Ty model,
            PosSymbol exemplar,
            Exp constraint,
            InitItem initialization,
            FinalItem finalization)
    {
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
    public PosSymbol getName() { return name; }

    /** Returns the value of the model variable. */
    public Ty getModel() { return model; }

    /** Returns the value of the exemplar variable. */
    public PosSymbol getExemplar() { return exemplar; }

    /** Returns the value of the constraint variable. */
    public Exp getConstraint() { return constraint; }

    /** Returns the value of the initialization variable. */
    public InitItem getInitialization() { return initialization; }

    /** Returns the value of the finalization variable. */
    public FinalItem getFinalization() { return finalization; }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) { this.name = name; }

    /** Sets the model variable to the specified value. */
    public void setModel(Ty model) { this.model = model; }

    /** Sets the exemplar variable to the specified value. */
    public void setExemplar(PosSymbol exemplar) { this.exemplar = exemplar; }

    /** Sets the constraint variable to the specified value. */
    public void setConstraint(Exp constraint) { this.constraint = constraint; }

    /** Sets the initialization variable to the specified value. */
    public void setInitialization(InitItem initialization) { this.initialization = initialization; }

    /** Sets the finalization variable to the specified value. */
    public void setFinalization(FinalItem finalization) { this.finalization = finalization; }

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
            sb.append(name.asString(indent+increment,increment));
        }

        if (model != null) {
            sb.append(model.asString(indent+increment,increment));
        }

        if (exemplar != null) {
            sb.append(exemplar.asString(indent+increment,increment));
        }

        if (constraint != null) {
            sb.append(constraint.asString(indent+increment,increment));
        }

        if (initialization != null) {
            sb.append(initialization.asString(indent+increment,increment));
        }

        if (finalization != null) {
            sb.append(finalization.asString(indent+increment,increment));
        }

        return sb.toString();
    }
}
