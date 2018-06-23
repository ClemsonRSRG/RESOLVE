/*
 * DoubleExp.java
 * ---------------------------------
 * Copyright (c) 2018
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
import java.util.Map;

/**
 * <p>This is the class for all the mathematical double expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class DoubleExp extends LiteralExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The double representing this mathematical double</p> */
    private final double myDouble;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a mathematical double expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param d A double value.
     */
    public DoubleExp(Location l, double d) {
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

        DoubleExp doubleExp = (DoubleExp) o;

        return Double.compare(doubleExp.myDouble, myDouble) == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = (e instanceof DoubleExp);
        if (retval) {
            DoubleExp eAsDoubleExp = (DoubleExp) e;
            retval = (myDouble == eAsDoubleExp.myDouble);
        }

        return retval;
    }

    /**
     * <p>This method returns the double value.</p>
     *
     * @return The {@link double} value.
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
        return new DoubleExp(cloneLocation(), myDouble);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new DoubleExp(cloneLocation(), myDouble);
    }

}