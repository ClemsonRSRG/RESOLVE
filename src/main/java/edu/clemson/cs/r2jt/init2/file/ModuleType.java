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

/** <p>Provides access to type checkable module types.</p> */
public class ModuleType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private final String myName;
    private final String myExtension;

    // ===========================================================
    // Objects
    // ===========================================================

    /** <p>TODO: Description for this object.</p> */
    public final static ModuleType THEORY = new ModuleType("Theory", "mt");

    /** <p>TODO: Description for this object.</p> */
    public final static ModuleType CONCEPT = new ModuleType("Concept", "co");

    /** <p>TODO: Description for this object.</p> */
    public final static ModuleType ENHANCEMENT =
            new ModuleType("Enhancement", "en");

    /** <p>TODO: Description for this object.</p> */
    public final static ModuleType REALIZATION =
            new ModuleType("Realization", "rb");

    /** <p>TODO: Description for this object.</p> */
    public final static ModuleType FACILITY = new ModuleType("Facility", "fa");

    /** <p>TODO: Description for this object.</p> */
    public final static ModuleType PROFILE = new ModuleType("Profile", "pp");

    // ===========================================================
    // Constructors
    // ===========================================================

    private ModuleType(String name, String extension) {
        myName = name;
        myExtension = extension;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>TODO: Decription for this method.</p>
     *
     * @return
     */
    public String getExtension() {
        return myExtension;
    }

    /**
     * <p>TODO: Decription for this method.</p>
     *
     * @return
     */
    public String getName() {
        return myName;
    }

}