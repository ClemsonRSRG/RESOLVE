/*
 * ProgramIntegerExp.java
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
package edu.clemson.cs.rsrg.absyn.expressions.programexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.Map;

/**
 * <p>This is the class for all the programming integer expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class ProgramIntegerExp extends ProgramLiteralExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The integer representing this programming integer</p> */
    private final Integer myInteger;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a programing integer expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param i A {@link Integer} expression.
     */
    public ProgramIntegerExp(Location l, int i) {
        super(l);
        myInteger = i;
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
        sb.append(myInteger);

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

        ProgramIntegerExp that = (ProgramIntegerExp) o;

        return myInteger.equals(that.myInteger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = e instanceof ProgramIntegerExp;
        if (retval) {
            ProgramIntegerExp eAsProgramIntegerExp = (ProgramIntegerExp) e;
            retval = myInteger.equals(eAsProgramIntegerExp.myInteger);
        }

        return retval;
    }

    /**
     * <p>This method returns the integer value.</p>
     *
     * @return The {@link Integer} value.
     */
    public final int getValue() {
        return myInteger;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myInteger.hashCode();
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
        return new ProgramIntegerExp(cloneLocation(), myInteger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new ProgramIntegerExp(cloneLocation(), myInteger);
    }

}