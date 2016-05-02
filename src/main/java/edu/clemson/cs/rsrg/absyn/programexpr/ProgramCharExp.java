/**
 * ProgramCharExp.java
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
package edu.clemson.cs.rsrg.absyn.programexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.Map;

/**
 * <p>This is the class for all the programming character expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class ProgramCharExp extends ProgramLiteralExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The character representing this programming character</p> */
    private final Character myCharacter;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a programming character expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param c A {@link Character} expression.
     */
    public ProgramCharExp(Location l, Character c) {
        super(l);
        myCharacter = c;
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
        sb.append("ProgramCharExp\n");

        if (myCharacter != null) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append(myCharacter.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link ProgramCharExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ProgramCharExp) {
            ProgramCharExp eAsProgramCharExp = (ProgramCharExp) o;
            result = myLoc.equals(eAsProgramCharExp.myLoc);

            if (result) {
                result = myCharacter.equals(eAsProgramCharExp.myCharacter);
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
        boolean retval = (e instanceof ProgramCharExp);
        if (retval) {
            ProgramCharExp eAsProgramCharExp = (ProgramCharExp) e;
            retval = myCharacter.equals(eAsProgramCharExp.myCharacter);
        }

        return retval;
    }

    /**
     * <p>This method returns a deep copy of the character value.</p>
     *
     * @return The {@link Character} value.
     */
    public Character getValue() {
        return myCharacter;
    }

    /**
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (myCharacter != null) {
            sb.append(myCharacter.toString());
        }

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
    protected Exp copy() {
        return new ProgramCharExp(new Location(myLoc), myCharacter.charValue());
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
        return new ProgramCharExp(new Location(myLoc), myCharacter.charValue());
    }

}