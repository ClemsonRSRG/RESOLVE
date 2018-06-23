/*
 * FuncAssignStmt.java
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
package edu.clemson.cs.rsrg.absyn.statements;

import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramVariableExp;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the class for all the function assignment statement objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class FuncAssignStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The variable expression to be assigned</p> */
    private final ProgramVariableExp myVariableExp;

    /** <p>The programming expression to be assigned</p> */
    private final ProgramExp myAssignExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a function assignment statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param var A {@link ProgramVariableExp} representing the variable
     *            expression we want to assign to.
     * @param exp A {@link ProgramExp} representing the expression we are
     *            assigning.
     */
    public FuncAssignStmt(Location l, ProgramVariableExp var, ProgramExp exp) {
        super(l);
        myVariableExp = var;
        myAssignExp = exp;
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

        sb.append(myVariableExp.asString(indentSize, innerIndentInc));
        sb.append(" := ");
        sb.append(myAssignExp.asString(0, innerIndentInc));
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

        FuncAssignStmt that = (FuncAssignStmt) o;

        if (!myVariableExp.equals(that.myVariableExp))
            return false;
        return myAssignExp.equals(that.myAssignExp);

    }

    /**
     * <p>This method returns the assigned expression in
     * this function assignment statement.</p>
     *
     * @return The {@link ProgramExp} representation object.
     */
    public final ProgramExp getAssignExp() {
        return myAssignExp;
    }

    /**
     * <p>This method returns the variable expression in
     * this function assignment statement.</p>
     *
     * @return The {@link ProgramVariableExp} representation object.
     */
    public final ProgramVariableExp getVariableExp() {
        return myVariableExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myVariableExp.hashCode();
        result = 31 * result + myAssignExp.hashCode();
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
        return new FuncAssignStmt(cloneLocation(),
                (ProgramVariableExp) myVariableExp.clone(), myAssignExp.clone());
    }

}