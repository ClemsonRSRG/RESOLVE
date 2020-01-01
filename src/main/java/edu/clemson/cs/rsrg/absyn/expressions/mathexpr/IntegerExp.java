/*
 * IntegerExp.java
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
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.Map;

/**
 * <p>
 * This is the class for all the mathematical integer expression objects that
 * the compiler builds
 * using the ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class IntegerExp extends LiteralExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The qualifier for this mathematical integer
     * </p>
     */
    private PosSymbol myQualifier;

    /**
     * <p>
     * The integer representing this mathematical integer
     * </p>
     */
    private final Integer myInteger;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a mathematical integer expression.
     * </p>
     *
     * @param l A {@link Location} representation object.
     * @param qualifier A {@link PosSymbol} representation object.
     * @param i A {@link Integer} expression.
     */
    public IntegerExp(Location l, PosSymbol qualifier, int i) {
        super(l);
        myQualifier = qualifier;
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
        if (myQualifier != null) {
            sb.append(myQualifier.asString(0, innerIndentInc));
            sb.append("::");
        }
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

        IntegerExp that = (IntegerExp) o;

        if (myQualifier != null ? !myQualifier.equals(that.myQualifier)
                : that.myQualifier != null)
            return false;
        return myInteger.equals(that.myInteger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = e instanceof IntegerExp;
        if (retval) {
            IntegerExp eAsIntegerExp = (IntegerExp) e;
            retval = myInteger.equals(eAsIntegerExp.myInteger);
        }

        return retval;
    }

    /**
     * <p>
     * This method returns the qualifier name.
     * </p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public final PosSymbol getQualifier() {
        return myQualifier;
    }

    /**
     * <p>
     * This method returns the integer value.
     * </p>
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
        result = 31 * result
                + (myQualifier != null ? myQualifier.hashCode() : 0);
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
        PosSymbol newQualifier = null;
        if (myQualifier != null) {
            newQualifier = myQualifier.clone();
        }

        return new IntegerExp(cloneLocation(), newQualifier, myInteger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        PosSymbol newQualifier = null;
        if (myQualifier != null) {
            newQualifier = myQualifier.clone();
        }

        return new IntegerExp(cloneLocation(), newQualifier, myInteger);
    }

}
