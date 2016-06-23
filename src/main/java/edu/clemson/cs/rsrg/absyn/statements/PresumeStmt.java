/**
 * PresumeStmt.java
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

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the class for all the presume statement objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class PresumeStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The presume assertion expression</p> */
    private final Exp myAssertion;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a presume statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param assertion A {@link Exp} representing the presume statement's
     *                  assertion expression.
     */
    public PresumeStmt(Location l, Exp assertion) {
        super(l);
        myAssertion = assertion;
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
        sb.append("Presume ");
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

        PresumeStmt that = (PresumeStmt) o;

        return myAssertion.equals(that.myAssertion);

    }

    /**
     * <p>This method returns the presumed assertion expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getAssertion() {
        return myAssertion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return myAssertion.hashCode();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Statement copy() {
        return new PresumeStmt(new Location(myLoc), myAssertion.clone());
    }

}