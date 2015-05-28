/**
 * WhileStmt.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;

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

    public WhileStmt(Location location, ProgramExp test,
            List<VariableExp> changing, Exp maintaining, Exp decreasing,
            Exp elapsed_time, List<Statement> statements) {
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
    public Location getLocation() {
        return location;
    }

    /** Returns the value of the test variable. */
    public ProgramExp getTest() {
        return test;
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

    /** Returns the value of the elapsed_time variable. */
    public Exp getElapsed_Time() {
        return elapsed_time;
    }

    /** Returns the value of the statements variable. */
    public List<Statement> getStatements() {
        return statements;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the test variable to the specified value. */
    public void setTest(ProgramExp test) {
        this.test = test;
    }

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

    /** Sets the elapsed_time variable to the specified value. */
    public void setElapsed_Time(Exp elapsed_time) {
        this.elapsed_time = elapsed_time;
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
        v.visitWhileStmt(this);
    }

    /** Adds a VariableExp to the changing list. */
    public void addToChanging(VariableExp exp) {
        changing.add(exp);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("WhileStmt\n");

        if (test != null) {
            sb.append(test.asString(indent + increment, increment));
        }

        if (changing != null) {
            sb.append(changing.asString(indent + increment, increment));
        }

        if (maintaining != null) {
            sb.append(maintaining.asString(indent + increment, increment));
        }

        if (decreasing != null) {
            sb.append(decreasing.asString(indent + increment, increment));
        }

        if (elapsed_time != null) {
            sb.append(elapsed_time.asString(indent + increment, increment));
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

        sb.append("While (" + test.toString(0) + ")\n");
        if (maintaining != null)
            sb.append("\t\tmaintaining " + maintaining.toString(0) + ";\n");
        if (decreasing != null)
            sb.append("\t\tdecreasing " + decreasing.toString(0) + ";\n");
        if (changing != null && !changing.isEmpty())
            sb.append("\t\tchanging " + argumentsToString(changing) + ";\n");
        if (elapsed_time != null)
            sb.append("\t\telapsed_time " + elapsed_time.toString(0) + ";\n");
        printSpace(indent, sb);
        sb.append("do\n");
        Iterator<Statement> i = statements.iterator();
        while (i.hasNext()) {
            sb.append((i.next()).toString(indent + 4 * 2) + ";\n");
        }
        printSpace(indent, sb);
        sb.append("end");

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
