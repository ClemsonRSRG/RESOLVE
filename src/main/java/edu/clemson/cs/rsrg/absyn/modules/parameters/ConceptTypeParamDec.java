/**
 * ConceptTypeParamDec.java
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
package edu.clemson.cs.rsrg.absyn.modules.parameters;

import edu.clemson.cs.rsrg.absyn.Dec;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the concept module type parameter
 * declarations that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class ConceptTypeParamDec extends Dec implements ModuleParameter {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a type representation that is passed as a parameter
     * to a module.</p>
     *
     * @param name A {@link PosSymbol} representing the type's name.
     */
    public ConceptTypeParamDec(PosSymbol name) {
        super(name.getLocation(), name);
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
        sb.append("ConceptTypeParamDec\n");
        sb.append(myName
                .asString(indentSize + innerIndentSize, innerIndentSize));

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link ConceptTypeParamDec} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final ConceptTypeParamDec clone() {
        return new ConceptTypeParamDec(myName.clone());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link ConceptTypeParamDec} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ConceptTypeParamDec) {
            ConceptTypeParamDec eAsConceptTypeParamDec =
                    (ConceptTypeParamDec) o;
            result = myLoc.equals(eAsConceptTypeParamDec.myLoc);

            if (result) {
                result = myName.equals(eAsConceptTypeParamDec.myName);
            }
        }

        return result;
    }

    /**
     * <p>Returns this object in string format.</p>
     *
     * @return This class as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Type ");
        sb.append(myName.toString());

        return sb.toString();
    }

}