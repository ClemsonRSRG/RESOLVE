/*
 * EnhancementSpecRealizItem.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.absyn.items.programitems;

import edu.clemson.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * This is the class for all the facility declaration arguments for Enhancement and its associated Realization extension
 * modules that the compiler builds using the ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class EnhancementSpecRealizItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Name of this imported module.
     * </p>
     */
    private final PosSymbol myEnhancementName;

    /**
     * <p>
     * List of parameters arguments for this enhancement extension.
     * </p>
     */
    private final List<ModuleArgumentItem> myEnhancementParams;

    /**
     * <p>
     * Profile name for this imported module.
     * </p>
     */
    private final PosSymbol myProfileName;

    /**
     * <p>
     * Name of the implementation module.
     * </p>
     */
    private final PosSymbol myEnhancementRealizName;

    /**
     * <p>
     * List of parameters arguments for the enhancement implementation.
     * </p>
     */
    private final List<ModuleArgumentItem> myEnhancementRealizParams;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a enhancement and realization extension argument for facility declarations.
     * </p>
     *
     * @param name
     *            Name of the extended enhancement module.
     * @param params
     *            The parameter arguments that are passed to instantiate this enhancement.
     * @param bodyName
     *            Name of the enhancement implementation module.
     * @param bodyParams
     *            The parameter arguments that are passed to instantiate the enhancement implementation.
     * @param profileName
     *            Performance profile name for the extended enhancement module.
     */
    public EnhancementSpecRealizItem(PosSymbol name, List<ModuleArgumentItem> params, PosSymbol bodyName,
            List<ModuleArgumentItem> bodyParams, PosSymbol profileName) {
        super(name.getLocation());
        myEnhancementName = name;
        myEnhancementParams = params;
        myEnhancementRealizName = bodyName;
        myEnhancementRealizParams = bodyParams;
        myProfileName = profileName;
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
        sb.append(myEnhancementName.asString(0, innerIndentInc));
        sb.append(formArgumentString(myEnhancementParams));
        sb.append("\n");

        printSpace(indentSize + innerIndentInc, sb);
        sb.append("realized by ");
        sb.append(myEnhancementRealizName.asString(0, innerIndentInc));
        sb.append("(");
        sb.append(formArgumentString(myEnhancementRealizParams));

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final EnhancementSpecRealizItem clone() {
        PosSymbol profileName = null;
        if (myProfileName != null) {
            profileName = myProfileName.clone();
        }

        return new EnhancementSpecRealizItem(myEnhancementName.clone(), copyEnhArgs(), myEnhancementRealizName.clone(),
                copyEnhRealizArgs(), profileName);
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

        EnhancementSpecRealizItem that = (EnhancementSpecRealizItem) o;

        if (!myEnhancementName.equals(that.myEnhancementName))
            return false;
        if (!myEnhancementParams.equals(that.myEnhancementParams))
            return false;
        if (myProfileName != null ? !myProfileName.equals(that.myProfileName) : that.myProfileName != null)
            return false;
        if (!myEnhancementRealizName.equals(that.myEnhancementRealizName))
            return false;
        return myEnhancementRealizParams.equals(that.myEnhancementRealizParams);

    }

    /**
     * <p>
     * Returns the symbol representation of the enhancement extension.
     * </p>
     *
     * @return A {@link PosSymbol} representation of the enhancement name.
     */
    public final PosSymbol getEnhancementName() {
        return myEnhancementName;
    }

    /**
     * <p>
     * Returns the list of arguments for this enhancement extension.
     * </p>
     *
     * @return A list of {@link ModuleArgumentItem} representation objects.
     */
    public final List<ModuleArgumentItem> getEnhancementParams() {
        return myEnhancementParams;
    }

    /**
     * <p>
     * Returns the symbol representation of the associated realization extension.
     * </p>
     *
     * @return A {@link PosSymbol} representation of the enhancement realization name.
     */
    public final PosSymbol getEnhancementRealizName() {
        return myEnhancementRealizName;
    }

    /**
     * <p>
     * Returns the list of arguments of arguments for the associated realization extension.
     * </p>
     *
     * @return A list of {@link ModuleArgumentItem} representation objects.
     */
    public final List<ModuleArgumentItem> getEnhancementRealizParams() {
        return myEnhancementRealizParams;
    }

    /**
     * <p>
     * Returns the symbol representation for the performance profile name.
     * </p>
     *
     * @return A {@link PosSymbol} representation of the performance profile name.
     */
    public final PosSymbol getProfileName() {
        return myProfileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myEnhancementName.hashCode();
        result = 31 * result + myEnhancementParams.hashCode();
        result = 31 * result + (myProfileName != null ? myProfileName.hashCode() : 0);
        result = 31 * result + myEnhancementRealizName.hashCode();
        result = 31 * result + myEnhancementRealizParams.hashCode();
        return result;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This is a helper method that makes a copy of the list containing all the enhancement parameter arguments.
     * </p>
     *
     * @return A list containing {@link ModuleArgumentItem}s.
     */
    private List<ModuleArgumentItem> copyEnhArgs() {
        List<ModuleArgumentItem> copyArgs = new ArrayList<>();
        for (ModuleArgumentItem m : myEnhancementParams) {
            copyArgs.add(m.clone());
        }

        return copyArgs;
    }

    /**
     * <p>
     * This is a helper method that makes a copy of the list containing all the enhancement realization parameter
     * arguments.
     * </p>
     *
     * @return A list containing {@link ModuleArgumentItem}s.
     */
    private List<ModuleArgumentItem> copyEnhRealizArgs() {
        List<ModuleArgumentItem> copyArgs = new ArrayList<>();
        for (ModuleArgumentItem m : myEnhancementRealizParams) {
            copyArgs.add(m.clone());
        }

        return copyArgs;
    }

    /**
     * <p>
     * An helper method to generate a string for the argument list.
     * </p>
     *
     * @param argumentItems
     *            A list containing {@link ModuleArgumentItem}s.
     *
     * @return String containing all module argument strings.
     */
    private String formArgumentString(List<ModuleArgumentItem> argumentItems) {
        StringBuffer sb = new StringBuffer();

        if (argumentItems.size() > 0) {
            sb.append("(");
            Iterator<ModuleArgumentItem> cArgIt = argumentItems.iterator();
            while (cArgIt.hasNext()) {
                sb.append(cArgIt.next().asString(0, 0));

                if (cArgIt.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(")");
        }

        return sb.toString();
    }
}
