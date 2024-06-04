/*
 * FacilityDec.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.absyn.declarations.facilitydecl;

import edu.clemson.rsrg.absyn.declarations.Dec;
import edu.clemson.rsrg.absyn.items.programitems.EnhancementSpecItem;
import edu.clemson.rsrg.absyn.items.programitems.EnhancementSpecRealizItem;
import edu.clemson.rsrg.absyn.items.programitems.ModuleArgumentItem;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * This is the class for all the facility declaration objects that the compiler builds using the ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class FacilityDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The concept name of this facility declaration.
     * </p>
     */
    private final PosSymbol myConceptName;

    /**
     * <p>
     * The arguments to instantiate the concept.
     * </p>
     */
    private final List<ModuleArgumentItem> myConceptParams;

    /**
     * <p>
     * The enhancements that are implemented by the concept realization directly.
     * </p>
     */
    private final List<EnhancementSpecItem> myEnhancements;

    /**
     * <p>
     * The concept realization name of this facility declaration.
     * </p>
     */
    private final PosSymbol myConceptRealizName;

    /**
     * <p>
     * The arguments to instantiate the concept realization.
     * </p>
     */
    private final List<ModuleArgumentItem> myConceptRealizParams;

    /**
     * <p>
     * The enhancement and their associated realizations instantiated by the facility declaration.
     * </p>
     */
    private final List<EnhancementSpecRealizItem> myEnhancementRealizPairs;

    /**
     * <p>
     * Profile name for this facility.
     * </p>
     */
    private final PosSymbol myProfileName;

    /**
     * <p>
     * Tells us whether or not the facility's realization has a implementation written in Resolve. If it does, then this
     * flag should be true, otherwise, it will be false.
     * </p>
     */
    private final boolean myExternallyRealizedFlag;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a facility declaration with "false" as the external realization option.
     * </p>
     *
     * @param name
     *            Name of the facility declaration.
     * @param conceptName
     *            Name of the concept module.
     * @param conceptParams
     *            The parameter arguments that are passed to instantiate the concept.
     * @param enhancements
     *            List of all enhancements that are implemented by the concept realization directly.
     * @param bodyName
     *            Name of the concept realization module.
     * @param bodyParams
     *            The parameter arguments that are passed to instantiate the concept realization.
     * @param enhancementBodies
     *            List of all enhancement/realization pairs that are instantiated by the facility.
     * @param profileName
     *            The name of the performance profile in use.
     */
    public FacilityDec(PosSymbol name, PosSymbol conceptName, List<ModuleArgumentItem> conceptParams,
            List<EnhancementSpecItem> enhancements, PosSymbol bodyName, List<ModuleArgumentItem> bodyParams,
            List<EnhancementSpecRealizItem> enhancementBodies, PosSymbol profileName) {
        this(name, conceptName, conceptParams, enhancements, bodyName, bodyParams, enhancementBodies, profileName,
                false);
    }

    /**
     * <p>
     * This constructs a facility declaration the passed in the external realization option.
     * </p>
     *
     * @param name
     *            Name of the facility declaration.
     * @param conceptName
     *            Name of the concept module.
     * @param conceptParams
     *            The parameter arguments that are passed to instantiate the concept.
     * @param enhancements
     *            List of all enhancements that are implemented by the concept realization directly.
     * @param bodyName
     *            Name of the concept realization module.
     * @param bodyParams
     *            The parameter arguments that are passed to instantiate the concept realization.
     * @param enhancementBodies
     *            List of all enhancement/realization pairs that are instantiated by the facility.
     * @param profileName
     *            The name of the performance profile in use.
     * @param externRealized
     *            Boolean option to indicate if this facility has been externally realized or not.
     */
    public FacilityDec(PosSymbol name, PosSymbol conceptName, List<ModuleArgumentItem> conceptParams,
            List<EnhancementSpecItem> enhancements, PosSymbol bodyName, List<ModuleArgumentItem> bodyParams,
            List<EnhancementSpecRealizItem> enhancementBodies, PosSymbol profileName, boolean externRealized) {
        super(name.getLocation(), name);
        myConceptName = conceptName;
        myConceptParams = conceptParams;
        myConceptRealizName = bodyName;
        myConceptRealizParams = bodyParams;
        myEnhancements = enhancements;
        myEnhancementRealizPairs = enhancementBodies;
        myProfileName = profileName;
        myExternallyRealizedFlag = externRealized;
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
        sb.append("Facility ");
        sb.append(myName.asString(0, innerIndentInc));
        sb.append(" is ");

        sb.append(myConceptName.asString(0, innerIndentInc));
        sb.append(formArgumentString(myConceptParams));
        sb.append("\n");

        for (EnhancementSpecItem eItem : myEnhancements) {
            sb.append(eItem.asString(indentSize + innerIndentInc, innerIndentInc));
            sb.append("\n");
        }

        printSpace(indentSize + innerIndentInc, sb);
        if (myExternallyRealizedFlag) {
            sb.append("externally ");
        }
        sb.append("realized by ");
        sb.append(myConceptRealizName.asString(0, innerIndentInc));
        sb.append(formArgumentString(myConceptRealizParams));

        for (EnhancementSpecRealizItem bodyItem : myEnhancementRealizPairs) {
            sb.append("\n");
            sb.append(bodyItem.asString(indentSize + innerIndentInc, innerIndentInc));
        }
        sb.append(";");

        return sb.toString();
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
        if (!super.equals(o))
            return false;

        FacilityDec that = (FacilityDec) o;

        if (myExternallyRealizedFlag != that.myExternallyRealizedFlag)
            return false;
        if (!myConceptName.equals(that.myConceptName))
            return false;
        if (!myConceptParams.equals(that.myConceptParams))
            return false;
        if (!myEnhancements.equals(that.myEnhancements))
            return false;
        if (!myConceptRealizName.equals(that.myConceptRealizName))
            return false;
        if (!myConceptRealizParams.equals(that.myConceptRealizParams))
            return false;
        if (!myEnhancementRealizPairs.equals(that.myEnhancementRealizPairs))
            return false;
        return myProfileName != null ? myProfileName.equals(that.myProfileName) : that.myProfileName == null;
    }

    /**
     * <p>
     * Returns the symbol representation for the concept name.
     * </p>
     *
     * @return A {@link PosSymbol} representation of the concept name.
     */
    public final PosSymbol getConceptName() {
        return myConceptName;
    }

    /**
     * <p>
     * Returns the list of arguments for this concept extension.
     * </p>
     *
     * @return A list of {@link ModuleArgumentItem} representation objects.
     */
    public final List<ModuleArgumentItem> getConceptParams() {
        return myConceptParams;
    }

    /**
     * <p>
     * Returns the symbol representation for the concept realization name.
     * </p>
     *
     * @return A {@link PosSymbol} representation of the enhancement realization name.
     */
    public final PosSymbol getConceptRealizName() {
        return myConceptRealizName;
    }

    /**
     * <p>
     * Returns the list of arguments for the enhancement realization.
     * </p>
     *
     * @return A list of {@link ModuleArgumentItem} representation objects.
     */
    public final List<ModuleArgumentItem> getConceptRealizParams() {
        return myConceptRealizParams;
    }

    /**
     * <p>
     * Returns the list of enhancement extensions implemented by the concept realization.
     * </p>
     *
     * @return A list of {@link EnhancementSpecItem} representation objects.
     */
    public final List<EnhancementSpecItem> getEnhancements() {
        return myEnhancements;
    }

    /**
     * <p>
     * Returns the list of enhancement/realizations in this facility declaration.
     * </p>
     *
     * @return A list of {@link EnhancementSpecRealizItem} representation objects.
     */
    public final List<EnhancementSpecRealizItem> getEnhancementRealizPairs() {
        return myEnhancementRealizPairs;
    }

    /**
     * <p>
     * Checks to see if the concept realization is implemented in RESOLVE.
     * </p>
     *
     * @return {@code true} if the realization is externally realized, {@code false} otherwise.
     */
    public final boolean getExternallyRealizedFlag() {
        return myExternallyRealizedFlag;
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
        int result = super.hashCode();
        result = 31 * result + myConceptName.hashCode();
        result = 31 * result + myConceptParams.hashCode();
        result = 31 * result + myEnhancements.hashCode();
        result = 31 * result + myConceptRealizName.hashCode();
        result = 31 * result + myConceptRealizParams.hashCode();
        result = 31 * result + myEnhancementRealizPairs.hashCode();
        result = 31 * result + (myProfileName != null ? myProfileName.hashCode() : 0);
        result = 31 * result + (myExternallyRealizedFlag ? 1 : 0);
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final FacilityDec copy() {
        PosSymbol newProfileName = null;
        if (myProfileName != null) {
            newProfileName = myProfileName.clone();
        }

        return new FacilityDec(myName.clone(), myConceptName.clone(), copyConceptArgs(), copyEnhItems(),
                myConceptRealizName.clone(), copyConceptRealizArgs(), copyEnhBodyItems(), newProfileName,
                myExternallyRealizedFlag);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This is a helper method that makes a copy of the list containing all the concept parameter arguments.
     * </p>
     *
     * @return A list containing {@link ModuleArgumentItem}s.
     */
    private List<ModuleArgumentItem> copyConceptArgs() {
        List<ModuleArgumentItem> copyArgs = new ArrayList<>();
        for (ModuleArgumentItem m : myConceptParams) {
            copyArgs.add(m.clone());
        }

        return copyArgs;
    }

    /**
     * <p>
     * This is a helper method that makes a copy of the list containing all the concept realization parameter arguments.
     * </p>
     *
     * @return A list containing {@link ModuleArgumentItem}s.
     */
    private List<ModuleArgumentItem> copyConceptRealizArgs() {
        List<ModuleArgumentItem> copyArgs = new ArrayList<>();
        for (ModuleArgumentItem m : myConceptRealizParams) {
            copyArgs.add(m.clone());
        }

        return copyArgs;
    }

    /**
     * <p>
     * This is a helper method that makes a copy of the list containing all the enhancement realization items.
     * </p>
     *
     * @return A list containing {@link EnhancementSpecRealizItem}s.
     */
    private List<EnhancementSpecRealizItem> copyEnhBodyItems() {
        List<EnhancementSpecRealizItem> copyArgs = new ArrayList<>();
        for (EnhancementSpecRealizItem eb : myEnhancementRealizPairs) {
            copyArgs.add(eb.clone());
        }

        return copyArgs;
    }

    /**
     * <p>
     * This is a helper method that makes a copy of the list containing all the enhancement items.
     * </p>
     *
     * @return A list containing {@link EnhancementSpecItem}s.
     */
    private List<EnhancementSpecItem> copyEnhItems() {
        List<EnhancementSpecItem> copyArgs = new ArrayList<>();
        for (EnhancementSpecItem e : myEnhancements) {
            copyArgs.add(e.clone());
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
