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
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the Clemson University nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
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
 * Clemson University. Contributors to the initial version are:
 * 
 * Steven Atkinson
 * Greg Kulczycki
 * Kunal Chopra
 * John Hunt
 * Heather Keown
 * Ben Markle
 * Kim Roche
 * Murali Sitaraman
 */
/*
 * ProgramOpExp.java
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
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;

public class ProgramOpExp extends ProgramExp {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final int AND = 1;
    public static final int OR = 2;
    public static final int EQUAL = 3;
    public static final int NOT_EQUAL = 4;
    public static final int LT = 5;
    public static final int LT_EQL = 6;
    public static final int GT = 7;
    public static final int GT_EQL = 8;
    public static final int PLUS = 9;
    public static final int MINUS = 10;
    public static final int MULTIPLY = 11;
    public static final int DIVIDE = 12;
    public static final int REM = 13;
    public static final int MOD = 14;
    public static final int DIV = 15;
    public static final int EXP = 16;
    public static final int NOT = 17;
    public static final int UNARY_MINUS = 18;

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The operator member. */
    private int operator;

    /** The first member. */
    private ProgramExp first;

    /** The second member. */
    private ProgramExp second;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProgramOpExp() {};

    public ProgramOpExp(Location location, int operator, ProgramExp first,
            ProgramExp second) {
        this.location = location;
        this.operator = operator;
        this.first = first;
        this.second = second;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new ProgramOpExp(location, operator, (ProgramExp) substitute(
                first, substitutions), (ProgramExp) substitute(second,
                substitutions));
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the location variable. */
    public Location getLocation() {
        return location;
    }

    /** Returns the value of the operator variable. */
    public int getOperator() {
        return operator;
    }

    /** Returns the value of the first variable. */
    public ProgramExp getFirst() {
        return first;
    }

    /** Returns the value of the second variable. */
    public ProgramExp getSecond() {
        return second;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the operator variable to the specified value. */
    public void setOperator(int operator) {
        this.operator = operator;
    }

    /** Sets the first variable to the specified value. */
    public void setFirst(ProgramExp first) {
        this.first = first;
    }

    /** Sets the second variable to the specified value. */
    public void setSecond(ProgramExp second) {
        this.second = second;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitProgramOpExp(this);
    }

    /** Accepts a TypeResolutionVisitor. */
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        return v.getProgramOpExpType(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ProgramOpExp\n");

        printSpace(indent + increment, sb);
        sb.append(printConstant(operator) + "\n");

        if (first != null) {
            sb.append(first.asString(indent + increment, increment));
        }

        if (second != null) {
            sb.append(second.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (first != null) {
            sb.append(first.toString(0));
        }

        sb.append(" " + printPOEConstant(operator) + " ");

        if (second != null) {
            sb.append(second.toString(0));
        }

        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Boolean found = false;
        if (first != null) {
            found = first.containsVar(varName, IsOldExp);
        }
        if (!found && second != null) {
            found = second.containsVar(varName, IsOldExp);
        }
        return found;
    }

    private String printConstant(int k) {
        StringBuffer sb = new StringBuffer();
        switch (k) {
        case 1:
            sb.append("AND");
            break;
        case 2:
            sb.append("OR");
            break;
        case 3:
            sb.append("EQUAL");
            break;
        case 4:
            sb.append("NOT_EQUAL");
            break;
        case 5:
            sb.append("LT");
            break;
        case 6:
            sb.append("LT_EQL");
            break;
        case 7:
            sb.append("GT");
            break;
        case 8:
            sb.append("GT_EQL");
            break;
        case 9:
            sb.append("PLUS");
            break;
        case 10:
            sb.append("MINUS");
            break;
        case 11:
            sb.append("MULTIPLY");
            break;
        case 12:
            sb.append("DIVIDE");
            break;
        case 13:
            sb.append("REM");
            break;
        case 14:
            sb.append("MOD");
            break;
        case 15:
            sb.append("DIV");
            break;
        case 16:
            sb.append("EXP");
            break;
        case 17:
            sb.append("NOT");
            break;
        case 18:
            sb.append("UNARY_MINUS");
            break;
        default:
            sb.append(k);
        }
        return sb.toString();
    }

    private String printPOEConstant(int k) {
        StringBuffer sb = new StringBuffer();
        switch (k) {
        case 1:
            sb.append("AND");
            break;
        case 2:
            sb.append("OR");
            break;
        case 3:
            sb.append("=");
            break;
        case 4:
            sb.append("/=");
            break;
        case 5:
            sb.append("<");
            break;
        case 6:
            sb.append("<=");
            break;
        case 7:
            sb.append(">");
            break;
        case 8:
            sb.append(">=");
            break;
        case 9:
            sb.append("+");
            break;
        case 10:
            sb.append("-");
            break;
        case 11:
            sb.append("*");
            break;
        case 12:
            sb.append("/");
            break;
        case 13:
            sb.append("REM");
            break;
        case 14:
            sb.append("%");
            break;
        case 15:
            sb.append("/");
            break;
        case 16:
            sb.append("^");
            break;
        case 17:
            sb.append("~");
            break;
        case 18:
            sb.append("-");
            break;
        default:
            sb.append(k);
        }
        return sb.toString();
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add((Exp) first);
        list.add((Exp) second);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        switch (index) {
        case 0:
            first = (ProgramExp) e;
            break;
        case 1:
            second = (ProgramExp) e;
            break;
        }
    }
}
