/**
 * AuxCodeBlockStmt.java
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
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is the class for all the auxiliary code block statement objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class AuxCodeBlockStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The list of auxiliary statements inside this block</p> */
    private final List<Statement> myStatements;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an auxiliary code block statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param statements The list of {@link Statement}s that are in
     *                   this auxiliary block.
     */
    public AuxCodeBlockStmt(Location l, List<Statement> statements) {
        super(l);
        myStatements = statements;
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
        sb.append("Aux_Code\n");

        for (Statement s : myStatements) {
            sb.append(s.asString(indentSize + innerIndentInc, innerIndentInc));
            sb.append("\n");
        }

        sb.append("end\n");

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

        AuxCodeBlockStmt that = (AuxCodeBlockStmt) o;

        return myStatements.equals(that.myStatements);

    }

    /**
     * <p>This method returns the list of statements
     * in this auxiliary code block.</p>
     *
     * @return The list of {@link Statement}s.
     */
    public final List<Statement> getStatements() {
        return myStatements;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return myStatements.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Aux_Code\n");

        for (Statement s : myStatements) {
            sb.append("\t");
            sb.append(s.toString());
            sb.append("\n");
        }

        sb.append("end");

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Statement copy() {
        List<Statement> copyStatements = new ArrayList<>();
        for (Statement s : myStatements) {
            copyStatements.add(s.clone());
        }

        return new AuxCodeBlockStmt(new Location(myLoc), copyStatements);
    }
}