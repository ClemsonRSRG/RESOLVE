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
 *     Nighat Yasmin
 */
/*
 * WhileStmt.java
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
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.PosSymbol;

public class WhileStmt extends Statement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The test member. */
    private ProgramExp test;

    /** The changing member. */
    private List<VariableExp> changing;

    /** The maintaining member. */
    private Exp maintaining;

    /** The decreasing member. */
    private Exp decreasing;

    /** The elapsed_time member. */
    private Exp elapsed_time;

    /** The statements member. */
    private List<Statement> statements;

    // ===========================================================
    // Constructors
    // ===========================================================

    public WhileStmt() {};

    public WhileStmt(
            Location location,
            ProgramExp test,
            List<VariableExp> changing,
            Exp maintaining,
            Exp decreasing,
            Exp elapsed_time,            
            List<Statement> statements)
    {
        this.location = location;
        this.test = test;
        this.changing = changing;
        this.maintaining = maintaining;
        this.decreasing = decreasing;
        this.elapsed_time = elapsed_time;
        this.statements = statements;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the location variable. */
    public Location getLocation() { return location; }

    /** Returns the value of the test variable. */
    public ProgramExp getTest() { return test; }

    /** Returns the value of the changing variable. */
    public List<VariableExp> getChanging() { return changing; }

    /** Returns the value of the maintaining variable. */
    public Exp getMaintaining() { return maintaining; }

    /** Returns the value of the decreasing variable. */
    public Exp getDecreasing() { return decreasing; }

    /** Returns the value of the elapsed_time variable. */
    public Exp getElapsed_Time() { return elapsed_time; }

    /** Returns the value of the statements variable. */
    public List<Statement> getStatements() { return statements; }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) { this.location = location; }

    /** Sets the test variable to the specified value. */
    public void setTest(ProgramExp test) { this.test = test; }

    /** Sets the changing variable to the specified value. */
    public void setChanging(List<VariableExp> changing) { this.changing = changing; }

    /** Sets the maintaining variable to the specified value. */
    public void setMaintaining(Exp maintaining) { this.maintaining = maintaining; }

    /** Sets the decreasing variable to the specified value. */
    public void setDecreasing(Exp decreasing) { this.decreasing = decreasing; }

    /** Sets the elapsed_time variable to the specified value. */
    public void setElapsed_Time(Exp elapsed_time) { this.elapsed_time = elapsed_time; }

    /** Sets the statements variable to the specified value. */
    public void setStatements(List<Statement> statements) { this.statements = statements; }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitWhileStmt(this);
    }
    
    /** Adds a VariableExp to the changing list. */
    public void addToChanging(VariableExp exp){
    	changing.add(exp);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("WhileStmt\n");

        if (test != null) {
            sb.append(test.asString(indent+increment,increment));
        }

        if (changing != null) {
            sb.append(changing.asString(indent+increment,increment));
        }

        if (maintaining != null) {
            sb.append(maintaining.asString(indent+increment,increment));
        }

        if (decreasing != null) {
            sb.append(decreasing.asString(indent+increment,increment));
        }

        if (elapsed_time != null) {
            sb.append(elapsed_time.asString(indent+increment,increment));
        }

        if (statements != null) {
            sb.append(statements.asString(indent+increment,increment));
        }

        return sb.toString();
    }
    
    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        sb.append("While (" + test.toString(0) + ")\n");
    	if(maintaining != null)
    		sb.append("\t\tmaintaining " + maintaining.toString(0) + ";\n");
    	if(decreasing != null)
    		sb.append("\t\tdecreasing " + decreasing.toString(0) + ";\n");
    	if(changing != null && !changing.isEmpty())
    		sb.append("\t\tchanging " + argumentsToString(changing) + ";\n");    	
    	if(elapsed_time != null)
    		sb.append("\t\telapsed_time " + elapsed_time.toString(0) + ";\n");    	
    	printSpace(indent, sb);
    	sb.append("do\n");
    	Iterator<Statement> i = statements.iterator();
    	while(i.hasNext()){
    		sb.append((i.next()).toString(indent + 4 * 2) + ";\n");
    	}
    	printSpace(indent, sb);
    	sb.append("end");


        return sb.toString();
    }
    
    String argumentsToString(List<VariableExp> arguments) {
    	String str = new String();
    	Iterator<VariableExp> i = arguments.iterator();
		while(i.hasNext()) {
			VariableExp exp = (VariableExp)i.next();
			str = str.concat(exp.toString(0));
			if(i.hasNext())
				str = str.concat(", ");
		}
        return str;
    }
}
