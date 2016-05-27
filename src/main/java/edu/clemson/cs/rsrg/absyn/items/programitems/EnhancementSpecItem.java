/**
 * EnhancementSpecItem.java
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

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the facility declaration arguments
 * for Enhancement extension modules that the compiler builds using
 * the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class EnhancementSpecItem extends ResolveConceptualElement {

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
    public EnhancementSpecItem(PosSymbol name, List<ModuleArgumentItem> params) {
        super(name.getLocation());
        myName = name;
        myParams = params;
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
        printSpace(indentSize, sb);
        sb.append("enhanced by ");
        sb.append(myName.asString(0, innerIndentInc));

        sb.append("( ");
        Iterator<ModuleArgumentItem> it = myParams.iterator();
        while (it.hasNext()) {
            ModuleArgumentItem m = it.next();
            sb.append(m.asString(0, innerIndentInc));

            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(" )\n");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final EnhancementSpecItem clone() {
        return new EnhancementSpecItem(myName.clone(), copyArgs());
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

        EnhancementSpecItem that = (EnhancementSpecItem) o;

        if (!myName.equals(that.myName))
            return false;
        return myParams.equals(that.myParams);

    }

    /**
     * <p>Returns the symbol representation of the enhancement extension.</p>
     *
     * @return A {@link PosSymbol} representation of the name.
     */
    public final PosSymbol getName() {
        return myName;
    }

    /**
     * <p>Returns the list of arguments for this enhancement
     * extension.</p>
     *
     * @return A list of {@link ModuleArgumentItem} representation objects.
     */
    public final List<ModuleArgumentItem> getParams() {
        return myParams;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myName.hashCode();
        result = 31 * result + myParams.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("enhanced by ");
        sb.append(myName.toString());

        sb.append("( ");
        Iterator<ModuleArgumentItem> it = myParams.iterator();
        while (it.hasNext()) {
            ModuleArgumentItem m = it.next();
            sb.append(m.toString());

            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(" )\n");

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