/*
 * ArbitraryExpTy.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.absyn.rawtypes;

import edu.clemson.rsrg.absyn.expressions.Exp;

/**
 * <p>
 * This is the class for all the raw arbitrary type objects that the compiler builds using the ANTLR4 AST nodes.
 * </p>
 *
 * <p>
 * A syntactic type based on an arbitrary mathematical {@link Exp}. All math expressions should have this {@link Ty}.
 * Ultimately their interfaces should be changed to reflect this fact, or this class should be unwrapped and math types
 * should simply be represented by {@link Exp}s.
 * </p>
 *
 * @version 2.0
 */
public class ArbitraryExpTy extends Ty {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The inner arbitrary expression.
     * </p>
     */
    private final Exp myArbitraryExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs an arbitrary raw type that contains an expression.
     * </p>
     *
     * @param arbitraryExp
     *            An {@link Exp} expression.
     */
    public ArbitraryExpTy(Exp arbitraryExp) {
        super(arbitraryExp.getLocation());
        myArbitraryExp = arbitraryExp;
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
        sb.append(myArbitraryExp.asString(0, innerIndentInc));

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

        ArbitraryExpTy that = (ArbitraryExpTy) o;

        return myArbitraryExp.equals(that.myArbitraryExp);

    }

    /**
     * <p>
     * Returns this raw type's inner expression.
     * </p>
     *
     * @return The inner (arbitrary) {@link Exp} representation object.
     */
    public final Exp getArbitraryExp() {
        return myArbitraryExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myArbitraryExp.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Ty copy() {
        return new ArbitraryExpTy(myArbitraryExp.clone());
    }

}
