/**
 * ModuleArgumentItem.java
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

import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.programexpr.ProgramExp;

/**
 * <p>This is the class for all the facility declaration arguments
 * for Concept/Enhancement modules that the compiler builds from
 * the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class ModuleArgumentItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>If this argument names a program type, this will be set by the 
     * populator to point to the correct type.</p>
     */
    private PTType myTypeValue;

    /** <p>The argument expression in this module argument.</p> */
    private final ProgramExp myArgumentExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a module argument for facility declarations.</p>
     *
     * @param evalExp A {@link ProgramExp} representing the argument being passed.
     */
    public ModuleArgumentItem(ProgramExp evalExp) {
        super(evalExp.getLocation());
        myArgumentExp = evalExp;
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
        sb.append("ModuleArgumentItem\n");
        sb.append(myArgumentExp.asString(indentSize + innerIndentSize,
                innerIndentSize));

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link ModuleArgumentItem} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public ModuleArgumentItem clone() {
        ModuleArgumentItem newItem =
                new ModuleArgumentItem(myArgumentExp.clone());
        newItem.setProgramTypeValue(myTypeValue);

        return newItem;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link ModuleArgumentItem} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ModuleArgumentItem) {
            ModuleArgumentItem moduleArgumentItem = (ModuleArgumentItem) o;
            result = myArgumentExp.equals(moduleArgumentItem.myArgumentExp);

            if (result) {
                if (myTypeValue != null
                        && moduleArgumentItem.myTypeValue != null) {
                    result = myTypeValue.equals(moduleArgumentItem.myTypeValue);
                }
                else {
                    result = false;
                }
            }
        }

        return result;
    }

    /**
     * <p>Returns the variable expression being passed to the
     * facility declaration.</p>
     *
     * @return A {@link ProgramExp} representation of the expression.
     */
    public ProgramExp getArgumentExp() {
        return myArgumentExp.clone();
    }

    /**
     * <p>Returns the variable expression's program type.</p>
     *
     * @return A {@link PTType} type representation.
     */
    public PTType getProgramTypeValue() {
        return myTypeValue;
    }

    /**
     * <p>Sets the variable expression's program type.</p>
     *
     * @param type A {@link PTType} type representation.
     */
    public void setProgramTypeValue(PTType type) {
        myTypeValue = type;
    }

    /**
     * <p>Returns the symbol in string format.</p>
     *
     * @return Symbol as a string.
     */
    @Override
    public String toString() {
        return myArgumentExp.toString();
    }

}