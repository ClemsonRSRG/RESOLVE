/*
 * MathAssertionDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.mathdecl;

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the mathematical assertion declarations
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class MathAssertionDec extends Dec {

    // ===========================================================
    // TheoremSubtype
    // ===========================================================

    /**
     * <p>This defines the various different theorem subtypes.</p>
     *
     * @version 2.0
     */
    public enum TheoremSubtype {
        NONE {

            @Override
            public String toString() {
                return "";
            }

        },
        ASSOCIATIVITY {

            @Override
            public String toString() {
                return "(Associative) ";
            }

        },
        COMMUTATIVITY {

            @Override
            public String toString() {
                return "(Commutative) ";
            }

        }
    }

    // ===========================================================
    // AssertionType
    // ===========================================================

    /**
     * <p>This defines the various different mathematical assertion types.</p>
     *
     * @version 2.0
     */
    public enum AssertionType {
        AXIOM {

            @Override
            public String toString() {
                return "Axiom ";
            }

        },
        THEOREM {

            @Override
            public String toString() {
                return "Theorem ";
            }

        },
        PROPERTY {

            @Override
            public String toString() {
                return "Property ";
            }

        },
        LEMMA {

            @Override
            public String toString() {
                return "Lemma ";
            }

        },
        COROLLARY {

            @Override
            public String toString() {
                return "Corollary ";
            }

        }
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The type of assertion</p> */
    private final AssertionType myAssertionType;

    /** <p>The mathematical assertion expression</p> */
    private final Exp myAssertion;

    /**
     * <p>For <em>theorem</em>s only, defines any special properties of the
     * theorem, such as if it is flagged as an associativity or commutativity
     * theorem in the definition.</p>
     */
    private final TheoremSubtype myTheoremSubtype;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs either a mathematical axiom,
     * corollary, lemma or property assertion.</p>
     *
     * @param name Name of the assertion declaration.
     * @param assertionType Type of assertion declaration.
     * @param assertion The assertion expression.
     */
    public MathAssertionDec(PosSymbol name, AssertionType assertionType,
            Exp assertion) {
        super(name.getLocation(), name);
        myAssertion = assertion;
        myAssertionType = assertionType;
        myTheoremSubtype = null;
    }

    /**
     * <p>This constructs either a mathematical theorem
     * assertion.</p>
     *
     * @param name Name of the assertion declaration.
     * @param theoremSubtype Theorem subtype properties.
     * @param assertion The assertion expression.
     */
    public MathAssertionDec(PosSymbol name, TheoremSubtype theoremSubtype,
            Exp assertion) {
        super(name.getLocation(), name);
        myAssertion = assertion;
        myAssertionType = AssertionType.THEOREM;
        myTheoremSubtype = theoremSubtype;
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

        sb.append(myAssertionType.toString());
        if (myTheoremSubtype != null) {
            sb.append(myTheoremSubtype.toString());
        }
        sb.append(myName.asString(0, innerIndentInc));
        sb.append(":\n");

        sb.append(myAssertion.asString(indentSize + innerIndentInc,
                innerIndentInc));
        sb.append(";");

        return sb.toString();
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
        if (!super.equals(o))
            return false;

        MathAssertionDec that = (MathAssertionDec) o;

        if (myAssertionType != that.myAssertionType)
            return false;
        if (!myAssertion.equals(that.myAssertion))
            return false;
        return myTheoremSubtype == that.myTheoremSubtype;
    }

    /**
     * <p>Returns the assertion expression for this math assertion declaration.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getAssertion() {
        return myAssertion;
    }

    /**
     * <p>Returns the assertion type for this math assertion declaration.</p>
     *
     * @return The {@link AssertionType} representation object.
     */
    public final AssertionType getAssertionType() {
        return myAssertionType;
    }

    /**
     * <p>Returns the specific subtype of the theorem represented by this
     * math assertion declaration.</p>
     *
     * @return The {@link TheoremSubtype} representation object.
     */
    public final TheoremSubtype getTheoremSubtype() {
        return myTheoremSubtype;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myAssertionType.hashCode();
        result = 31 * result + myAssertion.hashCode();
        result =
                31
                        * result
                        + (myTheoremSubtype != null ? myTheoremSubtype
                                .hashCode() : 0);
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final MathAssertionDec copy() {
        MathAssertionDec copyDec;
        if (myTheoremSubtype != null) {
            copyDec =
                    new MathAssertionDec(myName.clone(), myTheoremSubtype,
                            myAssertion.clone());
        }
        else {
            copyDec =
                    new MathAssertionDec(myName.clone(), myAssertionType,
                            myAssertion.clone());
        }

        return copyDec;
    }

}