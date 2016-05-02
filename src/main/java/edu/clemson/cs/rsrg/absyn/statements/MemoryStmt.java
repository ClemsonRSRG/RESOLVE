/**
 * MemoryStmt.java
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
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the class for all the remember/forget statements
 * that the compiler builds from the ANTLR4 AST tree or
 * generated during the VC Generation step.</p>
 *
 * @version 2.0
 */
public class MemoryStmt extends Statement {

    // ===========================================================
    // StatementType
    // ===========================================================

    public enum StatementType {
        FORGET {

            @Override
            public String toString() {
                return "Forget";
            }

        },
        REMEMBER {

            @Override
            public String toString() {
                return "Remember";
            }

        }
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>This indicates if this is a remember or a forget</p> */
    private final StatementType myType;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a confirm statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param type This enum indicates whether this is a remember
     *                  or a forget statement.
     */
    public MemoryStmt(Location l, StatementType type) {
        super(l);
        myType = type;
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
        sb.append("MemoryStmt\n");

        printSpace(indentSize + innerIndentSize, sb);
        sb.append(myType.toString());
        sb.append("\n");

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link MemoryStmt} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof MemoryStmt) {
            MemoryStmt eAsMemoryStmt = (MemoryStmt) o;
            result = myLoc.equals(eAsMemoryStmt.myLoc);
            result &= (myType == eAsMemoryStmt.myType);
        }

        return result;
    }

    /**
     * <p>This method returns the statement type.</p>
     *
     * @return A {@link StatementType} representation object.
     */
    public final StatementType getStatementType() {
        return myType;
    }

    /**
     * <p>Returns the statement in string format.</p>
     *
     * @return Statement as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(myType.toString());

        return sb.toString();
    }

}
