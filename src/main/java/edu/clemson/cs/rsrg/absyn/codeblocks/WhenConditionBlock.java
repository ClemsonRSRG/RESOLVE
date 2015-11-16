/**
 * WhenConditionBlock.java
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
package edu.clemson.cs.rsrg.absyn.codeblocks;

import edu.clemson.cs.rsrg.absyn.ConditionBlock;
import edu.clemson.cs.rsrg.absyn.Statement;
import edu.clemson.cs.rsrg.absyn.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.List;

/**
 * <p>This is the class for all the when condition block
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class WhenConditionBlock extends ConditionBlock {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a when code block with a condition test.
     * If the testing expression is met, then the code block is
     * executed.</p>
     *
     * @param l A {@link Location} representation object.
     * @param test A {@link ProgramExp} testing expression.
     * @param statements The list of {@link Statement}s that are in
     *                   this block.
     */
    public WhenConditionBlock(Location l, ProgramExp test,
            List<Statement> statements) {
        super(l, test, statements);
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
        sb.append("WhenConditionBlock\n");

        if (myTestingExp != null) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append("When ");
            sb.append(myTestingExp.asString(0, innerIndentSize));
            printSpace(indentSize + innerIndentSize, sb);
            sb.append("\ndo\n");
        }

        // Print the statements
        sb
                .append(super.asString(indentSize + innerIndentSize,
                        innerIndentSize));
        printSpace(indentSize + innerIndentSize, sb);
        sb.append("exit\n");

        return sb.toString();
    }

    /**
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(myTestingExp.toString());
        sb.append(" do\n");
        sb.append(super.toString());
        sb.append("exit");

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by this concrete subclass of {@link ConditionBlock} to manufacture
     * a copy of themselves.</p>
     *
     * @return A new {@link ConditionBlock} that is a deep copy of the original.
     */
    @Override
    protected ConditionBlock copy() {
        return new WhenConditionBlock(myLoc, myTestingExp.clone(),
                getStatements());
    }

}