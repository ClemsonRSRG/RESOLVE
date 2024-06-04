/*
 * ProgramDoubleExp.java
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
package edu.clemson.rsrg.absyn.expressions.programexpr;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.parsing.data.Location;
import java.util.Map;

/**
 * <p>
 * This is the class for all the programming double expression objects that the compiler builds using the ANTLR4 AST
 * nodes.
 * </p>
 *
 * @version 2.0
 */
public class ProgramDoubleExp extends ProgramLiteralExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The double representing this programming double
     * </p>
     */
    private final double myDouble;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a programming double expression.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param d
     *            A double value.
     */
    public ProgramDoubleExp(Location l, double d) {
        super(l);
        myDouble = d;
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
        sb.append(myDouble);

        return sb.toString();
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

        ProgramDoubleExp that = (ProgramDoubleExp) o;

        return Double.compare(that.myDouble, myDouble) == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = (e instanceof ProgramDoubleExp);
        if (retval) {
            ProgramDoubleExp eAsProgramDoubleExp = (ProgramDoubleExp) e;
            retval = (myDouble == eAsProgramDoubleExp.myDouble);
        }

        return retval;
    }

    /**
     * <p>
     * This method returns the double value.
     * </p>
     *
     * @return The double value.
     */
    public final double getValue() {
        return myDouble;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(myDouble);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
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
        return new ProgramDoubleExp(cloneLocation(), myDouble);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new ProgramDoubleExp(cloneLocation(), myDouble);
    }

}
