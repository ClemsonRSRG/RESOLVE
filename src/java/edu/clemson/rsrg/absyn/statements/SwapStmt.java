/*
 * SwapStmt.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.absyn.statements;

import edu.clemson.rsrg.absyn.expressions.programexpr.ProgramVariableExp;
import edu.clemson.rsrg.parsing.data.Location;

/**
 * <p>
 * This is the class for all the swap statement objects that the compiler builds using the ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class SwapStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The variable expression on the left hand side
     * </p>
     */
    private final ProgramVariableExp myLeftHandSide;

    /**
     * <p>
     * The variable expression on the right hand side
     * </p>
     */
    private final ProgramVariableExp myRightHandSide;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a swap statement.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param left
     *            A {@link ProgramVariableExp} representing the variable expression on the left hand side of the
     *            statement.
     * @param right
     *            A {@link ProgramVariableExp} representing the variable expression on the right hand side of the
     *            statement.
     */
    public SwapStmt(Location l, ProgramVariableExp left, ProgramVariableExp right) {
        super(l);
        myLeftHandSide = left;
        myRightHandSide = right;
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

        sb.append(myLeftHandSide.asString(indentSize, innerIndentInc));
        sb.append(" :=: ");
        sb.append(myRightHandSide.asString(0, innerIndentInc));
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

        SwapStmt swapStmt = (SwapStmt) o;

        if (!myLeftHandSide.equals(swapStmt.myLeftHandSide))
            return false;
        return myRightHandSide.equals(swapStmt.myRightHandSide);

    }

    /**
     * <p>
     * This method returns the left hand side variable expression.
     * </p>
     *
     * @return The {@link ProgramVariableExp} representation object.
     */
    public final ProgramVariableExp getLeft() {
        return myLeftHandSide;
    }

    /**
     * <p>
     * This method returns the right hand side variable expression.
     * </p>
     *
     * @return The {@link ProgramVariableExp} representation object.
     */
    public final ProgramVariableExp getRight() {
        return myRightHandSide;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myLeftHandSide.hashCode();
        result = 31 * result + myRightHandSide.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Statement copy() {
        return new SwapStmt(cloneLocation(), (ProgramVariableExp) myLeftHandSide.clone(),
                (ProgramVariableExp) myRightHandSide.clone());
    }

}
