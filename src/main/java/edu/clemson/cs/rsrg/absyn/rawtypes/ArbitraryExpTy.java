/**
 * ArbitraryExpTy.java
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
package edu.clemson.cs.rsrg.absyn.rawtypes;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.absyn.Ty;

/**
 * <p>This is the class for all the raw arbitrary types
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * <p>A syntactic type based on an arbitrary mathematical {@link Exp}.  All
 * math expressions should have this {@link Ty}. Ultimately their
 * interfaces should be changed to reflect this fact, or this class should
 * be unwrapped and math types should simply be represented by 
 * {@link Exp}s.</p>
 *
 * @version 2.0
 */
public class ArbitraryExpTy extends Ty {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The inner arbitrary expression.</p> */
    private final Exp myArbitraryExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an arbitrary raw type that contains
     * an expression.</p>
     *
     * @param arbitraryExp An {@link Exp} expression.
     */
    public ArbitraryExpTy(Exp arbitraryExp) {
        super(arbitraryExp.getLocation());
        myArbitraryExp = arbitraryExp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method creates a special indented
     * text version of the class as a string.</p>
     *
     * @param indentSize        The base indentation to the first line
     *                          of the text.
     * @param innerIndentSize   The additional indentation increment
     *                          for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public String asString(int indentSize, int innerIndentSize) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("ArbitraryTy\n");

        if (myArbitraryExp != null) {
            sb.append(myArbitraryExp.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link ArbitraryExpTy} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ArbitraryExpTy) {
            ArbitraryExpTy eAsArbitraryExpTy = (ArbitraryExpTy) o;
            result = myArbitraryExp.equals(eAsArbitraryExpTy.myArbitraryExp);
        }

        return result;
    }

    /**
     * <p>Returns a deep copy of this raw type's inner expression.</p>
     *
     * @return The inner (arbitrary) {@link Exp} representation object.
     */
    public Exp getArbitraryExp() {
        return myArbitraryExp;
    }

    /**
     * <p>Returns the raw type in string format.</p>
     *
     * @return Raw type as a string.
     */
    @Override
    public String toString() {
        return myArbitraryExp.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by this concrete subclass of {@link Ty} to manufacture
     * a copy of themselves.</p>
     *
     * @return A new {@link Ty} that is a deep copy of the original.
     */
    @Override
    protected Ty copy() {
        Exp newArbitraryExp = null;
        if (myArbitraryExp != null) {
            newArbitraryExp = myArbitraryExp.clone();
        }

        return new ArbitraryExpTy(newArbitraryExp);
    }

}