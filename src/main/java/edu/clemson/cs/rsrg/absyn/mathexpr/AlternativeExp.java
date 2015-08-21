/**
 * AlternativeExp.java
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
import edu.clemson.cs.rsrg.parsing.data.Location;

import java.util.*;

/**
 * <p>This is the class for all the mathematical alternative expression
 * intermediate objects that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class AlternativeExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The alternatives member.</p> */
    private final List<AltItemExp> myAlternatives;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an alternative expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param alternatives A list of {@link AltItemExp} expressions.
     */
    public AlternativeExp(Location l, List<AltItemExp> alternatives) {
        super(l);
        myAlternatives = alternatives;

        boolean foundOtherwise = false;
        for (AltItemExp e : alternatives) {
            foundOtherwise = foundOtherwise || (e.getTest() == null);
        }
        if (!foundOtherwise) {
            throw new IllegalArgumentException("Must have otherwise.");
        }
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
        sb.append("AlternativeExp\n");

        if (myAlternatives != null) {
            Iterator<AltItemExp> i = myAlternatives.iterator();
            while (i.hasNext()) {
                AltItemExp temp = i.next();
                if (temp != null) {
                    sb.append(temp.asString(indentSize + innerIndentSize,
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
        if (myAlternatives != null) {
            Iterator<AltItemExp> i = myAlternatives.iterator();
            while (i.hasNext() && !found) {
                AltItemExp temp = i.next();
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
        Iterator<AltItemExp> i = myAlternatives.iterator();
        while (i.hasNext() && !found) {
            AltItemExp temp = i.next();
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
     * for the {@link AlternativeExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof AlternativeExp) {
            AlternativeExp eAsAlternativeExp = (AlternativeExp) o;
            result = myLoc.equals(eAsAlternativeExp.myLoc);

            if (result) {
                Iterator<AltItemExp> thisAltItems = myAlternatives.iterator();
                Iterator<AltItemExp> eAltItems =
                        eAsAlternativeExp.myAlternatives.iterator();

                while (result && thisAltItems.hasNext() && eAltItems.hasNext()) {
                    result &= thisAltItems.next().equals(eAltItems.next());
                }

                //Both had better have run out at the same time
                result &= (!thisAltItems.hasNext()) && (!eAltItems.hasNext());
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
        boolean result = e instanceof AlternativeExp;

        if (result) {
            AlternativeExp eAsAlternativeExp = (AlternativeExp) e;

            Iterator<AltItemExp> thisAltItems = myAlternatives.iterator();
            Iterator<AltItemExp> eAltItems =
                    eAsAlternativeExp.myAlternatives.iterator();

            while (result && thisAltItems.hasNext() && eAltItems.hasNext()) {
                result &= thisAltItems.next().equivalent(eAltItems.next());
            }

            //Both had better have run out at the same time
            result &= (!thisAltItems.hasNext()) && (!eAltItems.hasNext());
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the
     * list of alternative expressions.</p>
     *
     * @return A list containing {@link AltItemExp} type objects.
     */
    public List<AltItemExp> getAlternatives() {
        List<AltItemExp> copyAlternatives = new ArrayList<>();
        Iterator<AltItemExp> altIt = myAlternatives.iterator();
        while (altIt.hasNext()) {
            AltItemExp copyItem = (AltItemExp) altIt.next().clone();
            copyAlternatives.add(copyItem);
        }

        return copyAlternatives;
    }

    /**
     * <p>This method returns a deep copy of the list of
     * subexpressions.</p>
     *
     * @return A list containing {@link Exp} type objects.
     */
    @Override
    public List<Exp> getSubExpressions() {
        List<Exp> list = new ArrayList<>();
        Iterator<AltItemExp> altIt = myAlternatives.iterator();
        while (altIt.hasNext()) {
            list.add(altIt.next().clone());
        }

        return list;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link AlternativeExp} from applying the remember rule.
     */
    @Override
    public AlternativeExp remember() {
        List<AltItemExp> itemsCopy = new ArrayList<>();
        for (AltItemExp item : myAlternatives) {
            itemsCopy.add(item.remember());
        }

        return new AlternativeExp(new Location(myLoc), itemsCopy);
    }

    /**
     *  <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*@Override
    public void setSubExpression(int index, Exp e) {
        myAlternatives.set(index, (AltItemExp) e);
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
        sb.append("{{");
        Iterator<AltItemExp> it = myAlternatives.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
            sb.append("\n");

        }
        sb.append("}}");

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
        Location newLoc = new Location(myLoc);

        List<AltItemExp> newAlternatives = new ArrayList<>();
        Iterator<AltItemExp> it = myAlternatives.iterator();
        while (it.hasNext()) {
            newAlternatives.add((AltItemExp) it.next().clone());
        }

        return new AlternativeExp(newLoc, newAlternatives);
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
        List<AltItemExp> newAlternatives = new ArrayList<>();
        for (Exp e : myAlternatives) {
            newAlternatives.add((AltItemExp) substitute(e, substitutions));
        }

        return new AlternativeExp(myLoc, newAlternatives);
    }

}