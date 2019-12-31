/*
 * UnaryMinusExp.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This is the class for all the mathematical unary minus expression objects
 * that the compiler
 * builds using the ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class UnaryMinusExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The mathematical expression that is being applied "unary minus".
     * </p>
     */
    private final Exp myInnerArgumentExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a unary minus expression.
     * </p>
     *
     * @param l A {@link Location} representation object.
     * @param exp An {@link Exp} that represents the actual expression.
     */
    public UnaryMinusExp(Location l, Exp exp) {
        super(l);
        myInnerArgumentExp = exp;
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

        if (myInnerArgumentExp != null) {
            sb.append(myInnerArgumentExp.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        boolean found = false;
        if (myInnerArgumentExp != null) {
            found = myInnerArgumentExp.containsExp(exp);
        }

        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = false;
        if (myInnerArgumentExp != null) {
            found = myInnerArgumentExp.containsVar(varName, IsOldExp);
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

        UnaryMinusExp that = (UnaryMinusExp) o;

        return myInnerArgumentExp.equals(that.myInnerArgumentExp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = (e instanceof UnaryMinusExp);
        if (retval) {
            UnaryMinusExp eAsUnaryMinusExp = (UnaryMinusExp) e;
            retval = myInnerArgumentExp
                    .equivalent(eAsUnaryMinusExp.myInnerArgumentExp);
        }

        return retval;
    }

    /**
     * <p>
     * Returns the expression's inner argument expression.
     * </p>
     *
     * @return The assignment {@link Exp} object.
     */
    public final Exp getArgument() {
        return myInnerArgumentExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> list = new ArrayList<>();
        list.add(myInnerArgumentExp);

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myInnerArgumentExp.hashCode();
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
        return new UnaryMinusExp(cloneLocation(), myInnerArgumentExp.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new UnaryMinusExp(cloneLocation(),
                substitute(myInnerArgumentExp, substitutions));
    }

}
