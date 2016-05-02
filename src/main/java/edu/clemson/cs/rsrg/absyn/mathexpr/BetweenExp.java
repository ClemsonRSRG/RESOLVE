/**
 * BetweenExp.java
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
package edu.clemson.cs.rsrg.absyn.mathexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for a list of mathematical expressions that
 * are joined together by the "and" operator that the compiler builds
 * from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class BetweenExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expressions that compose this expression.</p> */
    private final List<Exp> myJoiningExps;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a between expression of the form
     * "[x] and [y] and ...".</p>
     *
     * @param l A {link Location} representation object.
     * @param joiningExps A list of {@link Exp} expressions.
     */
    public BetweenExp(Location l, List<Exp> joiningExps) {
        super(l);
        myJoiningExps = joiningExps;
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
        sb.append("BetweenExp\n");

        if (myJoiningExps != null) {
            for (Exp exp : myJoiningExps) {
                if (exp != null) {
                    sb.append(exp.asString(indentSize + innerIndentSize,
                            innerIndentSize));
                }
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
        if (myJoiningExps != null) {
            Iterator<Exp> i = myJoiningExps.iterator();
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
        Iterator<Exp> i = myJoiningExps.iterator();
        while (i.hasNext() && !found) {
            Exp temp = i.next();
            if (temp != null) {
                if (temp.containsVar(varName, IsOldExp)) {
                    found = true;
                }
            }
        }

        return found;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link BetweenExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof BetweenExp) {
            BetweenExp eAsBetweenExp = (BetweenExp) o;
            result = myLoc.equals(eAsBetweenExp.myLoc);

            if (result) {
                Iterator<Exp> thisJoiningExps = myJoiningExps.iterator();
                Iterator<Exp> eJoiningExps =
                        eAsBetweenExp.myJoiningExps.iterator();

                while (result && thisJoiningExps.hasNext()
                        && eJoiningExps.hasNext()) {
                    result &=
                            thisJoiningExps.next().equals(eJoiningExps.next());
                }

                //Both had better have run out at the same time
                result &=
                        (!thisJoiningExps.hasNext())
                                && (!eJoiningExps.hasNext());
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
        // I don't really understand what a "BetweenExp" is, so for now its
        // 'equivalent' implementation just checks to see if all subexpressions
        // exist as a subexpression in e.  -HwS
        boolean retval = (e instanceof BetweenExp);

        if (retval) {
            BetweenExp eAsBetweenExp = (BetweenExp) e;
            Iterator<Exp> eSubexpressions =
                    eAsBetweenExp.getSubExpressions().iterator();
            Iterator<Exp> mySubexpressions;
            Exp curExp;
            while (retval && eSubexpressions.hasNext()) {
                curExp = eSubexpressions.next();
                mySubexpressions = myJoiningExps.iterator();
                retval = false;
                while (!retval && mySubexpressions.hasNext()) {
                    retval = curExp.equivalent(mySubexpressions.next());
                }
            }
        }

        return retval;
    }

    /**
     * <p>This method returns a deep copy of the list of
     * joining subexpressions.</p>
     *
     * @return A list containing joining {@link Exp}s.
     */
    public List<Exp> getJoiningExps() {
        return copyExps();
    }

    /**
     * <p>This method returns a deep copy of the list of
     * subexpressions.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
     */
    @Override
    public List<Exp> getSubExpressions() {
        return copyExps();
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link BetweenExp} from applying the remember rule.
     */
    @Override
    public BetweenExp remember() {
        List<Exp> itemsCopy = new ArrayList<>();
        if (myJoiningExps != null) {
            for (Exp item : myJoiningExps) {
                itemsCopy.add(((MathExp) item).remember().clone());
            }
        }

        return new BetweenExp(new Location(myLoc), itemsCopy);
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*@Override
    public void setSubExpression(int index, Exp e) {
        myJoiningExps.set(index, e);
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
        Iterator<Exp> i = myJoiningExps.iterator();
        while (i.hasNext()) {
            sb.append(i.next().toString());
            if (i.hasNext()) {
                sb.append(" and ");
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
        return new BetweenExp(new Location(myLoc), copyExps());
    }

    /**
     * <p>Implemented by this concrete subclass of {link Exp} to manufacture
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
        List<Exp> newJoiningExps = new ArrayList<>();
        for (Exp e : myJoiningExps) {
            newJoiningExps.add(substitute(e, substitutions));
        }

        return new BetweenExp(new Location(myLoc), newJoiningExps);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list of between expressions.</p>
     *
     * @return A list containing {@link Exp}s.
     */
    private List<Exp> copyExps() {
        List<Exp> copyJoiningExps = new ArrayList<>();
        for (Exp exp : myJoiningExps) {
            copyJoiningExps.add(exp.clone());
        }

        return copyJoiningExps;
    }
}