/*
 * RecpExp.java
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This is the class represents a named {@code Receptacle} that the compiler builds using the ANTLR4 AST nodes.
 * </p>
 *
 * <p>
 * As an example a receptacle: {@code recp.S} indicate that it is the {@code Receptacle} for the variable {@code S}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class RecpExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The expression's type represented as an {@link Exp}.
     * </p>
     */
    private final Exp myVarAsExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     *
     * @param l
     *            A {@link Location} representation object.
     * @param exp
     *            An {@link Exp} that represents the inner variable expression.
     */
    public RecpExp(Location l, Exp exp) {
        super(l);
        myVarAsExp = exp;
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
        sb.append("recp.");
        sb.append(myVarAsExp.asString(0, innerIndentInc));

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        return myVarAsExp.equivalent(exp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        return false;
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

        RecpExp that = (RecpExp) o;

        return myVarAsExp.equals(that.myVarAsExp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = false;
        if (e instanceof RecpExp) {
            RecpExp eAsRecpExp = (RecpExp) e;
            retval = (myVarAsExp.equivalent(eAsRecpExp.myVarAsExp));
        }

        return retval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        return Collections.singletonList(myVarAsExp.clone());
    }

    /**
     * <p>
     * This method returns the variable expression.
     * </p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getVarExp() {
        return myVarAsExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myVarAsExp.hashCode();
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
        return new RecpExp(cloneLocation(), myVarAsExp.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new RecpExp(cloneLocation(), substitute(myVarAsExp, substitutions));
    }

}
