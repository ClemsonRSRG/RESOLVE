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
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append(myType.name());
        sb.append(";");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MemoryStmt that = (MemoryStmt) o;

        return myType == that.myType;

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
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return myType.hashCode();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Statement copy() {
        return new MemoryStmt(new Location(myLoc), myType);
    }

}
