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
 * MathAssertionDec.java 
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

public class MathAssertionDec extends Dec {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final int AXIOM = 1;
    public static final int THEOREM = 2;
    public static final int PROPERTY = 3;
    public static final int LEMMA = 4;
    public static final int COROLLARY = 5;
    
    public static enum TheoremSubtype {NONE, ASSOCIATIVITY, COMMUTATIVITY};

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The kind member. */
    private int kind;

    /** The assertion member. */
    private Exp assertion;
    
    /**
     * <p>For <em>theorem</em>s only, defines any special properties of the
     * theorem, such as if it is flagged as an associativity or commutativity
     * theorem in the definition.</p>
     */
    private TheoremSubtype mySubtype;

    // ===========================================================
    // Constructors
    // ===========================================================

    public MathAssertionDec() {};

    public MathAssertionDec(
            PosSymbol name,
            int kind,
            Exp assertion)
    {
        this.name = name;
        this.kind = kind;
        this.assertion = assertion;
        mySubtype = TheoremSubtype.NONE;
    }
    
    public MathAssertionDec(
            PosSymbol name,
            Exp assertion, TheoremSubtype theoremSubtype)
    {
        this.name = name;
        this.kind = THEOREM;
        this.assertion = assertion;
        mySubtype = theoremSubtype;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the name variable. */
    public PosSymbol getName() { return name; }

    /** Returns the value of the kind variable. */
    public int getKind() { return kind; }

    /** Returns the value of the assertion variable. */
    public Exp getAssertion() { return assertion; }
    
    /**
     * <p>Returns the specific subtype of the theorem reprsented by this
     * assertion.</p>
     */
    public TheoremSubtype getTheoremSubtype() { 
    	return mySubtype;
	}

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) { this.name = name; }

    /** Sets the kind variable to the specified value. */
    public void setKind(int kind) { this.kind = kind; }

    /** Sets the assertion variable to the specified value. */
    public void setAssertion(Exp assertion) { this.assertion = assertion; }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitMathAssertionDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("MathAssertionDec\n");

        if (name != null) {
            sb.append(name.asString(indent+increment,increment));
        }

        printSpace(indent+increment, sb);
        sb.append(printConstant(kind) + "\n");

        if (assertion != null) {
            sb.append(assertion.asString(indent+increment,increment));
        }

        return sb.toString();
    }


    private String printConstant(int k) {
        StringBuffer sb = new StringBuffer();
        switch(k) {
        case 1: sb.append("AXIOM"); break;
        case 2: sb.append("THEOREM"); break;
        case 3: sb.append("PROPERTY"); break;
        case 4: sb.append("LEMMA"); break;
        case 5: sb.append("COROLLARY"); break;
        default: sb.append(k);
        }
        return sb.toString();
    }
}
