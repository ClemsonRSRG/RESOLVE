/**
 * AssumeStmt.java
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
package edu.clemson.cs.rsrg.vcgeneration.absyn;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.absyn.Statement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;

/**
 * <p>This is the class that builds the assume statements created by
 * the {@link VCGenerator}. Since the user cannot supply their own
 * assume clauses, any instances of this class will solely be created
 * by the {@link VCGenerator}.</p>
 *
 * @version 2.0
 */
public class AssumeStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The assume assertion expression</p> */
    private final Exp myAssertion;

    /** <p>This flag indicates if this is an stipulate assume clause or not</p> */
    private final boolean myIsStipulate;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a programming function call expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param assertion A {@link Exp} representing the assume statement's
     *                  assertion expression.
     * @param isStipulate A flag to indicate whether or not this is a
     *                    stipulate assume statement.
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
        sb.append("AssumeStmt\n");
        sb.append(myAssertion.asString(indentSize + innerIndentSize,
                innerIndentSize));

        return sb.toString();
    }

    /**
     * <p>Implemented by this concrete subclass of {@link Statement} to manufacture
     * a copy of themselves.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public AssumeStmt clone() {
        return new AssumeStmt(new Location(myLoc), myAssertion.clone(),
                myIsStipulate);
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link AssumeStmt} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof AssumeStmt) {
            AssumeStmt eAsAssumeStmt = (AssumeStmt) o;
            result = myLoc.equals(eAsAssumeStmt.myLoc);

            if (result) {
                result = myAssertion.equals(eAsAssumeStmt.myAssertion);
                result &= (myIsStipulate == eAsAssumeStmt.myIsStipulate);
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the assume assertion expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getAssertion() {
        return myAssertion.clone();
    }

    /**
     * <p>This method checks to see if this is is a stipulate assume statement.</p>
     *
     * @return True if it is a stipulate assume statement, false otherwise.
     */
    public final boolean getIsStipulate() {
        return myIsStipulate;
    }

    /**
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (myIsStipulate) {
            sb.append("Stipulate ");
        }
        else {
            sb.append("Assume ");
        }
        sb.append(myAssertion.toString());

        return sb.toString();
    }

}