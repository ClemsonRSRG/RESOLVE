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
 * ModuleID.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.data;

import edu.clemson.cs.r2jt.absyn.*;

public class ModuleID {

    // ===========================================================
    // Variables
    // ===========================================================

    private ModuleKind kind = null;

    private Symbol name = null;

    private Symbol eName = null;

    private Symbol cName = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    private ModuleID(Symbol name, ModuleKind kind) {
        this.name = name;
        this.kind = kind;
    }

    private ModuleID(Symbol name, Symbol cName, ModuleKind kind) {
        this.name = name;
        this.cName = cName;
        this.kind = kind;
    }

    private ModuleID(Symbol name, Symbol eName, Symbol cName) {
        this.name = name;
        this.eName = eName;
        this.cName = cName;
        this.kind = ModuleKind.ENHANCEMENT_BODY;
    }

    // ===========================================================
    // Creation Methods
    // ===========================================================

    public static ModuleID createTheoryID(Symbol sym) {
        return new ModuleID(sym, ModuleKind.THEORY);
    }

    public static ModuleID createFacilityID(Symbol sym) {
        return new ModuleID(sym, ModuleKind.FACILITY);
    }

    public static ModuleID createConceptID(PosSymbol name) {
        return new ModuleID(name.getSymbol(), ModuleKind.CONCEPT);
    }

    public static ModuleID createTheoryID(PosSymbol name) {
        return new ModuleID(name.getSymbol(), ModuleKind.THEORY);
    }
    
    public static ModuleID createProofID(PosSymbol name) {
    	return new ModuleID(name.getSymbol(), ModuleKind.PROOFS);
    }
    
    public static ModuleID createPerformanceID(PosSymbol name) {
    	return new ModuleID(name.getSymbol(),
    						ModuleKind.PERFORMANCE);
    }

    public static ModuleID createFacilityID(PosSymbol name) {
        return new ModuleID(name.getSymbol(), ModuleKind.FACILITY);
    }

    public static ModuleID createUsesItemID(PosSymbol name) {
        return new ModuleID(name.getSymbol(), ModuleKind.USES_ITEM);
    }

    public static ModuleID createEnhancementID(PosSymbol name,
                                               PosSymbol cName) {
        return new ModuleID(name.getSymbol(), cName.getSymbol(),
                            ModuleKind.ENHANCEMENT);
    }

    public static ModuleID createConceptBodyID(PosSymbol name,
                                               PosSymbol cName) {
        return new ModuleID(name.getSymbol(), cName.getSymbol(),
                            ModuleKind.CONCEPT_BODY);
    }

    public static ModuleID createEnhancementBodyID(PosSymbol name,
                                                   PosSymbol eName,
                                                   PosSymbol cName) {
        return new ModuleID(name.getSymbol(), eName.getSymbol(),
                            cName.getSymbol());
    }

    // -----------------------------------------------------------
    // Create ModuleID from ModuleDec
    // -----------------------------------------------------------

    public static ModuleID createID(ModuleDec dec) {
        ModuleID id = null;
        if (dec instanceof MathModuleDec) {
            PosSymbol name = dec.getName();
            id = createTheoryID(name);
        } else if (dec instanceof ConceptModuleDec) {
            PosSymbol name = dec.getName();
            id = createConceptID(name);
        } else if (dec instanceof FacilityModuleDec ||
                   dec instanceof ShortFacilityModuleDec) {
            PosSymbol name = dec.getName();
            id = createFacilityID(name);
        } else if (dec instanceof EnhancementModuleDec) {
            EnhancementModuleDec dec2 = (EnhancementModuleDec)dec;
            PosSymbol name = dec2.getName();
            PosSymbol cName = dec2.getConceptName();
            id = createEnhancementID(name, cName);
        } else if (dec instanceof ConceptBodyModuleDec) {
            ConceptBodyModuleDec dec2 = (ConceptBodyModuleDec)dec;
            PosSymbol name = dec2.getName();
            PosSymbol cName = dec2.getConceptName();
            id = createConceptBodyID(name, cName);
        } else if (dec instanceof EnhancementBodyModuleDec) {
            EnhancementBodyModuleDec dec2 = (EnhancementBodyModuleDec)dec;
            PosSymbol name = dec2.getName();
            PosSymbol eName = dec2.getEnhancementName();
            PosSymbol cName = dec2.getConceptName();
            id = createEnhancementBodyID(name, eName, cName);
        } else if (dec instanceof ProofModuleDec) {
        	ProofModuleDec dec2 = (ProofModuleDec)dec;
        	PosSymbol name = dec2.getName();
        	id = createProofID(name);
        } else if (dec instanceof PerformanceModuleDec) {
        	PerformanceModuleDec dec2 = (PerformanceModuleDec)dec;
        	PosSymbol name = dec2.getName();
        	id = createPerformanceID(name);
        }
        return id;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // OK?
    public boolean equals(ModuleID mid) {
    	if(cName != null && mid.getConceptName() != null) {
    		if(!cName.equals(mid.getConceptName().getName()))
    			return false;
    	}
    	if(eName != null && mid.getEnhancementName() != null) {
    		if(!eName.equals(mid.getEnhancementName().getName()))
    			return false;
    	}
    	return kind.toString().equals(mid.getModuleKind().toString()) &&
    	name.equals(mid.getName().getName());
    }
    
    public boolean hasConcept() {
        return (cName != null);
    }

    public boolean hasEnhancement() {
        return (eName != null);
    }

    public Symbol getName() {
        return name;
    }

    public Symbol getEnhancementName() {
        return eName;
    }

    public Symbol getConceptName() {
        return cName;
    }

    public ModuleID getConceptID() {
        assert cName != null : "cName is null";
        return new ModuleID(cName, ModuleKind.CONCEPT);
    }

    public ModuleID getEnhancementID() {
        assert cName != null : "cName is null";
        assert eName != null : "eName is null";
        return new ModuleID(eName, cName, ModuleKind.ENHANCEMENT);
    }

    public String getFilename() {
        StringBuffer sb = new StringBuffer();
        sb.append(name.toString());
        sb.append(kind.getExtension());
        return sb.toString();
    }

    public String getEnhancementFilename() {
        assert eName != null : "eName is null";
        return eName.toString() + ".en";
    }
    
    public String getConceptFilename() {
        assert cName != null : "cName is null";
        return cName.toString() + ".co";
    }
    
    public ModuleKind getModuleKind() {
        return kind;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

//      public String toString() {
//          StringBuffer sb = new StringBuffer();
//          sb.append("[");
//          sb.append(name.toString());
//          sb.append(kind.getExtension());
//          if (eName != null) {
//              sb.append(":" + eName.toString() + ".en");
//          }
//          if (cName != null) {
//              sb.append(":" + cName.toString() + ".co");
//          }
//          sb.append("]");
//          return sb.toString();
//      }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (cName != null) { sb.append(cName.toString() + "."); }
        //if (eName != null) { sb.append(eName.toString() + ":"); }
        if (name != null) {sb.append(name.toString()); }
        return sb.toString();
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof ModuleID) {
            ModuleID id = (ModuleID)obj;
            return (this.kind == id.kind && this.name == id.name &&
                    this.eName == id.eName && this.cName == id.cName);
        } else {
            return false;
        }
    }

//      public boolean equals(ModuleID id) {
//          return (this.kind == id.kind && this.name == id.name &&
//                  this.eName == id.eName && this.cName == id.cName);
//      }

}
