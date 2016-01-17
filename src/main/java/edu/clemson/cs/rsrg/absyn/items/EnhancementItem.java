/**
 * EnhancementItem.java
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the facility declaration arguments
 * for Enhancement extension modules that the compiler builds from
 * the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class EnhancementItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Name of this imported module.</p> */
    private final PosSymbol myName;

    /** <p>List of parameters arguments for this enhancement extension.</p> */
    private final List<ModuleArgumentItem> myParams;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a enhancement extension argument for facility declarations.</p>
     *
     * @param name Name of the extended enhancement module.
     * @param params The parameter arguments that are passed to instantiate this enhancement.
     */
    public EnhancementItem(PosSymbol name, List<ModuleArgumentItem> params) {
        super(name.getLocation());
        myName = name;
        myParams = params;
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
        sb.append("EnhancementItem\n");
        sb.append(myName
                .asString(indentSize + innerIndentSize, innerIndentSize));

        for (ModuleArgumentItem m : myParams) {
            sb
                    .append(m.asString(indentSize + innerIndentSize,
                            innerIndentSize));
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link EnhancementItem} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public EnhancementItem clone() {
        return new EnhancementItem(myName.clone(), copyArgs());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link EnhancementItem} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof EnhancementItem) {
            EnhancementItem enhancementItem = (EnhancementItem) o;
            result = myName.equals(enhancementItem.myName);

            if (result) {
                if (myParams != null && enhancementItem.myParams != null) {
                    Iterator<ModuleArgumentItem> thisParams =
                            myParams.iterator();
                    Iterator<ModuleArgumentItem> eParams =
                            enhancementItem.myParams.iterator();

                    while (result && thisParams.hasNext() && eParams.hasNext()) {
                        result &= thisParams.next().equals(eParams.next());
                    }

                    //Both had better have run out at the same time
                    result &= (!thisParams.hasNext()) && (!eParams.hasNext());
                }
            }
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
        return myName.clone();
    }

    /**
     * <p>Returns the list of arguments for this enhancement
     * extension.</p>
     *
     * @return A list of {@link ModuleArgumentItem} representation objects.
     */
    public List<ModuleArgumentItem> getParams() {
        return copyArgs();
    }

    /**
     * <p>Returns the symbol in string format.</p>
     *
     * @return Symbol as a string.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(myName.toString());
        sb.append("(");

        Iterator<ModuleArgumentItem> it = myParams.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());

            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the parameter arguments.</p>
     *
     * @return A list containing {@link ModuleArgumentItem}s.
     */
    private List<ModuleArgumentItem> copyArgs() {
        List<ModuleArgumentItem> copyArgs = new ArrayList<>();
        for (ModuleArgumentItem m : myParams) {
            copyArgs.add(m.clone());
        }

        return copyArgs;
    }
}