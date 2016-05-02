/**
 * SwapStmt.java
 * ---------------------------------
 * Copyright (c) 2016
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
import edu.clemson.cs.rsrg.absyn.programexpr.ProgramVariableExp;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the class for all the swap statements
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class SwapStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The variable expression on the left hand side</p> */
    private final ProgramVariableExp myLeftHandSide;

    /** <p>The variable expression on the right hand side</p> */
    private final ProgramVariableExp myRightHandSide;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a swap statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param left A {@link ProgramVariableExp} representing the variable
     *             expression on the left hand side of the statement.
     * @param right A {@link ProgramVariableExp} representing the variable
     *              expression on the right hand side of the statement.
     */
    public SwapStmt(Location l, ProgramVariableExp left,
            ProgramVariableExp right) {
        super(l);
        myLeftHandSide = left;
        myRightHandSide = right;
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
        sb.append("SwapStmt\n");

        if (myLeftHandSide != null) {
            sb.append(myLeftHandSide.asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append(" :=: ");
        }

        if (myRightHandSide != null) {
            sb.append(myRightHandSide.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link SwapStmt} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof SwapStmt) {
            SwapStmt eAsSwapStmt = (SwapStmt) o;
            result = myLoc.equals(eAsSwapStmt.myLoc);

            if (result) {
                result = myLeftHandSide.equals(eAsSwapStmt.myLeftHandSide);
                result &= myRightHandSide.equals(eAsSwapStmt.myRightHandSide);
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the left hand side
     * variable expression.</p>
     *
     * @return The {@link ProgramVariableExp} representation object.
     */
    public final ProgramVariableExp getLeft() {
        return (ProgramVariableExp) myLeftHandSide.clone();
    }

    /**
     * <p>This method returns a deep copy of the right hand side
     * variable expression.</p>
     *
     * @return The {@link ProgramVariableExp} representation object.
     */
    public final ProgramVariableExp getRight() {
        return (ProgramVariableExp) myRightHandSide.clone();
    }

    /**
     * <p>Returns the statement in string format.</p>
     *
     * @return Statement as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (myLeftHandSide != null) {
            sb.append(myLeftHandSide.toString());
            sb.append(" :=: ");
        }

        if (myRightHandSide != null) {
            sb.append(myRightHandSide.toString());
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
        return new SwapStmt(new Location(myLoc), getLeft(), getRight());
    }

}