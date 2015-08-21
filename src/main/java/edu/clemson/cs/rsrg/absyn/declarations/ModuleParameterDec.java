/**
 * ModuleParameterDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations;

import edu.clemson.cs.rsrg.absyn.Dec;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is a wrapper class for all the different module parameter
 * declarations that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class ModuleParameterDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The actual module parameter declaration</p> */
    private final Dec myWrappedDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Given a declaration, we wrap it to indicate that this
     * is a module level parameter declaration.</p>
     *
     * @param dec The declaration to be wrapped.
     * @param <T> The type of declaration.
     */
    public <T extends Dec> ModuleParameterDec(T dec) {
        super(dec.getLocation(), dec.getName());
        myWrappedDec = dec;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method creates a special indented
     * text version of the class as a string.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentSize The additional indentation increment
     *                        for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public String asString(int indentSize, int innerIndentSize) {
        return myWrappedDec.asString(indentSize, innerIndentSize);
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link ModuleParameterDec} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public ModuleParameterDec clone() {
        return new ModuleParameterDec(myWrappedDec.clone());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {link @ModuleParameterDec} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ModuleParameterDec) {
            ModuleParameterDec moduleParameterDec = (ModuleParameterDec) o;
            result = myWrappedDec.equals(moduleParameterDec);
        }

        return result;
    }

    /**
     * <p>Returns the symbol representation
     * of this class.</p>
     *
     * @return A {link PosSymbol} representation of the name.
     */
    @Override
    public PosSymbol getName() {
        return myWrappedDec.getName();
    }

    /**
     * <p>Returns the inner wrapped declaration.</p>
     *
     * @return A {@link Dec} object.
     */
    public Dec getWrappedDec() {
        return myWrappedDec;
    }

}