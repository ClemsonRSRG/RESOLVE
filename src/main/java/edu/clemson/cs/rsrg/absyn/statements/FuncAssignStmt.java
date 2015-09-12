/**
 * FuncAssignStmt.java
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
package edu.clemson.cs.rsrg.absyn.statements;

import edu.clemson.cs.rsrg.absyn.Statement;
import edu.clemson.cs.rsrg.absyn.programexpr.ProgramFunctionExp;
import edu.clemson.cs.rsrg.absyn.programexpr.ProgramVariableExp;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the class for all the function assignment statements
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class FuncAssignStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The variable expression to be assigned</p> */
    private final ProgramVariableExp myVariableExp;

    /** <p>The programming function expression</p> */
    private final ProgramFunctionExp myFunctionExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a programming function call expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param var A {@link ProgramVariableExp} representing the variable
     *            expression we want to assign to.
     * @param exp A {@link ProgramFunctionExp} representing the function
     *            we are calling.
     */
    public FuncAssignStmt(Location l, ProgramVariableExp var,
            ProgramFunctionExp exp) {
        super(l);
        myVariableExp = var;
        myFunctionExp = exp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method creates a special indented
     * text version of the class as a string.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentSize The additional indentation increment
     *                        for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public String asString(int indentSize, int innerIndentSize) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("FuncAssignStmt\n");

        if (myVariableExp != null) {
            sb.append(myVariableExp.asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append(" := ");
        }

        if (myFunctionExp != null) {
            sb.append(myFunctionExp.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link FuncAssignStmt} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof FuncAssignStmt) {
            FuncAssignStmt eAsFuncAssignStmt = (FuncAssignStmt) o;
            result = myLoc.equals(eAsFuncAssignStmt.myLoc);

            if (result) {
                result = myVariableExp.equals(eAsFuncAssignStmt.myVariableExp);
                result &= myFunctionExp.equals(eAsFuncAssignStmt.myFunctionExp);
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the function expression in
     * this function assignment statement.</p>
     *
     * @return The {@link ProgramFunctionExp} representation object.
     */
    public ProgramFunctionExp getFunctionExp() {
        return (ProgramFunctionExp) myFunctionExp.clone();
    }

    /**
     * <p>This method returns a deep copy of the variable expression in
     * this function assignment statement.</p>
     *
     * @return The {@link ProgramVariableExp} representation object.
     */
    public ProgramVariableExp getVariableExp() {
        return (ProgramVariableExp) myVariableExp.clone();
    }

    /**
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (myVariableExp != null) {
            sb.append(myVariableExp.toString());
            sb.append(" := ");
        }

        if (myFunctionExp != null) {
            sb.append(myFunctionExp.toString());
        }

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by this concrete subclass of {@link Statement} to
     * manufacture a copy of themselves.</p>
     *
     * @return A new {@link Statement} that is a deep copy of the original.
     */
    @Override
    protected Statement copy() {
        return new FuncAssignStmt(new Location(myLoc),
                (ProgramVariableExp) myVariableExp.clone(),
                (ProgramFunctionExp) myFunctionExp.clone());
    }

}