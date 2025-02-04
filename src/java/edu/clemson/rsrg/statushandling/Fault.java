/*
 * Fault.java
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

import edu.clemson.rsrg.parsing.data.Location;

public class Fault {
    private final FaultType type;
    private final String message;
    private final Location location;
    private final boolean critical;

    public Fault(FaultType typ, Location loc, String msg, boolean critical) {
        type = typ;
        message = msg;
        location = loc;
        this.critical = critical;
    }

    public boolean isType(FaultType type) {
        return this.type == type;
    }

    public boolean isCritical() {
        return this.critical;
    }

    public String getMessage() {
        return message;
    }

    public Location getLocation() {
        return location;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (critical) {
            sb.append("\nCRITICAL Fault: ");
        } else {
            sb.append("\nFault: ");
        }
        sb.append(type.toString());
        if (location != null) {
            sb.append("\nat ");
            sb.append(location.toString());
        }
        sb.append("\n");
        sb.append(message);
        sb.append("\n");
        return sb.toString();
    }
}
