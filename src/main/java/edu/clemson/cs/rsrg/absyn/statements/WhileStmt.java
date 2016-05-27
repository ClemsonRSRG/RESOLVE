/**
 * WhileStmt.java
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

import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.items.mathitems.LoopVerificationItem;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is the class for all the while loop statement objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class WhileStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**<p>The testing expression.</p> */
    private final ProgramExp myTestingExp;

    /** <p>The verification block for this while loop</p> */
    private final LoopVerificationItem myVerificationBlock;

    /** <p>The list of statements for this while loop</p> */
    private final List<Statement> myWhileStatements;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a while loop statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param test A {@link ProgramExp} testing expression.
     * @param verificationBlock A {@link LoopVerificationItem} containing
     *                          the assertions needed for verification purposes.
     * @param statements The list of {@link Statement}s that are in
     *                   the while loop.
     */
    public WhileStmt(Location l, ProgramExp test,
            LoopVerificationItem verificationBlock, List<Statement> statements) {
        super(l);
        myTestingExp = test;
        myVerificationBlock = verificationBlock;
        myWhileStatements = statements;
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

        // loop condition
        sb.append("While ( ");
        sb.append(myTestingExp.asString(0, innerIndentInc));
        sb.append(" )\n");

        // verification assertions
        sb.append(myVerificationBlock.asString(indentSize + innerIndentInc,
                innerIndentInc));
        sb.append("\n");

        printSpace(indentSize, sb);
        sb.append("do\n");

        // list of statements in the loop
        for (Statement statement : myWhileStatements) {
            sb.append(statement.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("\n");
        }

        printSpace(indentSize, sb);
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

        WhileStmt whileStmt = (WhileStmt) o;

        if (!myTestingExp.equals(whileStmt.myTestingExp))
            return false;
        if (!myVerificationBlock.equals(whileStmt.myVerificationBlock))
            return false;
        return myWhileStatements.equals(whileStmt.myWhileStatements);

    }

    /**
     * <p>This method returns the verification block in
     * this {@code WhileStmt}.</p>
     *
     * @return The {@link LoopVerificationItem} representation object.
     */
    public final LoopVerificationItem getLoopVerificationBlock() {
        return myVerificationBlock;
    }

    /**
     * <p>This method returns the list of statements in
     * this {@code WhileStmt}.</p>
     *
     * @return The list of {@link Statement}s.
     */
    public final List<Statement> getStatements() {
        return myWhileStatements;
    }

    /**
     * <p>This method returns the testing expression for
     * this {@code WhileStmt}.</p>
     *
     * @return The testing {@link ProgramExp} object.
     */
    public final ProgramExp getTest() {
        return myTestingExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myTestingExp.hashCode();
        result = 31 * result + myVerificationBlock.hashCode();
        result = 31 * result + myWhileStatements.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();

        // loop condition
        sb.append("While ( ");
        sb.append(myTestingExp.toString());
        sb.append(" )\n");

        // verification assertions
        sb.append(myVerificationBlock.toString());
        sb.append("\n");

        sb.append("do\n");

        // list of statements in the loop
        for (Statement statement : myWhileStatements) {
            sb.append("\t");
            sb.append(statement.toString());
            sb.append("\n");
        }

        sb.append("end\n");

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
        List<Statement> copyWhileStatements = new ArrayList<>();
        for (Statement s : myWhileStatements) {
            copyWhileStatements.add(s.clone());
        }

        return new WhileStmt(new Location(myLoc), myTestingExp.clone(), myVerificationBlock.clone(), copyWhileStatements);
    }
}