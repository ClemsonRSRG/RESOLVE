/**
 * EnhancementBodyItem.java
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
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;

public class EnhancementBodyItem extends ResolveConceptualElement {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The name member. */
    private PosSymbol name;

    /** The performance profile name member. */
    private PosSymbol profileName;

    /** The params member. */
    private List<ModuleArgumentItem> params;

    /** The bodyName member. */
    private PosSymbol bodyName;

    /** The bodyParams member. */
    private List<ModuleArgumentItem> bodyParams;

    // ===========================================================
    // Constructors
    // ===========================================================

    public EnhancementBodyItem() {};

    public EnhancementBodyItem(PosSymbol name, List<ModuleArgumentItem> params,
            PosSymbol bodyName, PosSymbol profileName,
            List<ModuleArgumentItem> bodyParams) {
        this.name = name;
        this.params = params;
        this.bodyName = bodyName;
        this.profileName = profileName;
        this.bodyParams = bodyParams;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    public Location getLocation() {
        return name.getLocation();
    }

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the profileName variable. */
    public PosSymbol getProfileName() {
        return profileName;
    }

    /** Returns the value of the params variable. */
    public List<ModuleArgumentItem> getParams() {
        return params;
    }

    /** Returns the value of the bodyName variable. */
    public PosSymbol getBodyName() {
        return bodyName;
    }

    /** Returns the value of the bodyParams variable. */
    public List<ModuleArgumentItem> getBodyParams() {
        return bodyParams;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the profileName variable to the specified value. */
    public void setProfileName(PosSymbol name) {
        this.profileName = name;
    }

    /** Sets the params variable to the specified value. */
    public void setParams(List<ModuleArgumentItem> params) {
        this.params = params;
    }

    /** Sets the bodyName variable to the specified value. */
    public void setBodyName(PosSymbol bodyName) {
        this.bodyName = bodyName;
    }

    /** Sets the bodyParams variable to the specified value. */
    public void setBodyParams(List<ModuleArgumentItem> bodyParams) {
        this.bodyParams = bodyParams;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitEnhancementBodyItem(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("EnhancementBodyItem\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (params != null) {
            sb.append(params.asString(indent + increment, increment));
        }

        if (bodyName != null) {
            sb.append(bodyName.asString(indent + increment, increment));
        }

        if (bodyParams != null) {
            sb.append(bodyParams.asString(indent + increment, increment));
        }

        return sb.toString();
    }
}
