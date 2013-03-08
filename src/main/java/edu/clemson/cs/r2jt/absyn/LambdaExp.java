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
 * LambdaExp.java
 * 
 * The Resolve Software Composition Workbench Project
 * 
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;

public class LambdaExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    private MathVarDec variable;

    /** The body member. */
    private Exp body;

    // ===========================================================
    // Constructors
    // ===========================================================

    public LambdaExp() {};

    public LambdaExp(Location location, PosSymbol name, Ty ty, Exp body) {
        this(location, new MathVarDec(name, ty), body);
    }

    public LambdaExp(Location location, MathVarDec variable, Exp body) {
        this.location = location;
        this.variable = variable;
        this.body = body;
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

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return variable.getName();
    }

    /** Returns the value of the ty variable. */
    public Ty getTy() {
        return variable.getTy();
    }

    /** Returns the value of the body variable. */
    public Exp getBody() {
        return body;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        variable.setName(name);
    }

    /** Sets the ty variable to the specified value. */
    public void setTy(Ty ty) {
        variable.setTy(ty);
    }

    /** Sets the body variable to the specified value. */
    public void setBody(Exp body) {
        this.body = body;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new LambdaExp(location, variable,
                substitute(body, substitutions));
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitLambdaExp(this);
    }

    /** Accepts a TypeResolutionVisitor. */
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        return v.getLambdaExpType(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("LambdaExp\n");

        if (variable != null) {
            sb.append(variable);
        }

        if (body != null) {
            sb.append(body.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public boolean equivalent(Exp e) {
        boolean result = e instanceof LambdaExp;

        if (result) {
            LambdaExp eAsLambdaExp = (LambdaExp) e;

            result = eAsLambdaExp.variable.equals(variable);
        }

        return result;
    }

    /** Returns true if the variable is found in any sub expression   
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        if (variable.getName().toString().equals(varName)) {
            return true;
        }

        if (body != null) {
            return body.containsVar(varName, IsOldExp);
        }

        return false;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(body);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        body = e;
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof LambdaExp)) {
            return false;
        }
        if (!(variable.getName().equals(((LambdaExp) e2).getName().getName()))) {
            return false;
        }
        return true;
    }

    public Exp replace(Exp old, Exp replace) {
        if (!(old instanceof LambdaExp)) {
            LambdaExp result = (LambdaExp) Exp.copy(this);
            result.body = Exp.replace(result.body, old, replace);

            //replace is idiotically implemented, so we have to do this
            if (result.body == null) {
                result.body = Exp.copy(body);
            }

            if (variable.getName() != null) {
                if (old instanceof VarExp && replace instanceof VarExp) {
                    if (((VarExp) old).getName().toString().equals(
                            variable.getName().toString())) {
                        this.variable.setName(((VarExp) replace).getName());
                        return this;
                    }
                }
            }
            return result;
        }
        return this;
    }

    public void prettyPrint() {
        System.out.print("lambda " + variable);
        System.out.print(" (");
        body.prettyPrint();
        System.out.print(")");
    }

    public String toString(int indent) {
        StringBuffer sb = new StringBuffer();
        sb.append("lambda " + variable);
        sb.append(" (");
        sb.append(body.toString(0));
        sb.append(")");
        return sb.toString();
    }

    public Exp copy() {
        MathVarDec newVariable = variable.copy();
        Exp newBody = Exp.copy(body);
        Exp result = new LambdaExp(null, newVariable, newBody);
        result.setType(type);

        return result;
    }

    public Object clone() {
        MathVarDec newVariable = variable.copy();
        Exp newBody = (Exp) Exp.clone(body);
        Exp result = new LambdaExp(null, newVariable, newBody);
        result.setType(type);

        return result;
    }

    public Exp remember() {

        if (body instanceof OldExp)
            this.setBody(((OldExp) (body)).getExp());

        if (body != null)
            body = body.remember();

        return this;
    }

}
