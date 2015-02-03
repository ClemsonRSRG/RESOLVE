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

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.typeandpopulate.MTFunction;
import java.util.LinkedList;

public class LambdaExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    private List<MathVarDec> parameters;

    /** The body member. */
    private Exp body;

    // ===========================================================
    // Constructors
    // ===========================================================

    public LambdaExp(Location location, List<MathVarDec> params, Exp body) {

        if (params == null) {
            throw new IllegalArgumentException("null LambdaExp params");
        }

        this.location = location;
        this.parameters = params;
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

    public List<MathVarDec> getParameters() {
        return parameters;
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

    public void setParameters(List<MathVarDec> params) {

        if (params == null) {
            throw new IllegalArgumentException("null LambdaExp params");
        }

        parameters = params;
    }

    /** Sets the body variable to the specified value. */
    public void setBody(Exp body) {
        this.body = body;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new LambdaExp(location, new List<MathVarDec>(parameters),
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

        if (parameters != null) {
            sb.append(parameters);
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

            result = (parameters.size() == eAsLambdaExp.parameters.size());

            Iterator<MathVarDec> parameterIterator = parameters.iterator();
            Iterator<MathVarDec> eParameterIterator =
                    eAsLambdaExp.parameters.iterator();
            while (parameterIterator.hasNext() && result) {
                result =
                        parameterIterator.next().equals(
                                eParameterIterator.next());
            }
        }

        return result;
    }

    /** Returns true if the variable is found in any sub expression   
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        boolean result = false;

        Iterator<MathVarDec> parameterIter = parameters.iterator();
        while (!result && parameterIter.hasNext()) {
            result = parameterIter.next().getName().getName().equals(varName);
        }

        if (!result && body != null) {
            result = body.containsVar(varName, IsOldExp);
        }

        return result;
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

        LambdaExp e2AsLambdaExp = (LambdaExp) e2;
        boolean result = (parameters.size() == e2AsLambdaExp.parameters.size());

        Iterator<MathVarDec> parameterIter = parameters.iterator();
        Iterator<MathVarDec> e2ParameterIter =
                e2AsLambdaExp.parameters.iterator();

        while (result && parameterIter.hasNext()) {
            result =
                    parameterIter.next().getName().equals(
                            e2ParameterIter.next().getName());
        }

        return result;
    }

    public Exp replace(Exp old, Exp replace) {
        if (!(old instanceof LambdaExp)) {
            LambdaExp result = (LambdaExp) Exp.copy(this);
            result.body = Exp.replace(result.body, old, replace);

            //replace is idiotically implemented, so we have to do this
            if (result.body == null) {
                result.body = Exp.copy(body);
            }

            List<MathVarDec> finalParameters = new List<MathVarDec>();
            if (old instanceof VarExp && replace instanceof VarExp) {
                Iterator<MathVarDec> parameterIter = parameters.iterator();
                MathVarDec param;
                while (parameterIter.hasNext()) {
                    param = parameterIter.next();

                    if (((VarExp) old).getName().toString().equals(
                            param.getName().toString())) {

                        param.setName(((VarExp) replace).getName());
                    }
                }
            }

            for (MathVarDec p : parameters) {
                finalParameters.add(p.copy());
            }

            return result;
        }
        return this;
    }

    public void prettyPrint() {
        System.out.print("lambda " + parameters);
        System.out.print(" (");
        body.prettyPrint();
        System.out.print(")");
    }

    public String toString(int indent) {
        StringBuffer sb = new StringBuffer();
        sb.append("lambda " + parameters);
        sb.append(" (");
        sb.append(body.toString(0));
        sb.append(")");
        return sb.toString();
    }

    public Exp copy() {
        List<MathVarDec> newParams = new List<MathVarDec>();
        for (MathVarDec p : parameters) {
            newParams.add(p.copy());
        }

        Exp newBody = Exp.copy(body);
        Exp result = new LambdaExp(null, newParams, newBody);
        result.setType(type);

        return result;
    }

    public Object clone() {
        return copy();
    }

    public Exp remember() {

        if (body instanceof OldExp)
            this.setBody(((OldExp) (body)).getExp());

        if (body != null)
            body = body.remember();

        return this;
    }

    @Override
    public MTFunction getMathType() {
        return (MTFunction) super.getMathType();
    }
}
