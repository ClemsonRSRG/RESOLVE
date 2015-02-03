/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
