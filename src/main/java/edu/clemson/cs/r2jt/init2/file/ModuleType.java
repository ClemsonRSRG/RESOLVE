/**
 * ModuleType.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.init2.file;

/** Provides access to type checkable module types. */
public class ModuleType {

    // ===========================================================
    // Variables 
    // ===========================================================

    private final String myName;
    private final String myExtension;

    // ===========================================================
    // Constructors
    // ===========================================================

    private ModuleType(String name, String extension) {
        myName = name;
        myExtension = extension;
    }

    // ===========================================================
    // Objects
    // ===========================================================

    public final static ModuleType THEORY = new ModuleType("Theory", "mt");
    public final static ModuleType CONCEPT = new ModuleType("Concept", "co");
    public final static ModuleType ENHANCEMENT =
            new ModuleType("Enhancement", "en");
    public final static ModuleType REALIZATION =
            new ModuleType("Realization", "rb");
    public final static ModuleType FACILITY = new ModuleType("Facility", "fa");
    public final static ModuleType PROFILE = new ModuleType("Profile", "pp");

    // ===========================================================
    // Public Methods
    // ===========================================================

    public String getExtension() {
        return myExtension;
    }

    public String getName() {
        return myName;
    }

}