/**
 * AuxCodeBlockStmt.java
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

import edu.clemson.cs.rsrg.absyn.Statement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the auxiliary code block statement
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class AuxCodeBlockStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The list of auxiliary statements inside this block</p> */
    private final List<Statement> myStatements;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a programming function call expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param statements The list of {@link Statement}s that are in
     *                   this auxiliary block.
     */
    public AuxCodeBlockStmt(Location l, List<Statement> statements) {
        super(l);
        myStatements = statements;
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
        sb.append("AuxCode\n");

        if (myStatements != null) {
            for (Statement s : myStatements) {
                sb.append(s.asString(indentSize + innerIndentSize,
                        innerIndentSize));
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * <p>Implemented by this concrete subclass of {@link Statement} to manufacture
     * a copy of themselves.</p>
     *
     * @return A deep copy of the object.
     */
    public AuxCodeBlockStmt clone() {
        return new AuxCodeBlockStmt(new Location(myLoc), getStatements());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link AuxCodeBlockStmt} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof AuxCodeBlockStmt) {
            AuxCodeBlockStmt eAsAuxCodeBlockStmt = (AuxCodeBlockStmt) o;
            result = myLoc.equals(eAsAuxCodeBlockStmt.myLoc);

            if (result) {
                if (myStatements != null
                        && eAsAuxCodeBlockStmt.myStatements != null) {
                    Iterator<Statement> thisStatements =
                            myStatements.iterator();
                    Iterator<Statement> eStatements =
                            eAsAuxCodeBlockStmt.myStatements.iterator();

                    while (result && thisStatements.hasNext()
                            && eStatements.hasNext()) {
                        result &=
                                thisStatements.next()
                                        .equals(eStatements.next());
                    }

                    //Both had better have run out at the same time
                    result &=
                            (!thisStatements.hasNext())
                                    && (!eStatements.hasNext());
                }
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the list of statements
     * in this auxiliary code block.</p>
     *
     * @return The list of {@link Statement}s.
     */
    public List<Statement> getStatements() {
        List<Statement> copyStatements = new ArrayList<>();
        for (Statement s : myStatements) {
            copyStatements.add(s.clone());
        }

        return copyStatements;
    }

    /**
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Aux_Code\n");

        for (Statement s : myStatements) {
            sb.append("\t");
            sb.append(s.toString());
            sb.append("\n");
        }

        sb.append("end");

        return sb.toString();
    }

}