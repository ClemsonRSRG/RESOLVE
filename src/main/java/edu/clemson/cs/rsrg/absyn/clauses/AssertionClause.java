/*
 * AssertionClause.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.clauses;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all mathematical assertion clause objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @author Daniel Welch
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class AssertionClause extends ResolveConceptualElement {

    // ===========================================================
    // ClauseType
    // ===========================================================

    /**
     * <p>This defines the various different assertion clause types.</p>
     *
     * @version 2.0
     */
    public enum ClauseType {
        CONSTRAINT {

            @Override
            public String toString() {
                return "constraint ";
            }

        },
        CONVENTION {

            @Override
            public String toString() {
                return "convention ";
            }

        },
        CORRESPONDENCE {

            @Override
            public String toString() {
                return "correspondence ";
            }

        },
        DECREASING {

            @Override
            public String toString() {
                return "decreasing ";
            }

        },
        DURATION {

            @Override
            public String toString() {
                return "duration ";
            }

        },
        ELAPSEDTIME {

            @Override
            public String toString() {
                return "elapsed_time ";
            }

        },
        ENSURES {

            @Override
            public String toString() {
                return "ensures ";
            }

        },
        MAINTAINING {

            @Override
            public String toString() {
                return "maintaining ";
            }

        },
        MANIPDISP {

            @Override
            public String toString() {
                return "manip_disp ";
            }

        },
        REQUIRES {

            @Override
            public String toString() {
                return "requires ";
            }

        }
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The mathematical assertion expression</p> */
    private final Exp myAssertionExp;

    /** <p>The type of clause</p> */
    private final ClauseType myClauseType;

    /** <p>The list of shared variable expressions that is affecting this clause.</p> */
    private final List<Exp> myInvolvedSharedVars;

    /** <p>The which_entails mathematical assertion expression</p> */
    private final Exp myWhichEntailsExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a mathematical assertion clause.</p>
     *
     * @param l A {@link Location} representation object.
     * @param type Indicates which type of assertion clause.
     * @param assertionExp A {@link Exp} representing the mathematical assertion.
     */
    public AssertionClause(Location l, ClauseType type, Exp assertionExp) {
        this(l, type, assertionExp, null);
    }

    /**
     * <p>This constructs a mathematical assertion clause with a
     * {@code which_entails} assertion.</p>
     *
     * @param l A {@link Location} representation object.
     * @param type Indicates which type of assertion clause.
     * @param assertionExp A {@link Exp} representing the mathematical assertion.
     * @param whichEntailsExp A {@link Exp} representing the {@code which_entails} mathematical assertion.
     */
    public AssertionClause(Location l, ClauseType type, Exp assertionExp,
            Exp whichEntailsExp) {
        this(l, type, assertionExp, whichEntailsExp, new ArrayList<Exp>());
    }

    /**
     * <p>This constructs a mathematical assertion clause with a
     * {@code which_entails} assertion and a list of variable
     * expressions that is affecting this clause.</p>
     *
     * @param l A {@link Location} representation object.
     * @param type Indicates which type of assertion clause.
     * @param assertionExp A {@link Exp} representing the mathematical assertion.
     * @param whichEntailsExp A {@link Exp} representing the {@code which_entails} mathematical assertion.
     * @param involvedSharedVars  The list of variable expressions that are listed to be involved
     *                            in this assertion clause.
     */
    public AssertionClause(Location l, ClauseType type, Exp assertionExp,
            Exp whichEntailsExp, List<Exp> involvedSharedVars) {
        super(l);
        myAssertionExp = assertionExp;
        myClauseType = type;
        myInvolvedSharedVars = involvedSharedVars;
        myWhichEntailsExp = whichEntailsExp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append(myClauseType.toString());

        // Indent size for the printing the assertion expression
        int assertionIndentSize = 0;

        // Add any shared variables being involved
        if (!myInvolvedSharedVars.isEmpty()) {
            sb.append("\n");
            printSpace(indentSize + innerIndentInc, sb);

            sb.append("involves ");
            Iterator<Exp> it = myInvolvedSharedVars.iterator();
            while (it.hasNext()) {
                Exp exp = it.next();
                sb.append(exp.asString(0, innerIndentInc));

                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(";\n");

            assertionIndentSize = indentSize + innerIndentInc;
        }

        sb.append(myAssertionExp.asString(assertionIndentSize, innerIndentInc));

        // Add any which entails clauses
        if (myWhichEntailsExp != null) {
            sb.append(" which_entails ");
            sb.append(myWhichEntailsExp.asString(0, innerIndentInc));
        }
        sb.append(";");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final AssertionClause clone() {
        Exp newWhichEntailsExp = null;
        if (myWhichEntailsExp != null) {
            newWhichEntailsExp = myWhichEntailsExp.clone();
        }

        List<Exp> newInvolvesExp = new ArrayList<>();
        for (Exp exp : myInvolvedSharedVars) {
            newInvolvesExp.add(exp.clone());
        }

        return new AssertionClause(cloneLocation(), myClauseType,
                myAssertionExp.clone(), newWhichEntailsExp, newInvolvesExp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AssertionClause that = (AssertionClause) o;

        if (!myAssertionExp.equals(that.myAssertionExp))
            return false;
        if (myClauseType != that.myClauseType)
            return false;
        if (!myInvolvedSharedVars.equals(that.myInvolvedSharedVars))
            return false;
        return myWhichEntailsExp != null ? myWhichEntailsExp
                .equals(that.myWhichEntailsExp)
                : that.myWhichEntailsExp == null;
    }

    /**
     * <p>This method returns the mathematical assertion for this clause.</p>
     *
     * @return The {@link Exp} representing the mathematical assertion.
     */
    public final Exp getAssertionExp() {
        return myAssertionExp;
    }

    /**
     * <p>This method returns the type of clause.</p>
     *
     * @return The {@link ClauseType} object.
     */
    public final ClauseType getClauseType() {
        return myClauseType;
    }

    /**
     * <p>Returns the list of shared variable expressions that are involved
     * in this clause.</p>
     *
     * @return The list of involved {@link Exp}s.
     */
    public final List<Exp> getInvolvedSharedVars() {
        return myInvolvedSharedVars;
    }

    /**
     * <p>This method returns the {@code which_entails} mathematical assertion
     * for this clause.</p>
     *
     * @return The {@link Exp} representing the {@code which_entails} expression.
     */
    public final Exp getWhichEntailsExp() {
        return myWhichEntailsExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myAssertionExp.hashCode();
        result = 31 * result + myClauseType.hashCode();
        result = 31 * result + myInvolvedSharedVars.hashCode();
        result =
                31
                        * result
                        + (myWhichEntailsExp != null ? myWhichEntailsExp
                                .hashCode() : 0);
        return result;
    }

}