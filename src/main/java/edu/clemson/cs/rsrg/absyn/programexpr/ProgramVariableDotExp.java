/**
 * ProgramVariableDotExp.java
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
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the programming dotted expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class ProgramVariableDotExp extends ProgramVariableExp {

    /// ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's collection of inner expressions.</p> */
    private final List<ProgramVariableExp> mySegmentExps;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a dotted expression to keep track
     * of all the inner expressions.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qual A {@link PosSymbol} representing the expression's qualifier.
     * @param segments A list of {@link Exp} object.
     */
    public ProgramVariableDotExp(Location l, PosSymbol qual,
            List<ProgramVariableExp> segments) {
        super(l, qual);
        mySegmentExps = segments;
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
        sb.append("ProgramVariableDotExp\n");

        if (getQualifier() != null) {
            sb.append(getQualifier().asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append("::");
        }

        if (mySegmentExps != null) {
            for (ProgramVariableExp e : mySegmentExps) {
                sb.append(e.asString(indentSize + innerIndentSize,
                        innerIndentSize));
            }
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link ProgramVariableDotExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ProgramVariableDotExp) {
            ProgramVariableDotExp eAsProgramVariableDotExp =
                    (ProgramVariableDotExp) o;
            result = myLoc.equals(eAsProgramVariableDotExp.myLoc);

            if (result) {
                result =
                        posSymbolEquivalent(getQualifier(),
                                eAsProgramVariableDotExp.getQualifier());

                if (mySegmentExps != null
                        && eAsProgramVariableDotExp.mySegmentExps != null) {
                    Iterator<ProgramVariableExp> thisSegmentExps =
                            mySegmentExps.iterator();
                    Iterator<ProgramVariableExp> eSegmentExps =
                            eAsProgramVariableDotExp.mySegmentExps.iterator();

                    while (result && thisSegmentExps.hasNext()
                            && eSegmentExps.hasNext()) {
                        result &=
                                thisSegmentExps.next().equals(
                                        eSegmentExps.next());
                    }

                    //Both had better have run out at the same time
                    result &=
                            (!thisSegmentExps.hasNext())
                                    && (!eSegmentExps.hasNext());
                }
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
        boolean retval = e instanceof ProgramVariableDotExp;

        if (retval) {
            ProgramVariableDotExp eAsProgramVariableDotExp =
                    (ProgramVariableDotExp) e;

            retval =
                    posSymbolEquivalent(getQualifier(),
                            eAsProgramVariableDotExp.getQualifier());

            if (mySegmentExps != null
                    && eAsProgramVariableDotExp.mySegmentExps != null) {
                Iterator<ProgramVariableExp> thisSegmentExps =
                        mySegmentExps.iterator();
                Iterator<ProgramVariableExp> eSegmentExps =
                        eAsProgramVariableDotExp.mySegmentExps.iterator();

                while (retval && thisSegmentExps.hasNext()
                        && eSegmentExps.hasNext()) {
                    retval &=
                            thisSegmentExps.next().equivalent(
                                    eSegmentExps.next());
                }

                //Both had better have run out at the same time
                retval &=
                        (!thisSegmentExps.hasNext())
                                && (!eSegmentExps.hasNext());
            }
        }

        return retval;
    }

    /**
     * <p>This method returns a deep copy of all the inner expressions.</p>
     *
     * @return A list containing all the segmented {@link Exp}s.
     */
    public List<Exp> getSegments() {
        List<Exp> copySegmentExps = new ArrayList<>();
        for (ProgramExp exp : mySegmentExps) {
            copySegmentExps.add(exp.clone());
        }

        return copySegmentExps;
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

        if (mySegmentExps != null) {
            Iterator<ProgramVariableExp> i = mySegmentExps.iterator();

            while (i.hasNext()) {
                sb.append(i.next().toString());
                if (i.hasNext()) {
                    sb.append(".");
                }
            }
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

        return new ProgramVariableDotExp(new Location(myLoc), newQualifier,
                copyExps());
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

        List<ProgramVariableExp> newSegments = new ArrayList<>();
        for (ProgramVariableExp e : mySegmentExps) {
            newSegments.add((ProgramVariableExp) substitute(e, substitutions));
        }

        return new ProgramVariableDotExp(new Location(myLoc), newQualifier, newSegments);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the segment expressions.</p>
     *
     * @return A list containing {@link Exp}s.
     */
    private List<ProgramVariableExp> copyExps() {
        List<ProgramVariableExp> copyJoiningExps = new ArrayList<>();
        for (ProgramVariableExp exp : mySegmentExps) {
            copyJoiningExps.add((ProgramVariableExp) exp.clone());
        }

        return copyJoiningExps;
    }
}