/*
 * FacilityDec.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.PosSymbol;

public class FacilityDec extends Dec {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The conceptName member. */
    private PosSymbol conceptName;

    /** The conceptParams member. */
    private List<ModuleArgumentItem> conceptParams;

    /** The enhancements member. */
    private List<EnhancementItem> enhancements;

    /** The bodyName member. */
    private PosSymbol bodyName;

    /** The performance profile name member. */
    private PosSymbol profileName;

    /** The bodyParams member. */
    private List<ModuleArgumentItem> bodyParams;

    /** The enhancementBodies member. */
    private List<EnhancementBodyItem> enhancementBodies;

    /**
     * <p>Tells us whether or not the facility's realization
     * has a implementation written in Resolve. If it does,
     * then this flag should be true, otherwise, it will be
     * false.</p>
     */
    private boolean myExternallyRealizedFlag;

    // ===========================================================
    // Constructors
    // ===========================================================

    public FacilityDec() {};

    public FacilityDec(PosSymbol name, PosSymbol conceptName,
            List<ModuleArgumentItem> conceptParams,
            List<EnhancementItem> enhancements, PosSymbol bodyName,
            PosSymbol profileName, List<ModuleArgumentItem> bodyParams,
            List<EnhancementBodyItem> enhancementBodies) {

        this(name, conceptName, conceptParams, enhancements, bodyName,
                profileName, bodyParams, enhancementBodies, false);
    }

    public FacilityDec(PosSymbol name, PosSymbol conceptName,
            List<ModuleArgumentItem> conceptParams,
            List<EnhancementItem> enhancements, PosSymbol bodyName,
            PosSymbol profileName, List<ModuleArgumentItem> bodyParams,
            List<EnhancementBodyItem> enhancementBodies, boolean externRealized) {

        this.name = name;
        this.conceptName = conceptName;
        this.conceptParams = conceptParams;
        this.enhancements = enhancements;
        this.bodyName = bodyName;
        this.profileName = profileName;
        this.bodyParams = bodyParams;
        this.enhancementBodies = enhancementBodies;
        myExternallyRealizedFlag = externRealized;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the conceptName variable. */
    public PosSymbol getConceptName() {
        return conceptName;
    }

    /** Returns the value of the conceptParams variable. */
    public List<ModuleArgumentItem> getConceptParams() {
        return conceptParams;
    }

    /** Returns the value of the enhancements variable. */
    public List<EnhancementItem> getEnhancements() {
        return enhancements;
    }

    /** Returns the value of the bodyName variable. */
    public PosSymbol getBodyName() {
        return bodyName;
    }

    /** Returns the value of the profileName variable. */
    public PosSymbol getProfileName() {
        return profileName;
    }

    /** Returns the value of the bodyParams variable. */
    public List<ModuleArgumentItem> getBodyParams() {
        return bodyParams;
    }

    /** Returns the value of the enhancementBodies variable. */
    public List<EnhancementBodyItem> getEnhancementBodies() {
        return enhancementBodies;
    }

    /** Returns the value of the myExternallyRealizedFlag variable. */
    public boolean getExternallyRealizedFlag() {
        return myExternallyRealizedFlag;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the conceptName variable to the specified value. */
    public void setConceptName(PosSymbol conceptName) {
        this.conceptName = conceptName;
    }

    /** Sets the conceptParams variable to the specified value. */
    public void setConceptParams(List<ModuleArgumentItem> conceptParams) {
        this.conceptParams = conceptParams;
    }

    /** Sets the enhancements variable to the specified value. */
    public void setEnhancements(List<EnhancementItem> enhancements) {
        this.enhancements = enhancements;
    }

    /** Sets the bodyName variable to the specified value. */
    public void setBodyName(PosSymbol bodyName) {
        this.bodyName = bodyName;
    }

    /** Sets the profileName variable to the specified value. */
    public void setProfileName(PosSymbol name) {
        this.profileName = name;
    }

    /** Sets the bodyParams variable to the specified value. */
    public void setBodyParams(List<ModuleArgumentItem> bodyParams) {
        this.bodyParams = bodyParams;
    }

    /** Sets the enhancementBodies variable to the specified value. */
    public void setEnhancementBodies(List<EnhancementBodyItem> enhancementBodies) {
        this.enhancementBodies = enhancementBodies;
    }

    /** Sets the myExternallyRealizedFlag variable to the specified value. */
    public void setExternallyRealizedFlag(boolean externRealized) {
        myExternallyRealizedFlag = externRealized;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitFacilityDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("FacilityDec\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (conceptName != null) {
            sb.append(conceptName.asString(indent + increment, increment));
        }

        if (conceptParams != null) {
            sb.append(conceptParams.asString(indent + increment, increment));
        }

        if (enhancements != null) {
            sb.append(enhancements.asString(indent + increment, increment));
        }

        if (bodyName != null) {
            sb.append(bodyName.asString(indent + increment, increment));
        }

        if (bodyParams != null) {
            sb.append(bodyParams.asString(indent + increment, increment));
        }

        if (enhancementBodies != null) {
            sb
                    .append(enhancementBodies.asString(indent + increment,
                            increment));
        }

        return sb.toString();
    }
}
