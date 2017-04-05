/*
 * AssertionClause.java
 * ---------------------------------
 * Copyright (c) 2017
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
        super(l);
        myAssertionExp = assertionExp;
        myClauseType = type;
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
        sb.append(myAssertionExp.asString(0, innerIndentInc));

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

        return new AssertionClause(cloneLocation(), myClauseType,
                myAssertionExp.clone(), newWhichEntailsExp);
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
        result =
                31
                        * result
                        + (myWhichEntailsExp != null ? myWhichEntailsExp
                                .hashCode() : 0);
        return result;
    }

}