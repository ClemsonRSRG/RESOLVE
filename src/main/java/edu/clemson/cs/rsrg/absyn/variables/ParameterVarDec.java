/**
 * ParameterVarDec.java
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
package edu.clemson.cs.rsrg.absyn.variables;

import edu.clemson.cs.r2jt.typeandpopulate2.entry.ProgramParameterEntry;
import edu.clemson.cs.rsrg.absyn.Ty;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the programming parameter variable declarations
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class ParameterVarDec extends AbstractVarDec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The object's mode representation.</p> */
    private ProgramParameterEntry.ParameterMode myMode;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a program parameter variable.</p>
     *
     * @param mode A {@link ProgramParameterEntry.ParameterMode} representing the parameter mode.
     * @param name A {@link PosSymbol} representing the variable's name.
     * @param ty A {@link Ty} representing the variable's raw type.
     */
    public ParameterVarDec(ProgramParameterEntry.ParameterMode mode,
            PosSymbol name, Ty ty) {
        super(name.getLocation(), name, ty);
        myMode = mode;
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
        printSpace(indentSize, sb);
        sb.append("ParameterVarDec\n");

        printSpace(indentSize + innerIndentSize, sb);
        sb.append(myMode.toString());
        sb.append(super.asString(indentSize, innerIndentSize));

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link ParameterVarDec} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final ParameterVarDec clone() {
        return new ParameterVarDec(myMode, myName.clone(), myTy.clone());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link ParameterVarDec} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ParameterVarDec) {
            ParameterVarDec eAsParameterVarDec = (ParameterVarDec) o;
            result = super.equals(eAsParameterVarDec);

            if (result) {
                result = myMode.equals(eAsParameterVarDec.myMode);
            }
        }

        return result;
    }

    /**
     * <p>Returns the mode of this parameter variable.</p>
     *
     * @return The mode in {@link ProgramParameterEntry.ParameterMode} format.
     */
    public ProgramParameterEntry.ParameterMode getMode() {
        return myMode;
    }

    /**
     * <p>Returns this object in string format.</p>
     *
     * @return This class as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(myMode);
        sb.append("\t");
        sb.append(super.toString());

        return sb.toString();
    }

}