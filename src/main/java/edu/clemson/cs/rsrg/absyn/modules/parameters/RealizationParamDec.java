/**
 * RealizationParamDec.java
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
package edu.clemson.cs.rsrg.absyn.modules.parameters;

import edu.clemson.cs.rsrg.absyn.Dec;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the realization parameter
 * declarations that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class RealizationParamDec extends Dec implements ModuleParameter {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The name of the concept.</p> */
    private final PosSymbol myConceptName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a type representation that is passed as a parameter
     * to a module.</p>
     *
     * @param name A {@link PosSymbol} representing the name of the realization.
     * @param conceptName A {@link PosSymbol} representing the name of the concept.
     */
    public RealizationParamDec(PosSymbol name, PosSymbol conceptName) {
        super(name.getLocation(), name);
        myConceptName = conceptName;
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
        sb.append("RealizationParamDec\n");
        sb.append(myName
                .asString(indentSize + innerIndentSize, innerIndentSize));
        sb.append(myConceptName.asString(indentSize + innerIndentSize,
                innerIndentSize));

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link RealizationParamDec} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final RealizationParamDec clone() {
        return new RealizationParamDec(myName.clone(), myConceptName.clone());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link RealizationParamDec} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof RealizationParamDec) {
            RealizationParamDec eAsRealizationParamDec =
                    (RealizationParamDec) o;
            result = myLoc.equals(eAsRealizationParamDec.myLoc);

            if (result) {
                result =
                        myName.equals(eAsRealizationParamDec.myName)
                                && myConceptName
                                        .equals(eAsRealizationParamDec.myConceptName);
            }
        }

        return result;
    }

    /**
     * <p>Returns the symbol representation of the concept name.</p>
     *
     * @return The concept name in {@link PosSymbol} format.
     */
    public PosSymbol getConceptName() {
        return myConceptName.clone();
    }

    /**
     * <p>Returns this object in string format.</p>
     *
     * @return This class as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(myName.toString());
        sb.append(" of ");
        sb.append(myConceptName);

        return sb.toString();
    }

}