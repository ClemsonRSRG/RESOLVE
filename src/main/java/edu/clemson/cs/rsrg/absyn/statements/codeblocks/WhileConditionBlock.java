/**
 * WhileConditionBlock.java
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
package edu.clemson.cs.rsrg.absyn.statements.codeblocks;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.absyn.Statement;
import edu.clemson.cs.rsrg.absyn.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.programexpr.ProgramVariableExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the while loop condition block
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class WhileConditionBlock extends ConditionBlock {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The changing clause</p> */
    private final List<ProgramVariableExp> myChangingVars;

    /** <p>The maintaining clause</p> */
    private final Exp myMaintainingClause;

    /** <p>The decreasing clause</p> */
    private final Exp myDecreasingClause;

    /** <p>The elapsed time clause</p> */
    private final Exp myElapsedTimeClause;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a while loop code block with a condition test.
     * If the testing expression is met, then the code block is
     * executed.</p>
     *
     * @param l A {@link Location} representation object.
     * @param test A {@link ProgramExp} testing expression.
     * @param statements The list of {@link Statement}s that are in
     *                   this block.
     */
    public WhileConditionBlock(Location l, ProgramExp test,
            List<ProgramVariableExp> changingVars, Exp maintaining,
            Exp decreasing, Exp elapsedTime, List<Statement> statements) {
        super(l, test, statements);
        myChangingVars = changingVars;
        myMaintainingClause = maintaining;
        myDecreasingClause = decreasing;
        myElapsedTimeClause = elapsedTime;
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

        if (myTestingExp != null) {
            sb.append("While ");
            sb.append(myTestingExp.asString(0, innerIndentSize));
            sb.append("\n");
        }

        if (myChangingVars != null) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append("changing ");

            Iterator<ProgramVariableExp> expIterator =
                    myChangingVars.iterator();
            while (expIterator.hasNext()) {
                sb.append(expIterator.next().asString(0, innerIndentSize));

                if (expIterator.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(";\n");
        }

        if (myMaintainingClause != null) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append("maintaining ");
            sb.append(myMaintainingClause.asString(0, innerIndentSize));
            sb.append(";\n");
        }

        if (myDecreasingClause != null) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append("decreasing ");
            sb.append(myDecreasingClause.asString(0, innerIndentSize));
            sb.append(";\n");
        }

        if (myElapsedTimeClause != null) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append("elapsed_time ");
            sb.append(myElapsedTimeClause.asString(0, innerIndentSize));
            sb.append(";\n");
        }

        printSpace(indentSize, sb);
        sb.append("do\n");

        // Print the statements
        sb
                .append(super.asString(indentSize + innerIndentSize,
                        innerIndentSize));

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link WhileConditionBlock} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof WhileConditionBlock) {
            WhileConditionBlock eAsWhileConditionBlock =
                    (WhileConditionBlock) o;
            result = super.equals(eAsWhileConditionBlock);

            if (result) {
                if (myChangingVars != null
                        && eAsWhileConditionBlock.myChangingVars != null) {

                    if (myChangingVars != null
                            && eAsWhileConditionBlock.myChangingVars != null) {
                        Iterator<ProgramVariableExp> thisChangingVars =
                                myChangingVars.iterator();
                        Iterator<ProgramVariableExp> eChangingVars =
                                eAsWhileConditionBlock.myChangingVars
                                        .iterator();

                        while (result && thisChangingVars.hasNext()
                                && eChangingVars.hasNext()) {
                            result &=
                                    thisChangingVars.next().equals(
                                            eChangingVars.next());
                        }

                        //Both had better have run out at the same time
                        result &=
                                (!thisChangingVars.hasNext())
                                        && (!eChangingVars.hasNext());
                    }
                }
                else {
                    result &= false;
                }

                if (myMaintainingClause != null
                        && eAsWhileConditionBlock.myMaintainingClause != null) {
                    result &=
                            myMaintainingClause
                                    .equals(eAsWhileConditionBlock.myMaintainingClause);
                }
                else {
                    result &= false;
                }

                if (myDecreasingClause != null
                        && eAsWhileConditionBlock.myDecreasingClause != null) {
                    result &=
                            myDecreasingClause
                                    .equals(eAsWhileConditionBlock.myDecreasingClause);
                }
                else {
                    result &= false;
                }

                if (myElapsedTimeClause != null
                        && eAsWhileConditionBlock.myElapsedTimeClause != null) {
                    result &=
                            myElapsedTimeClause
                                    .equals(eAsWhileConditionBlock.myElapsedTimeClause);
                }
                else {
                    result &= false;
                }
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the list of changing variables
     * in this loop condition block.</p>
     *
     * @return The list of {@link ProgramVariableExp}s.
     */
    public List<ProgramVariableExp> getChangingVars() {
        List<ProgramVariableExp> copyChangingVars = new ArrayList<>();
        for (ProgramVariableExp exp : myChangingVars) {
            copyChangingVars.add((ProgramVariableExp) exp.clone());
        }

        return copyChangingVars;
    }

    /**
     * <p>This method returns a deep copy of the decreasing
     * clause in this loop condition block.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getDecreasingClause() {
        Exp newDecreasingClause = null;
        if (myDecreasingClause != null) {
            newDecreasingClause = myDecreasingClause.clone();
        }

        return newDecreasingClause;
    }

    /**
     * <p>This method returns a deep copy of the elapsed time
     * clause in this loop condition block.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getElapsedTimeClause() {
        Exp newElapsedTimeClause = null;
        if (myElapsedTimeClause != null) {
            newElapsedTimeClause = myElapsedTimeClause.clone();
        }

        return newElapsedTimeClause;
    }

    /**
     * <p>This method returns a deep copy of the maintaining
     * clause in this loop condition block.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getMaintainingClause() {
        Exp newMaintainingClause = null;
        if (myMaintainingClause != null) {
            newMaintainingClause = myMaintainingClause.clone();
        }

        return newMaintainingClause;
    }

    /**
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (myTestingExp != null) {
            sb.append(toString());
            sb.append("\n");
        }

        if (myChangingVars != null) {
            sb.append("changing ");

            Iterator<ProgramVariableExp> expIterator =
                    myChangingVars.iterator();
            while (expIterator.hasNext()) {
                sb.append(expIterator.next().toString());

                if (expIterator.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(";\n");
        }

        if (myMaintainingClause != null) {
            sb.append("maintaining ");
            sb.append(myMaintainingClause.toString());
            sb.append(";\n");
        }

        if (myDecreasingClause != null) {
            sb.append("decreasing ");
            sb.append(myDecreasingClause.toString());
            sb.append(";\n");
        }

        if (myElapsedTimeClause != null) {
            sb.append("elapsed_time ");
            sb.append(myElapsedTimeClause.toString());
            sb.append(";\n");
        }

        sb.append("do\n");
        sb.append(super.toString());
        sb.append("end\n");

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
        return new WhileConditionBlock(myLoc, myTestingExp.clone(),
                getChangingVars(), getMaintainingClause(),
                getDecreasingClause(), getElapsedTimeClause(), getStatements());
    }

}