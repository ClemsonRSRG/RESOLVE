/**
 * WhileStmt.java
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
import edu.clemson.cs.rsrg.absyn.statements.codeblocks.WhileConditionBlock;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the class for all the while loop statements
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class WhileStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The while loop block of this statement</p> */
    private final WhileConditionBlock myWhileLoopBlock;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a while loop statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param whileLoopBlock A {@link WhileConditionBlock} representing while block.
     */
    public WhileStmt(Location l, WhileConditionBlock whileLoopBlock) {
        super(l);
        myWhileLoopBlock = whileLoopBlock;
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
        sb.append("WhileStmt\n");

        if (myWhileLoopBlock != null) {
            sb.append(myWhileLoopBlock.asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link WhileStmt} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof WhileStmt) {
            WhileStmt eAsWhileStmt = (WhileStmt) o;
            result = myLoc.equals(eAsWhileStmt.myLoc);

            if (result) {
                result = myWhileLoopBlock.equals(eAsWhileStmt.myWhileLoopBlock);
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the while loop block in
     * this {@link WhileStmt}.</p>
     *
     * @return The {@link WhileConditionBlock} representation object.
     */
    public WhileConditionBlock getWhileLoopBlock() {
        return (WhileConditionBlock) myWhileLoopBlock.clone();
    }

    /**
     * <p>Returns the statement in string format.</p>
     *
     * @return Statement as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(myWhileLoopBlock.toString());

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
        return new WhileStmt(new Location(myLoc), getWhileLoopBlock());
    }

}