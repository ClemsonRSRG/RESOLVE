/*
 * BetweenExp.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.absyn.expressions.mathexpr;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This is the class for a list of mathematical expression objects that are joined together by the "and" operator that
 * the compiler builds using the ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class BetweenExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The expressions that compose this expression.
     * </p>
     */
    private final List<Exp> myJoiningExps;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a between expression of the form "[x] and [y] and ...".
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param joiningExps
     *            A list of {@link Exp} expressions.
     */
    public BetweenExp(Location l, List<Exp> joiningExps) {
        super(l);
        myJoiningExps = joiningExps;
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
        Iterator<Exp> it = myJoiningExps.iterator();
        while (it.hasNext()) {
            sb.append(it.next().asString(0, innerIndentInc));

            if (it.hasNext()) {
                sb.append(" and ");
            }
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
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
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
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

        BetweenExp that = (BetweenExp) o;

        return myJoiningExps.equals(that.myJoiningExps);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        // I don't really understand what a "BetweenExp" is, so for now its
        // 'equivalent' implementation just checks to see if all subexpressions
        // exist as a subexpression in e. -HwS
        boolean retval = (e instanceof BetweenExp);

        if (retval) {
            BetweenExp eAsBetweenExp = (BetweenExp) e;
            Iterator<Exp> eSubexpressions = eAsBetweenExp.getSubExpressions().iterator();
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
     * <p>
     * This method returns a deep copy of the list of joining sub-expressions.
     * </p>
     *
     * @return A list containing joining {@link Exp}s.
     */
    public final List<Exp> getJoiningExps() {
        return myJoiningExps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        return copyExps();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myJoiningExps.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        return new BetweenExp(cloneLocation(), copyExps());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        List<Exp> newJoiningExps = new ArrayList<>();
        for (Exp e : myJoiningExps) {
            newJoiningExps.add(substitute(e, substitutions));
        }

        return new BetweenExp(cloneLocation(), newJoiningExps);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This is a helper method that makes a copy of the list of between expressions.
     * </p>
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
