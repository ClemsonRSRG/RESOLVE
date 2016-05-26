/**
 * IfConditionBlockItem.java
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
package edu.clemson.cs.rsrg.absyn.statements.codeblocks;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.Statement;
import edu.clemson.cs.rsrg.absyn.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is the class for all the if/else-if condition block objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class IfConditionBlockItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>The testing expression.</p>
     */
    private final ProgramExp myTestingExp;

    /**
     * <p>The list of statements that gets executed
     * if the testing expression is met</p>
     */
    private final List<Statement> myStatements;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a if/else-if code block with a condition test.
     * If the testing expression is met, then the code block is
     * executed.</p>
     *
     * @param l A {@link Location} representation object.
     * @param test A {@link ProgramExp} testing expression.
     * @param statements The list of {@link Statement}s that are in
     *                   this block.
     */
    public IfConditionBlockItem(Location l, ProgramExp test,
            List<Statement> statements) {
        super(l);
        myTestingExp = test;
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

        sb.append("If ");
        sb.append(myTestingExp.asString(0, innerIndentInc));
        sb.append(" then\n");

        // Print the statements
        for (Statement s : myStatements) {
            sb.append(s.asString(indentSize + innerIndentInc, innerIndentInc));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IfConditionBlockItem clone() {
        return new IfConditionBlockItem(myLoc, myTestingExp.clone(),
                copyStatements());
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

        IfConditionBlockItem that = (IfConditionBlockItem) o;

        if (!myTestingExp.equals(that.myTestingExp))
            return false;
        return myStatements.equals(that.myStatements);

    }

    /**
     * <p>This method returns the list of statements
     * in this code block.</p>
     *
     * @return The list of {@link Statement}s.
     */
    public final List<Statement> getStatements() {
        return myStatements;
    }

    /**
     * <p>This method returns the testing expression for
     * this code block.</p>
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
        result = 31 * result + myStatements.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("If ");
        sb.append(myTestingExp.toString());
        sb.append(" then\n");

        // Print the statements
        for (Statement s : myStatements) {
            sb.append("\t");
            sb.append(s.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This method returns a deep copy of the list of statements
     * in this code block.</p>
     *
     * @return The list of {@link Statement}s.
     */
    private final List<Statement> copyStatements() {
        List<Statement> copyStatements = new ArrayList<>();
        for (Statement s : myStatements) {
            copyStatements.add(s.clone());
        }

        return copyStatements;
    }
}