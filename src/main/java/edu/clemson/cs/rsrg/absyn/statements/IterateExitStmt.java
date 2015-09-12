/**
 * IterateExitStmt.java
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
import edu.clemson.cs.rsrg.absyn.blocks.WhenConditionBlock;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>This is the class for all the when-do statements
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class IterateExitStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The condition part of this statement</p> */
    private final WhenConditionBlock myConditionBlock;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a when-do condition statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param codeBlock A {@link WhenConditionBlock} representation object.
     */
    public IterateExitStmt(Location l, WhenConditionBlock codeBlock) {
        super(l);
        myConditionBlock = codeBlock;
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
        sb.append("IterateExitStmt\n");

        if (myConditionBlock != null) {
            sb.append(myConditionBlock.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link IterateExitStmt} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof IfStmt) {
            IterateExitStmt eAsIterateExitStmt = (IterateExitStmt) o;
            result = myLoc.equals(eAsIterateExitStmt.myLoc);

            if (result) {
                result =
                        myConditionBlock
                                .equals(eAsIterateExitStmt.myConditionBlock);
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the when-do code block in
     * this {@link IterateExitStmt}.</p>
     *
     * @return The {@link WhenConditionBlock} representation object.
     */
    public WhenConditionBlock getWhenBlock() {
        return (WhenConditionBlock) myConditionBlock.clone();
    }

    /**
     * <p>Returns the statement in string format.</p>
     *
     * @return Statement as a string.
     */
    @Override
    public String toString() {
        return myConditionBlock.toString();
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
        return new IterateExitStmt(new Location(myLoc), getWhenBlock());
    }

}