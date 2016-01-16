/**
 * PresumeStmt.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.statements;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.absyn.Statement;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the class for all the presume statements
 * that the compiler builds from the ANTLR4 AST tree.</p>
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
     * <p>This method creates a special indented
     * text version of the class as a string.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentSize The additional indentation increment
     *                        for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public String asString(int indentSize, int innerIndentSize) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("PresumeStmt\n");
        sb.append(myAssertion.asString(indentSize + innerIndentSize,
                innerIndentSize));

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link PresumeStmt} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof PresumeStmt) {
            PresumeStmt eAsPresumeStmt = (PresumeStmt) o;
            result = myLoc.equals(eAsPresumeStmt.myLoc);

            if (result) {
                result = myAssertion.equals(eAsPresumeStmt.myAssertion);
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the presume assertion expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getAssertion() {
        return myAssertion.clone();
    }

    /**
     * <p>Returns the statement in string format.</p>
     *
     * @return Statement as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Presume " + myAssertion.toString());

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
    protected Statement copy() {
        return new PresumeStmt(new Location(myLoc), getAssertion());
    }

}