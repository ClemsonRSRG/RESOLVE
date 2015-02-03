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

public class IterateExitStmt extends Statement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The test member. */
    private ProgramExp test;

    /** The statements member. */
    private List<Statement> statements;

    // ===========================================================
    // Constructors
    // ===========================================================

    public IterateExitStmt() {};

    public IterateExitStmt(ProgramExp test, List<Statement> statements) {
        this.test = test;
        this.statements = statements;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    public Location getLocation() {
        return test.getLocation();
    }

    /** Returns the value of the test variable. */
    public ProgramExp getTest() {
        return test;
    }

    /** Returns the value of the statements variable. */
    public List<Statement> getStatements() {
        return statements;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the test variable to the specified value. */
    public void setTest(ProgramExp test) {
        this.test = test;
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
        v.visitIterateExitStmt(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("IterateExitStmt\n");

        if (test != null) {
            sb.append(test.asString(indent + increment, increment));
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

        sb.append("when (" + test.toString(0) + ")");

        //	printSpace(indent, sb);
        sb.append("do\n");
        Iterator<Statement> i = statements.iterator();
        while (i.hasNext()) {
            sb.append((i.next()).toString(indent + 4 * 2) + ";\n");
        }
        printSpace(indent, sb);
        sb.append("exit");

        return sb.toString();
    }

    String argumentsToString(List<ProgramExp> arguments) {
        String str = new String();
        Iterator<ProgramExp> i = arguments.iterator();
        while (i.hasNext()) {
            ProgramExp exp = (ProgramExp) i.next();
            str = str.concat(exp.toString(0));
            if (i.hasNext())
                str = str.concat(", ");
        }
        return str;
    }

}
