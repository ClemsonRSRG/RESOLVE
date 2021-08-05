/*
 * AssumeStmt.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.absyn.statements;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.vcgeneration.VCGenerator;

/**
 * <p>
 * This is the class that builds the assume statements created by the {@link VCGenerator}. Since the user cannot supply
 * their own assume clauses, any instances of this class will solely be created by the {@link VCGenerator}.
 * </p>
 *
 * @version 2.0
 */
public class AssumeStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The assume assertion expression
     * </p>
     */
    private final Exp myAssertion;

    /**
     * <p>
     * This flag indicates if this is an stipulate assume clause or not
     * </p>
     */
    private final boolean myIsStipulate;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs an assume statement.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param assertion
     *            A {@link Exp} representing the assume statement's assertion expression.
     * @param isStipulate
     *            A flag to indicate whether or not this is a stipulate assume statement.
     */
    public AssumeStmt(Location l, Exp assertion, boolean isStipulate) {
        super(l);
        myAssertion = assertion;
        myIsStipulate = isStipulate;
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

        if (myIsStipulate) {
            sb.append("Stipulate ");
        } else {
            sb.append("Assume ");
        }
        sb.append(myAssertion.asString(0, innerIndentInc));
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

        AssumeStmt that = (AssumeStmt) o;

        if (myIsStipulate != that.myIsStipulate)
            return false;
        return myAssertion.equals(that.myAssertion);

    }

    /**
     * <p>
     * This method returns the assumed assertion expression.
     * </p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getAssertion() {
        return myAssertion;
    }

    /**
     * <p>
     * This method checks to see if this is is a stipulate assume statement.
     * </p>
     *
     * @return {@code true} if it is a stipulate assume statement, {@code false} otherwise.
     */
    public final boolean getIsStipulate() {
        return myIsStipulate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myAssertion.hashCode();
        result = 31 * result + (myIsStipulate ? 1 : 0);
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
        return new AssumeStmt(cloneLocation(), myAssertion.clone(), myIsStipulate);
    }

}
