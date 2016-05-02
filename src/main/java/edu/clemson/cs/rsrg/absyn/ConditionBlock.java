/**
 * ConditionBlock.java
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
package edu.clemson.cs.rsrg.absyn;

import edu.clemson.cs.rsrg.absyn.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the abstract base class for all the condition blocks
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public abstract class ConditionBlock extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>The testing expression.</p>
     */
    protected final ProgramExp myTestingExp;

    /**
     * <p>The list of statements that gets executed
     * if the testing expression is met</p>
     */
    protected final List<Statement> myStatements;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>A helper constructor that allow us to store the location,
     * of the created object, testing expression and list of statements
     * directly in this class.</p>
     *
     * @param l A {@link Location} representation object.
     * @param test A {@link ProgramExp} testing expression.
     * @param statements The list of {@link Statement}s that are in
     *                   this block.
     */
    protected ConditionBlock(Location l, ProgramExp test,
            List<Statement> statements) {
        super(l);
        myTestingExp = test;
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
     * <p>This method overrides the default clone method implementation
     * for all the classes that extend from {@link ConditionBlock}.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final ConditionBlock clone() {
        return this.copy();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link ConditionBlock} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ConditionBlock) {
            ConditionBlock eAsConditionBlock = (ConditionBlock) o;
            result = myLoc.equals(eAsConditionBlock.myLoc);

            if (result) {
                if (myTestingExp != null
                        && eAsConditionBlock.myTestingExp != null) {
                    result &=
                            myTestingExp.equals(eAsConditionBlock.myTestingExp);

                    if (myStatements != null
                            && eAsConditionBlock.myStatements != null) {
                        Iterator<Statement> thisStatements =
                                myStatements.iterator();
                        Iterator<Statement> eStatements =
                                eAsConditionBlock.myStatements.iterator();

                        while (result && thisStatements.hasNext()
                                && eStatements.hasNext()) {
                            result &=
                                    thisStatements.next().equals(
                                            eStatements.next());
                        }

                        //Both had better have run out at the same time
                        result &=
                                (!thisStatements.hasNext())
                                        && (!eStatements.hasNext());
                    }
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
    public final List<Statement> getStatements() {
        List<Statement> copyStatements = new ArrayList<>();
        for (Statement s : myStatements) {
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
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (Statement s : myStatements) {
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
     * <p>Implemented by concrete subclasses of {@link ConditionBlock} to manufacture
     * a copy of themselves.</p>
     *
     * @return A new {@link ConditionBlock} that is a deep copy of the original.
     */
    protected ConditionBlock copy() {
        throw new MiscErrorException(
                "Shouldn't be calling copy() from code block "
                        + this.getClass(), new CloneNotSupportedException());
    }

}