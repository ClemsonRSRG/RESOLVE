/**
 * ConditionItem.java
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
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class stores an if (or else if) path that
 * an if statement can take.</p>
 *
 * @version 2.0
 */
public class ConditionItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The testing expression.</p> */
    private final ProgramExp myTestingExp;

    /**
     * <p>The list of statements that gets executed
     * if the testing expression is met</p>
     */
    private final List<Statement> myThenStatements;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a programming function call expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param test A {@link ProgramExp} testing expression.
     * @param thenStatements The list of {@link Statement}s that are in
     *                       the then block.
     */
    public ConditionItem(Location l, ProgramExp test,
            List<Statement> thenStatements) {
        super(l);
        myTestingExp = test;
        myThenStatements = thenStatements;
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
        sb.append("ConditionItem\n");

        if (myTestingExp != null) {
            sb.append("If ");
            sb.append(myTestingExp.asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append(" then\n");
        }

        if (myThenStatements != null) {
            for (Statement s : myThenStatements) {
                sb.append(s.asString(indentSize + innerIndentSize,
                        innerIndentSize));
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link ConditionItem} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public ConditionItem clone() {
        return new ConditionItem(new Location(myLoc), myTestingExp.clone(),
                getThenStatements());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link ConditionItem} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ConditionItem) {
            ConditionItem eAsConditionItem = (ConditionItem) o;
            result = myLoc.equals(eAsConditionItem.myLoc);

            if (result) {
                if (myTestingExp != null
                        && eAsConditionItem.myTestingExp != null) {
                    result &=
                            myTestingExp.equals(eAsConditionItem.myTestingExp);

                    if (myThenStatements != null
                            && eAsConditionItem.myThenStatements != null) {
                        Iterator<Statement> thisStatements =
                                myThenStatements.iterator();
                        Iterator<Statement> eStatements =
                                eAsConditionItem.myThenStatements.iterator();

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
     * in the then code block.</p>
     *
     * @return The list of {@link Statement}s.
     */
    public List<Statement> getThenStatements() {
        List<Statement> copyStatements = new ArrayList<>();
        for (Statement s : myThenStatements) {
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
        sb.append("If ");
        sb.append(myTestingExp.toString());
        sb.append(" then\n");

        for (Statement s : myThenStatements) {
            sb.append("\t");
            sb.append(s.toString());
            sb.append("\n");
        }

        sb.append("end");

        return sb.toString();
    }

}