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
package edu.clemson.cs.rsrg.absyn.items.programitems;

import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;

/**
 * <p>This is the class for all the facility declaration arguments
 * for Concept/Enhancement modules that the compiler builds using
 * the ANTLR4 AST nodes.</p>
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
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        sb.append(myArgumentExp.asString(0, innerIndentInc));

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ModuleArgumentItem clone() {
        ModuleArgumentItem newItem =
                new ModuleArgumentItem(myArgumentExp.clone());
        newItem.setProgramTypeValue(myTypeValue);

        return newItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ModuleArgumentItem that = (ModuleArgumentItem) o;

        if (myTypeValue != null ? !myTypeValue.equals(that.myTypeValue)
                : that.myTypeValue != null)
            return false;
        return myArgumentExp.equals(that.myArgumentExp);

    }

    /**
     * <p>Returns the variable expression being passed to the
     * facility declaration.</p>
     *
     * @return A {@link ProgramExp} representation of the expression.
     */
    public final ProgramExp getArgumentExp() {
        return myArgumentExp;
    }

    /**
     * <p>Returns the variable expression's program type.</p>
     *
     * @return A {@link PTType} type representation.
     */
    public final PTType getProgramTypeValue() {
        return myTypeValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myTypeValue != null ? myTypeValue.hashCode() : 0;
        result = 31 * result + myArgumentExp.hashCode();
        return result;
    }

    /**
     * <p>Sets the variable expression's program type.</p>
     *
     * @param type A {@link PTType} type representation.
     */
    public final void setProgramTypeValue(PTType type) {
        myTypeValue = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        return myArgumentExp.toString();
    }

}