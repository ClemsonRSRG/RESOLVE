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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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

public class FunctionArgList extends ResolveConceptualElement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The arguments member. */
    private List<Exp> arguments;

    // ===========================================================
    // Constructors
    // ===========================================================

    public FunctionArgList() {};

    public FunctionArgList(List<Exp> arguments) {
        this.arguments = arguments;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the arguments variable. */
    public List<Exp> getArguments() {
        return arguments;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the arguments variable to the specified value. */
    public void setArguments(List<Exp> arguments) {
        this.arguments = arguments;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Location getLocation() {
        return arguments.get(0).getLocation();
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitFunctionArgList(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("FunctionArgList\n");

        if (arguments != null) {
            sb.append(arguments.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public Object clone() {
        FunctionArgList clone = new FunctionArgList();
        Iterator<Exp> i = arguments.iterator();
        List<Exp> arg = new List<Exp>();
        while (i.hasNext()) {
            Exp tmp = i.next();
            if (tmp != null)
                arg.add((Exp) Exp.clone(tmp));
        }
        clone.setArguments(arg);
        return clone;
    }

    public void prettyPrint() {
        Iterator<Exp> it = arguments.iterator();
        if (it.hasNext()) {
            it.next().prettyPrint();
        }
        while (it.hasNext()) {
            System.out.print(", ");
            it.next().prettyPrint();
        }
    }

    public FunctionArgList copy() {
        Iterator<Exp> it = arguments.iterator();
        List<Exp> newArguments = new List<Exp>();
        while (it.hasNext()) {
            newArguments.add(Exp.copy(it.next()));
        }
        return new FunctionArgList(newArguments);
    }

}
