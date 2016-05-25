/**
 * CallStmt.java
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
import edu.clemson.cs.rsrg.absyn.programexpr.ProgramFunctionExp;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the class for all the call statement objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class CallStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The programming function expression</p> */
    private final ProgramFunctionExp myFunctionExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a call statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param exp A {@link ProgramFunctionExp} representing the function
     *            we are calling.
     */
    public CallStmt(Location l, ProgramFunctionExp exp) {
        super(l);
        myFunctionExp = exp;
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
        sb.append(myFunctionExp.asString(indentSize + innerIndentInc,
                innerIndentInc));

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

        CallStmt callStmt = (CallStmt) o;

        return myFunctionExp.equals(callStmt.myFunctionExp);

    }

    /**
     * <p>This method returns the function expression in
     * this calling statement.</p>
     *
     * @return The {@link ProgramFunctionExp} representation object.
     */
    public final ProgramFunctionExp getFunctionExp() {
        return myFunctionExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return myFunctionExp.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(myFunctionExp.toString());

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
        return new CallStmt(new Location(myLoc),
                (ProgramFunctionExp) myFunctionExp.clone());
    }

}