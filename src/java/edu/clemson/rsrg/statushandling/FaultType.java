/*
 * FaultType.java
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

public enum FaultType {
    GENERIC_FAULT("Generic Warning"), INCORRECT_PARAMETER_MODE_USAGE("Incorrect Parameter Mode"),
    MISSING_INFO_VC_GEN("Missing Information About VC Generation"), COMPILER_EXCEPTION("Compiler Exception"),
    FLAG_DEPENDENCY_EXCEPTION("Flag Dependency Exception"), TOKEN_FAULT("Token Fault"), ANTLR_FAULT("Antlr Fault");

    private final String plainName;

    private FaultType(String plainName) {
        this.plainName = plainName;
    }

    public String toString() {
        return plainName;
    }
}
