/**
 * TypeName.java
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
package edu.clemson.cs.r2jt.type;

import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.ModuleKind;
import edu.clemson.cs.r2jt.data.Pos;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;

public class TypeName {

    // ===========================================================
    // Variables
    // ===========================================================

    private ModuleID id;

    private Symbol facility = null;

    private int facLine = 0;

    private int facColumn = 0;

    private Symbol name;

    // ===========================================================
    // Constructors
    // ===========================================================

    //FIX: Do we allow id of null to be passed in? If so, should
    //     we get it from the facility?
    public TypeName(ModuleID id, PosSymbol facility, PosSymbol name) {
        this.id = id;
        if (facility != null) {
            this.facility = facility.getSymbol();
            this.facLine = facility.getLocation().getPos().getLine();
            this.facColumn = facility.getLocation().getPos().getColumn();
        }
        this.name = name.getSymbol();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Symbol getName() {
        return name;
    }

    public Symbol getFacilityQualifier() {
        if (facility != null) {
            return facility;
        }
        if (id.getModuleKind() == ModuleKind.FACILITY) {
            return id.getName();
        }
        return null;
    }

    public ModuleID getModuleID() {
        return id;
    }

    /* Returns true if the given type is the same type family from the same
     * concept, without respect to facility.
     */
    public boolean canImplement(TypeName tname) {
        return (id.equals(tname.id) && name == tname.name);
    }

    public boolean equals(TypeName tname) {
        return (id.equals(tname.id) && facility == tname.facility
                && facLine == tname.facLine && facColumn == tname.facColumn && name == tname.name);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(id.toString());
        if (facility != null) {
            sb.append(facility.toString());
            sb.append("(" + facLine + "," + facColumn + ")");
        }
        sb.append("." + name.toString());
        return sb.toString();
    }
}
