/**
 * DotExp.java
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
package edu.clemson.cs.rsrg.absyn.mathexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical dotted expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class DotExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's collection of inner expressions.</p> */
    private final List<Exp> mySegmentExps;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a dotted expression to keep track
     * of all the inner expressions.</p>
     *
     * @param l A {@link Location} representation object.
     * @param segments A list of {@link Exp} object.
     */
    public DotExp(Location l, List<Exp> segments) {
        super(l);
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
        sb.append("DotExp\n");

        if (mySegmentExps != null) {
            for (Exp e : mySegmentExps) {
                sb.append(e.asString(indentSize + innerIndentSize,
                        innerIndentSize));
            }
        }

        return sb.toString();
    }

    /**
     * <p>This method attempts to find the provided expression in our
     * subexpressions.</p>
     *
     * @param exp The expression we wish to locate.
     *
     * @return True if there is an instance of <code>exp</code>
     * within this object's subexpressions. False otherwise.
     */
    @Override
    public boolean containsExp(Exp exp) {
        boolean found = false;
        if (mySegmentExps != null) {
            Iterator<Exp> i = mySegmentExps.iterator();
            while (i.hasNext() && !found) {
                Exp temp = i.next();
                if (temp != null) {
                    if (temp.containsExp(exp)) {
                        found = true;
                    }
                }
            }
        }

        return found;
    }

    /**
     *  <p>This method attempts to find an expression with the given name in our
     * subexpressions.</p>
     *
     * @param varName Expression name.
     * @param IsOldExp Flag to indicate if the given name is of the form
     *                 "#[varName]"
     *
     * @return True if there is a {@link Exp} within this object's
     * subexpressions that matches <code>varName</code>. False otherwise.
     */
    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = false;
        if (mySegmentExps != null) {
            Iterator<Exp> i = mySegmentExps.iterator();
            while (i.hasNext() && !found) {
                Exp temp = i.next();
                if (temp != null) {
                    if (temp.containsVar(varName, IsOldExp)) {
                        found = true;
                    }
                }
            }
        }

        return found;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link DotExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof DotExp) {
            DotExp eAsDotExp = (DotExp) o;
            result = myLoc.equals(eAsDotExp.myLoc);

            if (result) {
                if (mySegmentExps != null && eAsDotExp.mySegmentExps != null) {
                    Iterator<Exp> thisSegmentExps = mySegmentExps.iterator();
                    Iterator<Exp> eSegmentExps =
                            eAsDotExp.mySegmentExps.iterator();

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
        boolean result = (e instanceof DotExp);

        if (result) {
            DotExp eAsDotExp = (DotExp) e;

            if (mySegmentExps != null && eAsDotExp.mySegmentExps != null) {
                Iterator<Exp> thisSegmentExps = mySegmentExps.iterator();
                Iterator<Exp> eSegmentExps = eAsDotExp.mySegmentExps.iterator();
                while (result && thisSegmentExps.hasNext()
                        && eSegmentExps.hasNext()) {

                    result &=
                            thisSegmentExps.next().equivalent(
                                    eSegmentExps.next());
                }

                //Both had better have run out at the same time
                result &=
                        (!thisSegmentExps.hasNext())
                                && (!eSegmentExps.hasNext());
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of all the inner expressions.</p>
     *
     * @return A list containing all the segmented {@link Exp}s.
     */
    public List<Exp> getSegments() {
        return copyExps();
    }

    /**
     * <p>This method method returns a deep copy of the list of
     * subexpressions. This method will return the same result
     * as calling the {@link DotExp#getSegments()} method.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
     */
    @Override
    public List<Exp> getSubExpressions() {
        return getSegments();
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link DotExp} from applying the remember rule.
     */
    @Override
    public DotExp remember() {
        List<Exp> newSegmentExps = new ArrayList<>();
        for (Exp e : mySegmentExps) {
            Exp copyExp;
            if (e instanceof OldExp) {
                copyExp = ((OldExp) e).getExp();
            }
            else if (e instanceof MathExp){
                copyExp = ((MathExp) e).remember();
            }
            else {
                throw new MiscErrorException("We encountered an expression of the type " +
                        e.getClass().getName(),
                        new InvalidClassException(""));
            }

            newSegmentExps.add(copyExp);
        }

        return new DotExp(new Location(myLoc), newSegmentExps);
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*public void setSubExpression(int index, Exp e) {
        segments.set(index, e);
    }*/

    /**
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link MathExp} from applying the simplification step.
     */
    @Override
    public MathExp simplify() {
        return this.clone();
    }

    /**
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (mySegmentExps != null) {
            Iterator<Exp> i = mySegmentExps.iterator();

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
        return new DotExp(new Location(myLoc), copyExps());
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
    public Exp substituteChildren(Map<Exp, Exp> substitutions) {
        List<Exp> newSegments = new ArrayList<>();
        for (Exp e : mySegmentExps) {
            newSegments.add(substitute(e, substitutions));
        }

        return new DotExp(new Location(myLoc), newSegments);
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
    private List<Exp> copyExps() {
        List<Exp> copyJoiningExps = new ArrayList<>();
        for (Exp exp : mySegmentExps) {
            copyJoiningExps.add(exp.clone());
        }

        return copyJoiningExps;
    }
}