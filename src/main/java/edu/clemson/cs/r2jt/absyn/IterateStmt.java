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

public class IterateStmt extends Statement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The changing member. */
    private List<VariableExp> changing;

    /** The maintaining member. */
    private Exp maintaining;

    /** The decreasing member. */
    private Exp decreasing;

    /** The statements member. */
    private List<Statement> statements;

    // ===========================================================
    // Constructors
    // ===========================================================

    public IterateStmt() {};

    public IterateStmt(List<VariableExp> changing, Exp maintaining,
            Exp decreasing, List<Statement> statements) {
        this.changing = changing;
        this.maintaining = maintaining;
        this.decreasing = decreasing;
        this.statements = statements;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    public Location getLocation() {
        return maintaining.getLocation();
    }

    /** Returns the value of the changing variable. */
    public List<VariableExp> getChanging() {
        return changing;
    }

    /** Returns the value of the maintaining variable. */
    public Exp getMaintaining() {
        return maintaining;
    }

    /** Returns the value of the decreasing variable. */
    public Exp getDecreasing() {
        return decreasing;
    }

    /** Returns the value of the statements variable. */
    public List<Statement> getStatements() {
        return statements;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the changing variable to the specified value. */
    public void setChanging(List<VariableExp> changing) {
        this.changing = changing;
    }

    /** Sets the maintaining variable to the specified value. */
    public void setMaintaining(Exp maintaining) {
        this.maintaining = maintaining;
    }

    /** Sets the decreasing variable to the specified value. */
    public void setDecreasing(Exp decreasing) {
        this.decreasing = decreasing;
    }

    /** Sets the statements variable to the specified value. */
    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitIterateStmt(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("IterateStmt\n");

        if (changing != null) {
            sb.append(changing.asString(indent + increment, increment));
        }

        if (maintaining != null) {
            sb.append(maintaining.asString(indent + increment, increment));
        }

        if (decreasing != null) {
            sb.append(decreasing.asString(indent + increment, increment));
        }

        if (statements != null) {
            sb.append(statements.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        sb.append("Iterate \n");
        if (maintaining != null) {
            printSpace(indent, sb);
            sb.append("\tmaintaining " + maintaining.toString(0) + ";\n");
        }
        if (decreasing != null) {
            printSpace(indent, sb);
            sb.append("\tdecreasing " + decreasing.toString(0) + ";\n");
        }
        if (!changing.isEmpty()) {
            printSpace(indent, sb);
            sb.append("\tchanging " + argumentsToString(changing) + ";\n");
        }
        //	printSpace(indent, sb);

        Iterator<Statement> i = statements.iterator();
        while (i.hasNext()) {
            sb.append((i.next()).toString(indent + 4 * 2) + ";\n");
        }
        printSpace(indent, sb);
        sb.append("repeat");

        return sb.toString();
    }

    String argumentsToString(List<VariableExp> arguments) {
        String str = new String();
        Iterator<VariableExp> i = arguments.iterator();
        while (i.hasNext()) {
            VariableExp exp = (VariableExp) i.next();
            str = str.concat(exp.toString(0));
            if (i.hasNext())
                str = str.concat(", ");
        }
        return str;
    }
}
