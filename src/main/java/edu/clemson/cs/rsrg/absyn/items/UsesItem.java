/**
 * UsesItem.java
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
package edu.clemson.cs.rsrg.absyn.items;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class functions as a reference to the name of the
 * imported module used by a module the compiler is currently compiling.</p>
 *
 * @version 2.0
 */
public class UsesItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Name of this imported module.</p> */
    private final PosSymbol myName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Constructs an item that represents a imported module.</p>
     *
     * @param name Name of the imported module.
     */
    public UsesItem(PosSymbol name) {
        super(name.getLocation());
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
        sb.append("UsesItem\n");
        if (myName != null) {
            sb.append(myName.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link UsesItem} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public UsesItem clone() {
        return new UsesItem(myName.clone());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link UsesItem} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof UsesItem) {
            UsesItem usesItem = (UsesItem) o;
            result = myName.equals(usesItem.myName);
        }

        return result;
    }

    /**
     * <p>Returns the symbol representation
     * of this class.</p>
     *
     * @return A {@link PosSymbol} representation of the name.
     */
    public PosSymbol getName() {
        return myName;
    }

    /**
     * <p>Returns the symbol in string format.</p>
     *
     * @return Symbol as a string.
     */
    @Override
    public String toString() {
        return myName.toString();
    }

}