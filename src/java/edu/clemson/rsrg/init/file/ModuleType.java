/*
 * ModuleType.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.init.file;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class contains all the possible RESOLVE extension files as well as helper methods to retrieve the description
 * and name of the extension.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class ModuleType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Extension Description.
     * </p>
     */
    private final String myDescription;

    /**
     * <p>
     * Extension Name.
     * </p>
     */
    private final String myExtension;

    // ===========================================================
    // Objects
    // ===========================================================

    /**
     * <p>
     * The extension type object for theory files.
     * </p>
     */
    public final static ModuleType THEORY = new ModuleType("mt", "Theory files");

    /**
     * <p>
     * The extension type object for concept module files.
     * </p>
     */
    public final static ModuleType CONCEPT = new ModuleType("co", "Concept modules");

    /**
     * <p>
     * The extension type object for enhancement module files.
     * </p>
     */
    public final static ModuleType ENHANCEMENT = new ModuleType("en", "Enhancement modules");

    /**
     * <p>
     * The extension type object for concept/enhancement realization module files.
     * </p>
     */
    public final static ModuleType REALIZATION = new ModuleType("rb", "Realization for Concept/Enhancement modules");

    /**
     * <p>
     * The extension type object for facility module files.
     * </p>
     */
    public final static ModuleType FACILITY = new ModuleType("fa", "Facility modules");

    /**
     * <p>
     * The extension type object for performance profile files.
     * </p>
     */
    public final static ModuleType PROFILE = new ModuleType("pp",
            "Performance profiles for Concept/Enhancement modules");

    // ===========================================================
    // Constructors
    // ===========================================================

    private ModuleType(String extension, String description) {
        myDescription = description;
        myExtension = extension;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Obtain the list of all possible extensions for RESOLVE files.
     * </p>
     *
     * @return The list of extension names.
     */
    public static List<String> getAllExtensions() {
        List<String> extensions = new ArrayList<>();
        extensions.add(THEORY.getExtension());
        extensions.add(CONCEPT.getExtension());
        extensions.add(ENHANCEMENT.getExtension());
        extensions.add(REALIZATION.getExtension());
        extensions.add(FACILITY.getExtension());
        extensions.add(PROFILE.getExtension());

        return extensions;
    }

    /**
     * <p>
     * Obtains the description of the extension as a {@code String}.
     * </p>
     *
     * @return The extension's description.
     */
    public final String getDescription() {
        return myDescription;
    }

    /**
     * <p>
     * Obtains the name of the extension as a {@code String}.
     * </p>
     *
     * @return The extension's name.
     */
    public final String getExtension() {
        return myExtension;
    }

}
