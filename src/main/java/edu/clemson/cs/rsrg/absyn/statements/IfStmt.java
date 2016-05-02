/**
 * IfStmt.java
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
import edu.clemson.cs.rsrg.absyn.statements.codeblocks.IfConditionBlock;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the if-else statements
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class IfStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The if part of this statement</p> */
    private final IfConditionBlock myIfClause;

    /** <p>The else-if part of this statement</p> */
    private final List<IfConditionBlock> myElseIfs;

    /** <p>The else part of this statement</p> */
    private final List<Statement> myElseStatements;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an if-elseif-else statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param ifClause A {@link IfConditionBlock} representing if block.
     * @param elseifpairs A list of {@link IfConditionBlock} representing all
     *                    the else-if pairs.
     * @param elseStatements The list of {@link Statement}s that are in
     *                       the else block.
     */
    public IfStmt(Location l, IfConditionBlock ifClause,
            List<IfConditionBlock> elseifpairs, List<Statement> elseStatements) {
        super(l);
        myIfClause = ifClause;
        myElseIfs = elseifpairs;
        myElseStatements = elseStatements;
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
        sb.append("IfStmt\n");

        if (myIfClause != null) {
            sb.append(myIfClause.asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append("\n");
        }

        if (myElseIfs != null) {
            for (IfConditionBlock c : myElseIfs) {
                printSpace(indentSize + innerIndentSize, sb);
                sb.append("Else ");
                sb.append(c.asString(0, innerIndentSize));
                sb.append("\n");
            }
        }

        if (myElseStatements != null) {
            sb.append("Else\n");

            for (Statement s : myElseStatements) {
                sb.append(s.asString(indentSize + innerIndentSize,
                        innerIndentSize));
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link IfStmt} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof IfStmt) {
            IfStmt eAsIfStmt = (IfStmt) o;
            result = myLoc.equals(eAsIfStmt.myLoc);

            if (result) {
                result = myIfClause.equals(eAsIfStmt.myIfClause);

                if (myElseIfs != null && eAsIfStmt.myElseIfs != null) {
                    Iterator<IfConditionBlock> thisIfCodeBlockItem =
                            myElseIfs.iterator();
                    Iterator<IfConditionBlock> eIfCodeBlockItem =
                            eAsIfStmt.myElseIfs.iterator();

                    while (result && thisIfCodeBlockItem.hasNext()
                            && eIfCodeBlockItem.hasNext()) {
                        result &=
                                thisIfCodeBlockItem.next().equals(
                                        eIfCodeBlockItem.next());
                    }

                    //Both had better have run out at the same time
                    result &=
                            (!thisIfCodeBlockItem.hasNext())
                                    && (!eIfCodeBlockItem.hasNext());
                }

                if (myElseStatements != null
                        && eAsIfStmt.myElseStatements != null) {
                    Iterator<Statement> thisStatements =
                            myElseStatements.iterator();
                    Iterator<Statement> eStatements =
                            eAsIfStmt.myElseStatements.iterator();

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
     * <p>This method returns a deep copy of the statements in this else block</p>
     *
     * @return The list of {@link Statement} representation objects.
     */
    public List<Statement> getElseclause() {
        List<Statement> copyStatements = new ArrayList<>();
        for (Statement s : myElseStatements) {
            copyStatements.add(s.clone());
        }

        return copyStatements;
    }

    /**
     * <p>This method returns a deep copy of all the else-if blocks in
     * this {@link IfStmt}.</p>
     *
     * @return The list of {@link IfConditionBlock} representation objects.
     */
    public List<IfConditionBlock> getElseifpairs() {
        List<IfConditionBlock> newElseifpairs = new ArrayList<>();
        for (IfConditionBlock item : myElseIfs) {
            newElseifpairs.add((IfConditionBlock) item.clone());
        }

        return newElseifpairs;
    }

    /**
     * <p>This method returns a deep copy of the if block in
     * this {@link IfStmt}.</p>
     *
     * @return The {@link IfConditionBlock} representation object.
     */
    public IfConditionBlock getIfClause() {
        return (IfConditionBlock) myIfClause.clone();
    }

    /**
     * <p>Returns the statement in string format.</p>
     *
     * @return Statement as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(myIfClause.toString());

        for (IfConditionBlock item : myElseIfs) {
            sb.append("Else ");
            sb.append(item.toString());
        }

        sb.append("Else\n");
        for (Statement s : myElseStatements) {
            sb.append("\t");
            sb.append(s.toString());
            sb.append("\n");
        }
        sb.append("end");

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
        return new IfStmt(new Location(myLoc), getIfClause(), getElseifpairs(),
                getElseclause());
    }

}