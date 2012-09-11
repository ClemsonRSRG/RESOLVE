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
 * TypeFunctionExp.java
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
import edu.clemson.cs.r2jt.type.TypeMatcher;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.collections.Iterator;

public class TypeFunctionExp extends Exp {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final int DENOTED_BY = 1;
    public static final int IS_INITIAL = 2;
    public static final int LAST_SPECIMEN_NUM = 3;
    public static final int SPECIMEN_NUM = 4;

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The qualifier member. */
    private PosSymbol qualifier;

    /** The typeName member. */
    private PosSymbol typeName;

    /** The funcLocation member. */
    private Location funcLocation;

    /** The function member. */
    private int function;

    /** The params member. */
    private List<Exp> params;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TypeFunctionExp() {};

    public TypeFunctionExp(Location location, PosSymbol qualifier,
            PosSymbol typeName, Location funcLocation, int function,
            List<Exp> params) {
        this.location = location;
        this.qualifier = qualifier;
        this.typeName = typeName;
        this.funcLocation = funcLocation;
        this.function = function;
        this.params = params;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        List<Exp> newParams = new List<Exp>();
        for (Exp p : params) {
            newParams.add(substitute(p, substitutions));
        }

        return new TypeFunctionExp(location, qualifier, typeName, funcLocation,
                function, newParams);
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

    /** Returns the value of the qualifier variable. */
    public PosSymbol getQualifier() {
        return qualifier;
    }

    /** Returns the value of the typeName variable. */
    public PosSymbol getTypeName() {
        return typeName;
    }

    /** Returns the value of the funcLocation variable. */
    public Location getFuncLocation() {
        return funcLocation;
    }

    /** Returns the value of the function variable. */
    public int getFunction() {
        return function;
    }

    /** Returns the value of the params variable. */
    public List<Exp> getParams() {
        return params;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the qualifier variable to the specified value. */
    public void setQualifier(PosSymbol qualifier) {
        this.qualifier = qualifier;
    }

    /** Sets the typeName variable to the specified value. */
    public void setTypeName(PosSymbol typeName) {
        this.typeName = typeName;
    }

    /** Sets the funcLocation variable to the specified value. */
    public void setFuncLocation(Location funcLocation) {
        this.funcLocation = funcLocation;
    }

    /** Sets the function variable to the specified value. */
    public void setFunction(int function) {
        this.function = function;
    }

    /** Sets the params variable to the specified value. */
    public void setParams(List<Exp> params) {
        this.params = params;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitTypeFunctionExp(this);
    }

    /** Accepts a TypeResolutionVisitor. */
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        return v.getTypeFunctionExpType(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("TypeFunctionExp\n");

        if (qualifier != null) {
            sb.append(qualifier.asString(indent + increment, increment));
        }

        if (typeName != null) {
            sb.append(typeName.asString(indent + increment, increment));
        }

        printSpace(indent + increment, sb);
        sb.append(printConstant(function) + "\n");

        if (params != null) {
            sb.append(params.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Iterator<Exp> i = params.iterator();
        while (i.hasNext()) {
            Exp temp = i.next();
            if (temp != null) {
                if (temp.containsVar(varName, IsOldExp)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String printConstant(int k) {
        StringBuffer sb = new StringBuffer();
        switch (k) {
        case 1:
            sb.append("DENOTED_BY");
            break;
        case 2:
            sb.append("IS_INITIAL");
            break;
        case 3:
            sb.append("LAST_SPECIMEN_NUM");
            break;
        case 4:
            sb.append("SPECIMEN_NUM");
            break;
        default:
            sb.append(k);
        }
        return sb.toString();
    }

    public List<Exp> getSubExpressions() {
        return params;
    }

    public void setSubExpression(int index, Exp e) {
        params.set(index, e);
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof TypeFunctionExp)) {
            return false;
        }
        if (qualifier != null
                && (((TypeFunctionExp) e2).getQualifier() != null)) {
            if (!(qualifier.equals(((TypeFunctionExp) e2).getQualifier()
                    .getName()))) {
                return false;
            }
        }
        if (!(typeName.equals(((TypeFunctionExp) e2).getTypeName().getName()))) {
            return false;
        }
        if (function != ((TypeFunctionExp) e2).getFunction()) {
            return false;
        }
        return true;
    }

}
