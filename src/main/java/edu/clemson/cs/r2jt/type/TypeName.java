/*
 * This software is released under the new BSD 2006 license.
 * 
 * Note the new BSD license is equivalent to the MIT License, except for the
 * no-endorsement final clause.
 * 
 * Copyright (c) 2007, Clemson University
 * 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer. 
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution. 
 *   * Neither the name of the Clemson University nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * This sofware has been developed by past and present members of the
 * Reusable Sofware Research Group (RSRG) in the School of Computing at
 * Clemson University.  Contributors to the initial version are:
 * 
 *     Steven Atkinson
 *     Greg Kulczycki
 *     Kunal Chopra
 *     John Hunt
 *     Heather Keown
 *     Ben Markle
 *     Kim Roche
 *     Murali Sitaraman
 */

/*
 * TypeName.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
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

    public ModuleID getModuleID() { return id; }
    
    /* Returns true if the given type is the same type family from the same
     * concept, without respect to facility.
     */
    public boolean canImplement(TypeName tname) {
    	return (id.equals(tname.id) &&
    			name == tname.name);
    }
    
    public boolean equals(TypeName tname) {
        return (id.equals(tname.id) &&
                facility == tname.facility &&
                facLine == tname.facLine &&
                facColumn == tname.facColumn &&
                name == tname.name);
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
