/**
 * SelectionStmt.java
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
import edu.clemson.cs.rsrg.absyn.codeblocks.WhenConditionBlock;
import edu.clemson.cs.rsrg.absyn.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the selection statements
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class SelectionStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The testing expression</p> */
    private final ProgramExp myTestingExp;

    /** <p>The different when condition statement blocks</p> */
    private final List<WhenConditionBlock> myWhenConditionBlocks;

    /** <p>The default statement block</p> */
    private final List<Statement> myDefaultBlock;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a when-do condition statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param test A {@link ProgramExp} testing expression.
     * @param whenpairs A list of {@link WhenConditionBlock} representation objects.
     * @param defaultclause A list of {@link Statement} representation objects.
     */
    public SelectionStmt(Location l, ProgramExp test,
            List<WhenConditionBlock> whenpairs, List<Statement> defaultclause) {
        super(l);
        myTestingExp = test;
        myWhenConditionBlocks = whenpairs;
        myDefaultBlock = defaultclause;
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
        sb.append("SelectionStmt\n");

        if (myTestingExp != null) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append("Case: ");
            sb.append(myTestingExp.asString(0, innerIndentSize));
            sb.append(" of\n");
        }

        if (myWhenConditionBlocks != null) {
            for (WhenConditionBlock block : myWhenConditionBlocks) {
                sb.append(block.asString(indentSize + innerIndentSize,
                        innerIndentSize));
                sb.append("\n");
            }
        }

        if (myDefaultBlock != null) {
            for (Statement s : myDefaultBlock) {
                sb.append(s.asString(indentSize + innerIndentSize,
                        innerIndentSize));
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link SelectionStmt} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof SelectionStmt) {
            SelectionStmt eAsSelectionStmt = (SelectionStmt) o;
            result = myLoc.equals(eAsSelectionStmt.myLoc);

            if (result) {
                result = myTestingExp.equals(eAsSelectionStmt.myTestingExp);

                if (myWhenConditionBlocks != null
                        && eAsSelectionStmt.myWhenConditionBlocks != null) {
                    Iterator<WhenConditionBlock> thisWhenCodeBlockItem =
                            myWhenConditionBlocks.iterator();
                    Iterator<WhenConditionBlock> eWhenCodeBlockItem =
                            eAsSelectionStmt.myWhenConditionBlocks.iterator();

                    while (result && thisWhenCodeBlockItem.hasNext()
                            && eWhenCodeBlockItem.hasNext()) {
                        result &=
                                thisWhenCodeBlockItem.next().equals(
                                        eWhenCodeBlockItem.next());
                    }

                    //Both had better have run out at the same time
                    result &=
                            (!thisWhenCodeBlockItem.hasNext())
                                    && (!eWhenCodeBlockItem.hasNext());
                }

                if (myDefaultBlock != null
                        && eAsSelectionStmt.myDefaultBlock != null) {
                    Iterator<Statement> thisStatements =
                            myDefaultBlock.iterator();
                    Iterator<Statement> eStatements =
                            eAsSelectionStmt.myDefaultBlock.iterator();

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
     * in this code block.</p>
     *
     * @return The list of {@link Statement}s.
     */
    public final List<Statement> getDefaultStatements() {
        List<Statement> copyStatements = new ArrayList<>();
        for (Statement s : myDefaultBlock) {
            copyStatements.add(s.clone());
        }

        return copyStatements;
    }

    /**
     * <p>Returns a deep copy this expression's testing expression.</p>
     *
     * @return The testing {@link ProgramExp} object.
     */
    public final ProgramExp getTest() {
        return myTestingExp.clone();
    }

    /**
     * <p>This method returns a deep copy of all the when condition blocks in
     * this {@link SelectionStmt}.</p>
     *
     * @return The list of {@link WhenConditionBlock} representation objects.
     */
    public List<WhenConditionBlock> getWhenConditionPairs() {
        List<WhenConditionBlock> newWhenConditionPairs = new ArrayList<>();
        for (WhenConditionBlock item : myWhenConditionBlocks) {
            newWhenConditionPairs.add((WhenConditionBlock) item.clone());
        }

        return newWhenConditionPairs;
    }

    /**
     * <p>Returns the statement in string format.</p>
     *
     * @return Statement as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (myTestingExp != null) {
            sb.append("Case: ");
            sb.append(myTestingExp.toString());
            sb.append(" of\n");
        }

        for (WhenConditionBlock block : myWhenConditionBlocks) {
            sb.append("\t");
            sb.append(block.toString());
            sb.append("\n");
        }

        for (Statement s : myDefaultBlock) {
            sb.append("\t");
            sb.append(s.toString());
            sb.append("\n");
        }

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
        return new SelectionStmt(new Location(myLoc), getTest(),
                getWhenConditionPairs(), getDefaultStatements());
    }

}