/**
 * ProgramIntegerExp.java
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
package edu.clemson.cs.rsrg.absyn.programexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.Map;

/**
 * <p>This is the class for all the programming integer expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class ProgramIntegerExp extends ProgramLiteralExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The integer representing this programming integer</p> */
    private final Integer myInteger;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a programing integer expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param i A {@link Integer} expression.
     */
    public ProgramIntegerExp(Location l, int i) {
        super(l);
        myInteger = i;
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
        sb.append("ProgramIntegerExp\n");

        printSpace(indentSize + innerIndentSize, sb);
        sb.append(myInteger);
        sb.append("\n");

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link ProgramIntegerExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ProgramIntegerExp) {
            ProgramIntegerExp eAsProgramIntegerExp = (ProgramIntegerExp) o;
            result = myLoc.equals(eAsProgramIntegerExp.myLoc);

            if (result) {
                result = myInteger.equals(eAsProgramIntegerExp.myInteger);
            }
        }

        return result;
    }

    /**
     * <p>Shallow compare is too weak for many things, and equals() is too
     * strict. This method returns <code>true</code> <strong>iff</code> this
     * expression and the provided expression, <code>e</code>, are equivalent
     * with respect to structure and all function and variable names.</p>
     *
     * @param e The expression to compare this one to.
     *
     * @return True <strong>iff</strong> this expression and the provided
     *         expression are equivalent with respect to structure and all
     *         function and variable names.
     */
    @Override
    public boolean equivalent(Exp e) {
        boolean retval = e instanceof ProgramIntegerExp;
        if (retval) {
            ProgramIntegerExp eAsProgramIntegerExp = (ProgramIntegerExp) e;
            retval = myInteger.equals(eAsProgramIntegerExp.myInteger);
        }

        return retval;
    }

    /**
     * <p>This method returns a deep copy of the integer value.</p>
     *
     * @return The {@link Integer} value.
     */
    public int getValue() {
        return myInteger;
    }

    /**
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(myInteger);

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by this concrete subclass of {@link Exp} to manufacture
     * a copy of themselves.</p>
     *
     * @return A new {@link Exp} that is a deep copy of the original.
     */
    @Override
    public Exp copy() {
        return new ProgramIntegerExp(new Location(myLoc), myInteger);
    }

    /**
     * <p>Implemented by this concrete subclass of {@link Exp} to manufacture
     * a copy of themselves where all subexpressions have been appropriately
     * substituted. This class is assuming that <code>this</code>
     * does not match any key in <code>substitutions</code> and thus need only
     * concern itself with performing substitutions in its children.</p>
     *
     * @param substitutions A mapping from {@link Exp}s that should be
     *                      substituted out to the {@link Exp} that should
     *                      replace them.
     *
     * @return A new {@link Exp} that is a deep copy of the original with
     *         the provided substitutions made.
     */
    @Override
    protected Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new ProgramIntegerExp(new Location(myLoc), myInteger);
    }

}