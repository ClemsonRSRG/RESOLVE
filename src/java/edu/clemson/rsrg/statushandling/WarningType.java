/*
 * WarningType.java
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
package edu.clemson.rsrg.statushandling;

public enum WarningType {
    GENERIC_WARNING("Generic Warning"), INCORRECT_PARAMETER_MODE_USAGE("Incorrect Parameter Mode"),
    MISSING_INFO_VC_GEN("Missing Information About VC Generation");

    private final String plainName;

    private WarningType(String plainName) {
        this.plainName = plainName;
    }

    public String toString() {
        return plainName;
    }
}
