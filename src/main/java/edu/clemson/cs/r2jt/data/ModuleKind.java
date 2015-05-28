/**
 * ModuleKind.java
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
package edu.clemson.cs.r2jt.data;

/** Provides access to type checkable module types. */
public class ModuleKind {

    // ===========================================================
    // Variables 
    // ===========================================================

    private String name;

    // ===========================================================
    // Constructors
    // ===========================================================

    private ModuleKind(String name) {
        this.name = name;
    }

    // ===========================================================
    // Objects
    // ===========================================================

    public final static ModuleKind THEORY = new ModuleKind("Theory");
    public final static ModuleKind PROOFS = new ModuleKind("Proofs");
    public final static ModuleKind CONCEPT = new ModuleKind("Concept");
    public final static ModuleKind ENHANCEMENT = new ModuleKind("Enhancement");
    public final static ModuleKind REALIZATION = new ModuleKind("Realization");
    public final static ModuleKind CONCEPT_BODY =
            new ModuleKind("Concept Body");
    public final static ModuleKind ENHANCEMENT_BODY =
            new ModuleKind("Enhancement Body");
    public final static ModuleKind FACILITY = new ModuleKind("Facility");
    public final static ModuleKind LONG_FACILITY =
            new ModuleKind("Long Facility");
    public final static ModuleKind SHORT_FACILITY =
            new ModuleKind("Short Facility");
    public final static ModuleKind USES_ITEM = new ModuleKind("Uses Item");

    public final static ModuleKind PROFILE = new ModuleKind("Profile");
    public final static ModuleKind UNDEFINED = new ModuleKind("Undefined");

    // ===========================================================
    // Public Methods
    // ===========================================================

    public String getExtension() {
        String str = "";
        if (this == ModuleKind.THEORY) {
            str = ".mt";
        }
        else if (this == ModuleKind.PROOFS) {
            str = ".mt";
        }
        else if (this == ModuleKind.CONCEPT) {
            str = ".co";
        }
        else if (this == ModuleKind.ENHANCEMENT) {
            str = ".en";
        }
        else if (this == ModuleKind.REALIZATION
                || this == ModuleKind.CONCEPT_BODY
                || this == ModuleKind.ENHANCEMENT_BODY) {
            str = ".rb";
        }
        else if (this == ModuleKind.FACILITY
                || this == ModuleKind.SHORT_FACILITY
                || this == ModuleKind.LONG_FACILITY) {
            str = ".fa";
        }
        else if (this == ModuleKind.PROFILE) {
            str = ".pp";
        }

        return str;
    }

    public String toString() {
        return name;
    }

}
