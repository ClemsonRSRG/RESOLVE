/**
 * IfCodeBlockItem.java
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
package edu.clemson.cs.rsrg.absyn.items;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.Statement;
import edu.clemson.cs.rsrg.absyn.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.statements.IfStmt;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class that stores a code block that gets executed
 * if the condition is met. Used by the {@link IfStmt}.</p>
 *
 * @version 2.0
 */
public class IfCodeBlockItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The testing expression.</p> */
    private final ProgramExp myTestingExp;

    /**
     * <p>The list of statements that gets executed
     * if the testing expression is met</p>
     */
    private final List<Statement> myStatements;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a code block with a condition test.
     * If the testing expression is met, then the code block is
     * executed.</p>
     *
     * @param l A {@link Location} representation object.
     * @param test A {@link ProgramExp} testing expression.
     * @param statements The list of {@link Statement}s that are in
     *                   this block.
     */
    public IfCodeBlockItem(Location l, ProgramExp test,
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
        printSpace(indentSize, sb);
        sb.append("IfCodeBlockItem\n");

        if (myTestingExp != null) {
            sb.append(myTestingExp.asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append(" then\n");
        }

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
     * for the {@link IfCodeBlockItem} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public IfCodeBlockItem clone() {
        return new IfCodeBlockItem(new Location(myLoc), myTestingExp.clone(),
                getStatements());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link IfCodeBlockItem} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof IfCodeBlockItem) {
            IfCodeBlockItem eAsIfCodeBlockItem = (IfCodeBlockItem) o;
            result = myLoc.equals(eAsIfCodeBlockItem.myLoc);

            if (result) {
                if (myTestingExp != null
                        && eAsIfCodeBlockItem.myTestingExp != null) {
                    result &=
                            myTestingExp
                                    .equals(eAsIfCodeBlockItem.myTestingExp);

                    if (myStatements != null
                            && eAsIfCodeBlockItem.myStatements != null) {
                        Iterator<Statement> thisStatements =
                                myStatements.iterator();
                        Iterator<Statement> eStatements =
                                eAsIfCodeBlockItem.myStatements.iterator();

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
     * <p>Returns a deep copy this expression's testing expression.</p>
     *
     * @return The testing {@link ProgramExp} object.
     */
    public ProgramExp getTest() {
        return myTestingExp.clone();
    }

    /**
     * <p>This method returns a deep copy of the list of statements
     * in this code block.</p>
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
        sb.append(myTestingExp.toString());
        sb.append(" then\n");

        for (Statement s : myStatements) {
            sb.append("\t");
            sb.append(s.toString());
            sb.append("\n");
        }

        sb.append("end");

        return sb.toString();
    }

}