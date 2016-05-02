/**
 * AbstractVarDec.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.variables;

import edu.clemson.cs.rsrg.absyn.Dec;
import edu.clemson.cs.rsrg.absyn.Ty;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the abstract base class for both the mathematical and
 * programming variable declarations that the compiler builds from
 * the ANTLR4 AST tree.</p>
 *
 * @version 1.0
 */
public abstract class AbstractVarDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The object's raw type representation.</p> */
    protected final Ty myTy;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>A helper constructor that allow us to store the location,
     * the name and the raw type of the created object directly in
     * this class.</p>
     *
     * @param l A {@link Location} representation object.
     */
    protected AbstractVarDec(Location l, PosSymbol name, Ty ty) {
        super(l, name);
        myTy = ty;
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
        StringBuffer sb = new StringBuffer();
        sb.append(myName
                .asString(indentSize + innerIndentSize, innerIndentSize));
        sb.append(myTy.asString(indentSize + innerIndentSize, innerIndentSize));

        return sb.toString();
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link AbstractVarDec} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof AbstractVarDec) {
            AbstractVarDec eAsAbstractVarDec = (AbstractVarDec) o;
            result = myLoc.equals(eAsAbstractVarDec.myLoc);

            if (result) {
                result = myName.equals(eAsAbstractVarDec.myName);

                if (result) {
                    result = myTy.equals(eAsAbstractVarDec.myTy);
                }
            }
        }

        return result;
    }

    /**
     * <p>Returns the raw type representation
     * of this class.</p>
     *
     * @return The raw type in {@link Ty} format.
     */
    public final Ty getTy() {
        return myTy.clone();
    }

    /**
     * <p>Returns this object in string format.</p>
     *
     * @return This class as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append("\t");
        sb.append(myTy.toString());

        return sb.toString();
    }

}