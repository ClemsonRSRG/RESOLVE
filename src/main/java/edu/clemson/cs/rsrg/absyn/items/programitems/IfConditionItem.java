/*
 * IfConditionItem.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.items.programitems;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is the class for all the if/else-if condition block objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class IfConditionItem extends ResolveConceptualElement {

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
    public IfConditionItem(Location l, ProgramExp test,
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

        sb.append("If ( ");
        sb.append(myTestingExp.asString(0, innerIndentInc));
        sb.append(" ) then\n");

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
    public final IfConditionItem clone() {
        return new IfConditionItem(cloneLocation(), myTestingExp.clone(),
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

        IfConditionItem that = (IfConditionItem) o;

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

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This method returns a deep copy of the list of statements
     * in this code block.</p>
     *
     * @return The list of {@link Statement}s.
     */
    private List<Statement> copyStatements() {
        List<Statement> copyStatements = new ArrayList<>();
        for (Statement s : myStatements) {
            copyStatements.add(s.clone());
        }

        return copyStatements;
    }
}