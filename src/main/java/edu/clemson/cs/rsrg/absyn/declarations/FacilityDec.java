/**
 * FacilityDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations;

import edu.clemson.cs.rsrg.absyn.Dec;
import edu.clemson.cs.rsrg.absyn.items.EnhancementBodyItem;
import edu.clemson.cs.rsrg.absyn.items.EnhancementItem;
import edu.clemson.cs.rsrg.absyn.items.ModuleArgumentItem;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the facility declarations
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class FacilityDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The concept name of this facility declaration.</p> */
    private final PosSymbol myConceptName;

    /** <p>The arguments to instantiate the concept.</p> */
    private final List<ModuleArgumentItem> myConceptParams;

    /**
     * <p>The enhancements that are implemented by the concept
     * realization directly.</p>
     */
    private final List<EnhancementItem> myEnhancements;

    /** <p>The concept realization name of this facility declaration.</p> */
    private final PosSymbol myConceptRealizName;

    /** <p>The arguments to instantiate the concept realization.</p> */
    private final List<ModuleArgumentItem> myConceptRealizParams;

    /**
     * <p>The enhancement and their associated realizations instantiated
     * by the facility declaration.</p>
     */
    private final List<EnhancementBodyItem> myEnhancementRealizPairs;

    /** <p>Profile name for this facility.</p> */
    private PosSymbol myProfileName;

    /**
     * <p>Tells us whether or not the facility's realization
     * has a implementation written in Resolve. If it does,
     * then this flag should be true, otherwise, it will be
     * false.</p>
     */
    private final boolean myExternallyRealizedFlag;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a facility declaration with "false"
     * as the external realization option.</p>
     *
     * @param name Name of the facility declaration.
     * @param conceptName Name of the concept module.
     * @param conceptParams The parameter arguments that are passed to
     *                      instantiate the concept.
     * @param enhancements List of all enhancements that are implemented
     *                     by the concept realization directly.
     * @param bodyName Name of the concept realization module.
     * @param bodyParams The parameter arguments that are passed to
     *                   instantiate the concept realization.
     * @param enhancementBodies List of all enhancement/realization pairs
     *                          that are instantiated by the facility.
     * @param profileName The name of the performance profile in use.
     */
    public FacilityDec(PosSymbol name, PosSymbol conceptName,
            List<ModuleArgumentItem> conceptParams,
            List<EnhancementItem> enhancements, PosSymbol bodyName,
            List<ModuleArgumentItem> bodyParams,
            List<EnhancementBodyItem> enhancementBodies, PosSymbol profileName) {
        this(name, conceptName, conceptParams, enhancements, bodyName,
                bodyParams, enhancementBodies, profileName, false);
    }

    /**
     * <p>This constructs a facility declaration the passed in
     * the external realization option.</p>
     *
     * @param name Name of the facility declaration.
     * @param conceptName Name of the concept module.
     * @param conceptParams The parameter arguments that are passed to
     *                      instantiate the concept.
     * @param enhancements List of all enhancements that are implemented
     *                     by the concept realization directly.
     * @param bodyName Name of the concept realization module.
     * @param bodyParams The parameter arguments that are passed to
     *                   instantiate the concept realization.
     * @param enhancementBodies List of all enhancement/realization pairs
     *                          that are instantiated by the facility.
     * @param profileName The name of the performance profile in use.
     * @param externRealized Boolean option to indicate if this facility
     *                       has been externally realized or not.
     */
    public FacilityDec(PosSymbol name, PosSymbol conceptName,
            List<ModuleArgumentItem> conceptParams,
            List<EnhancementItem> enhancements, PosSymbol bodyName,
            List<ModuleArgumentItem> bodyParams,
            List<EnhancementBodyItem> enhancementBodies, PosSymbol profileName,
            boolean externRealized) {
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
        sb.append("FacilityDec\n");

        printSpace(indentSize, sb);
        sb.append("Facility ");
        sb.append(myName.asString(0, innerIndentSize));
        sb.append(" is ");

        sb.append(myConceptName.asString(0, 0));
        sb.append("(");
        Iterator<ModuleArgumentItem> cArgIt = myConceptParams.iterator();
        while (cArgIt.hasNext()) {
            sb.append(cArgIt.next().asString(0, 0));

            if (cArgIt.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");

        for (EnhancementItem eItem : myEnhancements) {
            sb.append("\n");
            sb.append(eItem.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        sb.append("\n");
        printSpace(indentSize + innerIndentSize + innerIndentSize, sb);
        sb.append(myConceptRealizName.asString(0, 0));
        sb.append("(");
        Iterator<ModuleArgumentItem> cRealizArgIt =
                myConceptRealizParams.iterator();
        while (cRealizArgIt.hasNext()) {
            sb.append(cRealizArgIt.next().asString(0, 0));

            if (cRealizArgIt.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");

        for (EnhancementBodyItem bodyItem : myEnhancementRealizPairs) {
            sb.append("\n");
            sb.append(bodyItem.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }
        sb.append(";");

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link FacilityDec} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public FacilityDec clone() {
        return new FacilityDec(myName.clone(), getConceptName(),
                getConceptParams(), getEnhancements(), getConceptRealizName(),
                getConceptRealizParams(), getEnhancementRealizPairs(),
                getProfileName(), myExternallyRealizedFlag);
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link FacilityDec} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof FacilityDec) {
            FacilityDec facilityDec = (FacilityDec) o;
            result =
                    myName.equals(facilityDec.myName)
                            && myConceptName.equals(facilityDec.myConceptName)
                            && myConceptRealizName
                                    .equals(facilityDec.myConceptRealizName);

            if (result) {
                if (myConceptParams != null
                        && facilityDec.myConceptParams != null) {
                    Iterator<ModuleArgumentItem> thisParams =
                            myConceptParams.iterator();
                    Iterator<ModuleArgumentItem> eParams =
                            facilityDec.myConceptParams.iterator();

                    while (result && thisParams.hasNext() && eParams.hasNext()) {
                        result &= thisParams.next().equals(eParams.next());
                    }

                    //Both had better have run out at the same time
                    result &= (!thisParams.hasNext()) && (!eParams.hasNext());
                }
            }

            if (result) {
                if (myConceptRealizParams != null
                        && facilityDec.myConceptRealizParams != null) {
                    Iterator<ModuleArgumentItem> thisParams =
                            myConceptRealizParams.iterator();
                    Iterator<ModuleArgumentItem> eParams =
                            facilityDec.myConceptRealizParams.iterator();

                    while (result && thisParams.hasNext() && eParams.hasNext()) {
                        result &= thisParams.next().equals(eParams.next());
                    }

                    //Both had better have run out at the same time
                    result &= (!thisParams.hasNext()) && (!eParams.hasNext());
                }
            }

            if (result) {
                if (myEnhancements != null
                        && facilityDec.myEnhancements != null) {
                    Iterator<EnhancementItem> thisParams =
                            myEnhancements.iterator();
                    Iterator<EnhancementItem> eParams =
                            facilityDec.myEnhancements.iterator();

                    while (result && thisParams.hasNext() && eParams.hasNext()) {
                        result &= thisParams.next().equals(eParams.next());
                    }

                    //Both had better have run out at the same time
                    result &= (!thisParams.hasNext()) && (!eParams.hasNext());
                }
            }

            if (result) {
                if (myEnhancementRealizPairs != null
                        && facilityDec.myEnhancementRealizPairs != null) {
                    Iterator<EnhancementBodyItem> thisParams =
                            myEnhancementRealizPairs.iterator();
                    Iterator<EnhancementBodyItem> eParams =
                            facilityDec.myEnhancementRealizPairs.iterator();

                    while (result && thisParams.hasNext() && eParams.hasNext()) {
                        result &= thisParams.next().equals(eParams.next());
                    }

                    //Both had better have run out at the same time
                    result &= (!thisParams.hasNext()) && (!eParams.hasNext());
                }
            }

            if (result) {
                if (myEnhancementRealizPairs != null
                        && facilityDec.myEnhancementRealizPairs != null) {
                    result &= myProfileName.equals(facilityDec.myProfileName);
                }
            }

            if (result) {
                result &=
                        (myExternallyRealizedFlag == facilityDec.myExternallyRealizedFlag);
            }
        }

        return result;
    }

    /**
     * <p>Returns the symbol representation for the concept name.</p>
     *
     * @return A {@link PosSymbol} representation of the concept name.
     */
    public PosSymbol getConceptName() {
        return myConceptName.clone();
    }

    /**
     * <p>Returns the list of arguments for this concept
     * extension.</p>
     *
     * @return A list of {@link ModuleArgumentItem} representation objects.
     */
    public List<ModuleArgumentItem> getConceptParams() {
        return copyConceptArgs();
    }

    /**
     * <p>Returns the symbol representation for the concept realization name.</p>
     *
     * @return A {@link PosSymbol} representation of the enhancement realization name.
     */
    public PosSymbol getConceptRealizName() {
        return myConceptRealizName.clone();
    }

    /**
     * <p>Returns the list of arguments for the enhancement realization.</p>
     *
     * @return A list of {@link ModuleArgumentItem} representation objects.
     */
    public List<ModuleArgumentItem> getConceptRealizParams() {
        return copyConceptRealizArgs();
    }

    /**
     * <p>Returns the list of enhancement extensions implemented by the concept
     * realization.</p>
     *
     * @return A list of {@link EnhancementItem} representation objects.
     */
    public List<EnhancementItem> getEnhancements() {
        return copyEnhItems();
    }

    /**
     * <p>Returns the list of enhancement/realizations in this facility
     * declaration.</p>
     *
     * @return A list of {@link EnhancementBodyItem} representation objects.
     */
    public List<EnhancementBodyItem> getEnhancementRealizPairs() {
        return copyEnhBodyItems();
    }

    /**
     * <p>Returns the value of the myExternallyRealizedFlag variable.</p>
     *
     * @return True if the realization is externally realized, false otherwise.
     */
    public boolean getExternallyRealizedFlag() {
        return myExternallyRealizedFlag;
    }

    /**
     * <p>Returns the symbol representation for the performance profile name.</p>
     *
     * @return A {@link PosSymbol} representation of the performance profile name.
     */
    public PosSymbol getProfileName() {
        PosSymbol profileName = null;
        if (myProfileName != null) {
            profileName = myProfileName.clone();
        }

        return profileName;
    }

    /**
     * <p>Returns the symbol in string format.</p>
     *
     * @return Symbol as a string.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Facility ");
        sb.append(myName.toString());
        sb.append(" is ");

        sb.append(myConceptName.toString());
        sb.append("(");
        Iterator<ModuleArgumentItem> cArgIt = myConceptParams.iterator();
        while (cArgIt.hasNext()) {
            sb.append(cArgIt.next().toString());

            if (cArgIt.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");

        for (EnhancementItem eItem : myEnhancements) {
            sb.append("\n");
            sb.append(eItem.toString());
        }

        sb.append("\n");
        sb.append(myConceptRealizName.toString());
        sb.append("(");
        Iterator<ModuleArgumentItem> cRealizArgIt =
                myConceptRealizParams.iterator();
        while (cRealizArgIt.hasNext()) {
            sb.append(cRealizArgIt.next().toString());

            if (cRealizArgIt.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");

        for (EnhancementBodyItem bodyItem : myEnhancementRealizPairs) {
            sb.append("\n");
            sb.append(bodyItem.toString());
        }
        sb.append(";");

        return sb.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the concept parameter arguments.</p>
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
     * <p>This is a helper method that makes a copy of the
     * list containing all the concept realization parameter arguments.</p>
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
     * <p>This is a helper method that makes a copy of the
     * list containing all the enhancement realization items.</p>
     *
     * @return A list containing {@link EnhancementBodyItem}s.
     */
    private List<EnhancementBodyItem> copyEnhBodyItems() {
        List<EnhancementBodyItem> copyArgs = new ArrayList<>();
        for (EnhancementBodyItem eb : myEnhancementRealizPairs) {
            copyArgs.add(eb.clone());
        }

        return copyArgs;
    }

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the enhancement items.</p>
     *
     * @return A list containing {@link EnhancementItem}s.
     */
    private List<EnhancementItem> copyEnhItems() {
        List<EnhancementItem> copyArgs = new ArrayList<>();
        for (EnhancementItem e : myEnhancements) {
            copyArgs.add(e.clone());
        }

        return copyArgs;
    }
}