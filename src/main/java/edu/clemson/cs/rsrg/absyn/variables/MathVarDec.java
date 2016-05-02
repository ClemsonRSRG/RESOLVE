/**
 * MathVarDec.java
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

import edu.clemson.cs.rsrg.absyn.Ty;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the mathematical variable declarations
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class MathVarDec extends AbstractVarDec {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a mathematical variable.</p>
     *
     * @param name A {@link PosSymbol} representing the variable's name.
     * @param ty A {@link Ty} representing the variable's raw type.
     */
    public MathVarDec(PosSymbol name, Ty ty) {
        super(name.getLocation(), name, ty);
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
    public final String asString(int indentSize, int innerIndentSize) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("MathVarDec\n");
        sb.append(super.asString(indentSize, innerIndentSize));

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link MathVarDec} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final MathVarDec clone() {
        return new MathVarDec(myName.clone(), myTy.clone());
    }

}