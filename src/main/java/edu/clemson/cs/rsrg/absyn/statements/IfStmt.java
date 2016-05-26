/**
 * IfStmt.java
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
import edu.clemson.cs.rsrg.absyn.statements.codeblocks.IfConditionBlockItem;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the if-else statement objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class IfStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The if part of this statement</p> */
    private final IfConditionBlockItem myIfClause;

    /** <p>The else-if part of this statement</p> */
    private final List<IfConditionBlockItem> myElseIfs;

    /** <p>The else part of this statement</p> */
    private final List<Statement> myElseStatements;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an if-elseif-else statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param ifClause A {@link IfConditionBlockItem} representing if block.
     * @param elseifpairs A list of {@link IfConditionBlockItem} representing all
     *                    the else-if pairs.
     * @param elseStatements The list of {@link Statement}s that are in
     *                       the else block.
     */
    public IfStmt(Location l, IfConditionBlockItem ifClause,
            List<IfConditionBlockItem> elseifpairs,
            List<Statement> elseStatements) {
        super(l);
        myIfClause = ifClause;
        myElseIfs = elseifpairs;
        myElseStatements = elseStatements;
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

        // If
        sb.append(myIfClause.asString(0, innerIndentInc));
        sb.append("\n");

        // Else-if
        for (IfConditionBlockItem c : myElseIfs) {
            printSpace(indentSize, sb);
            sb.append("Else ");
            sb.append(c.asString(0, innerIndentInc));
            sb.append("\n");
        }

        // Else
        if (myElseStatements.size() > 0) {
            sb.append("Else\n");

            for (Statement s : myElseStatements) {
                sb.append(s.asString(indentSize + innerIndentInc,
                        innerIndentInc));
                sb.append("\n");
            }
        }
        printSpace(indentSize, sb);
        sb.append("end");

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

        IfStmt ifStmt = (IfStmt) o;

        if (!myIfClause.equals(ifStmt.myIfClause))
            return false;
        if (!myElseIfs.equals(ifStmt.myElseIfs))
            return false;
        return myElseStatements.equals(ifStmt.myElseStatements);

    }

    /**
     * <p>This method returns the statements in this else block</p>
     *
     * @return The list of {@link Statement} representation objects.
     */
    public final List<Statement> getElseclause() {
        return myElseStatements;
    }

    /**
     * <p>This method returns all the else-if blocks in
     * this {@link IfStmt}.</p>
     *
     * @return The list of {@link IfConditionBlockItem} representation objects.
     */
    public final List<IfConditionBlockItem> getElseifpairs() {
        return myElseIfs;
    }

    /**
     * <p>This method returns the if block in
     * this {@link IfStmt}.</p>
     *
     * @return The {@link IfConditionBlockItem} representation object.
     */
    public final IfConditionBlockItem getIfClause() {
        return myIfClause;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myIfClause.hashCode();
        result = 31 * result + myElseIfs.hashCode();
        result = 31 * result + myElseStatements.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(myIfClause.toString());

        for (IfConditionBlockItem item : myElseIfs) {
            sb.append("Else ");
            sb.append(item.toString());
        }

        sb.append("Else\n");
        for (Statement s : myElseStatements) {
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
     * <p>Implemented by this concrete subclass of {@link Statement} to
     * manufacture a copy of themselves.</p>
     *
     * @return A new {@link Statement} that is a deep copy of the original.
     */
    @Override
    protected final Statement copy() {
        // Copy any else-ifs
        List<IfConditionBlockItem> newElseIfs = new ArrayList<>();
        for (IfConditionBlockItem item : myElseIfs) {
            newElseIfs.add(item.clone());
        }

        // Copy any statements in the else block
        List<Statement> newElseStmts = new ArrayList<>();
        for (Statement statement : myElseStatements) {
            newElseStmts.add(statement.clone());
        }

        return new IfStmt(new Location(myLoc), myIfClause.clone(), newElseIfs,
                newElseStmts);
    }
}