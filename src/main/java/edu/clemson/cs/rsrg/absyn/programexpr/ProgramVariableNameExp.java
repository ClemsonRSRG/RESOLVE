/**
 * ProgramVariableNameExp.java
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
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.Map;

/**
 * <p>This is the class for all the programming named variable expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class ProgramVariableNameExp extends ProgramVariableExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The variable name</p> */
    private final PosSymbol myVarName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a programming function call expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qual A {@link PosSymbol} representing the expression's qualifier.
     * @param name A {@link PosSymbol} representing the expression's name.
     */
    public ProgramVariableNameExp(Location l, PosSymbol qual, PosSymbol name) {
        super(l, qual);
        myVarName = name;
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
        sb.append("ProgramVariableNameExp\n");

        if (getQualifier() != null) {
            sb.append(getQualifier().asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append("::");
        }

        if (myVarName != null) {
            sb.append(myVarName.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link ProgramVariableNameExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ProgramVariableNameExp) {
            ProgramVariableNameExp eAsProgramVariableNameExp =
                    (ProgramVariableNameExp) o;
            result = myLoc.equals(eAsProgramVariableNameExp.myLoc);

            if (result) {
                result =
                        posSymbolEquivalent(getQualifier(),
                                eAsProgramVariableNameExp.getQualifier())
                                && posSymbolEquivalent(myVarName,
                                        eAsProgramVariableNameExp.myVarName);
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
        boolean retval = e instanceof ProgramVariableNameExp;

        if (retval) {
            ProgramVariableNameExp eAsProgramVariableNameExp =
                    (ProgramVariableNameExp) e;

            retval =
                    posSymbolEquivalent(getQualifier(),
                            eAsProgramVariableNameExp.getQualifier())
                            && posSymbolEquivalent(myVarName,
                                    eAsProgramVariableNameExp.myVarName);
        }

        return retval;
    }

    /**
     * <p>This method returns a deep copy of the variable name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public PosSymbol getName() {
        return myVarName.clone();
    }

    /**
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (getQualifier() != null) {
            sb.append(getQualifier().toString());
            sb.append("::");
        }

        if (myVarName != null) {
            sb.append(myVarName.toString());
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
        PosSymbol newQualifier = null;
        if (getQualifier() != null) {
            newQualifier = getQualifier().clone();
        }

        return new ProgramVariableNameExp(new Location(myLoc), newQualifier,
                myVarName.clone());
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
        PosSymbol newQualifier = null;
        if (getQualifier() != null) {
            newQualifier = getQualifier().clone();
        }

        return new ProgramVariableNameExp(new Location(myLoc), newQualifier,
                myVarName.clone());
    }

}