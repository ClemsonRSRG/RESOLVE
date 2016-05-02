/**
 * AffectsItem.java
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
package edu.clemson.cs.rsrg.absyn.items;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the affected variable expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class AffectsItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The qualifier for the affected variable.</p> */
    private PosSymbol myQualifier;

    /** <p>The name for the affected variable.</p> */
    private final PosSymbol myName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a variable expression that is affected
     * in a verification context.</p>
     *
     * @param qualifier A {@link PosSymbol} representing the variable's qualifier.
     * @param name A {@link PosSymbol} representing the variable's name.
     */
    public AffectsItem(PosSymbol qualifier, PosSymbol name) {
        super(name.getLocation());
        myQualifier = qualifier;
        myName = name;
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
        sb.append("AffectsItem\n");

        if (myQualifier != null) {
            sb.append(myQualifier.asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append("::");
        }

        sb.append(myName.asString(0, innerIndentSize));

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link AffectsItem} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public AffectsItem clone() {
        PosSymbol newQualifier = null;
        if (myQualifier != null) {
            newQualifier = myQualifier.clone();
        }

        return new AffectsItem(newQualifier, myName.clone());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link AffectsItem} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof AffectsItem) {
            AffectsItem affectsItem = (AffectsItem) o;
            result = myName.equals(affectsItem.myName);

            if (result && myQualifier != null) {
                result = myQualifier.equals(affectsItem.myQualifier);
            }
        }

        return result;
    }

    /**
     * <p>Returns the symbol representation of the variable
     * expression.</p>
     *
     * @return A {@link PosSymbol} representation of the name.
     */
    public PosSymbol getName() {
        return myName.clone();
    }

    /**
     * <p>Returns the symbol representation of the qualifier
     * expression.</p>
     *
     * @return A {@link PosSymbol} representation of the name.
     */
    public PosSymbol getQualifier() {
        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return qualifier;
    }

    /**
     * <p>Returns the symbol in string format.</p>
     *
     * @return Symbol as a string.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (myQualifier != null) {
            sb.append(myQualifier.toString());
            sb.append("::");
        }

        sb.append(myName.toString());

        return sb.toString();
    }

}