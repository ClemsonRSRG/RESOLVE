/*
 * StringExp.java
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
import java.util.Map;

/**
 * <p>
 * This is the class for all the mathematical string expression objects that the compiler builds using the ANTLR4 AST
 * nodes.
 * </p>
 *
 * @version 2.0
 */
public class StringExp extends LiteralExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The inner representation for this mathematical string
     * </p>
     */
    private final String myString;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a mathematical string expression.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param s
     *            A {@link String} expression.
     */
    public StringExp(Location l, String s) {
        super(l);
        myString = s;
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
        sb.append(myString);

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

        StringExp stringExp = (StringExp) o;

        return myString.equals(stringExp.myString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = (e instanceof StringExp);
        if (retval) {
            StringExp eAsStringExp = (StringExp) e;
            retval = myString.equals(eAsStringExp.myString);
        }

        return retval;
    }

    /**
     * <p>
     * This method returns the string value.
     * </p>
     *
     * @return The {@link String} value.
     */
    public final String getValue() {
        return myString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myString.hashCode();
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
        return new StringExp(cloneLocation(), myString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new StringExp(cloneLocation(), myString);
    }

}
