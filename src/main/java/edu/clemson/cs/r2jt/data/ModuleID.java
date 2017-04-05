/*
 * ModuleID.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
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

    // --ny
    private Symbol pName = null;

    private Symbol p2Name = null;

    private Symbol p3Name = null;

    private Symbol pcName = null;

    private Symbol pcpName = null;

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

    // --ny
    private ModuleID(Symbol name, Symbol cName, Symbol pName, ModuleKind kind) {
        this.name = name;
        this.cName = cName;
        this.pName = pName;
        this.kind = kind;
    }

    private ModuleID(Symbol name, Symbol eName, Symbol cName, Symbol pName,
            ModuleKind kind) {
        this.name = name;
        this.cName = cName;
        this.eName = eName;
        this.pName = pName;
        this.kind = kind;
    }

    private ModuleID(Symbol name, Symbol eName, Symbol cName) {
        this.name = name;
        this.eName = eName;
        this.cName = cName;
        this.kind = ModuleKind.ENHANCEMENT_BODY;
    }

    // --ny
    private ModuleID(Symbol name, Symbol eName, Symbol cName, Symbol pName) {
        this.name = name;
        this.eName = eName;
        this.cName = cName;
        this.pName = pName;
        this.kind = ModuleKind.ENHANCEMENT_BODY;
    }

    // --ny
    private ModuleID(Symbol name, Symbol p2Name, Symbol p3Name, Symbol pcName,
            Symbol pcpName, ModuleKind kind) {
        this.name = name;
        this.p2Name = p2Name;
        this.p3Name = p3Name;
        this.pcName = pcName;
        this.pcpName = pcpName;
        this.kind = ModuleKind.PROFILE;
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
        return new ModuleID(name.getSymbol(), ModuleKind.PROFILE);
    }

    public static ModuleID createFacilityID(PosSymbol name) {
        return new ModuleID(name.getSymbol(), ModuleKind.FACILITY);
    }

    public static ModuleID createUsesItemID(PosSymbol name) {
        return new ModuleID(name.getSymbol(), ModuleKind.USES_ITEM);
    }

    public static ModuleID createEnhancementID(PosSymbol name, PosSymbol cName) {
        return new ModuleID(name.getSymbol(), cName.getSymbol(),
                ModuleKind.ENHANCEMENT);
    }

    public static ModuleID createConceptBodyID(PosSymbol name, PosSymbol cName) {
        return new ModuleID(name.getSymbol(), cName.getSymbol(),
                ModuleKind.CONCEPT_BODY);
    }

    // --ny
    public static ModuleID createConceptBodyID(PosSymbol name, PosSymbol cName,
            PosSymbol pName) {
        return new ModuleID(name.getSymbol(), cName.getSymbol(), pName
                .getSymbol(), ModuleKind.CONCEPT_BODY);
    }

    public static ModuleID createEnhancementBodyID(PosSymbol name,
            PosSymbol eName, PosSymbol cName) {
        return new ModuleID(name.getSymbol(), eName.getSymbol(), cName
                .getSymbol());
    }

    // --ny
    public static ModuleID createEnhancementBodyID(PosSymbol name,
            PosSymbol eName, PosSymbol cName, PosSymbol pName) {
        return new ModuleID(name.getSymbol(), eName.getSymbol(), cName
                .getSymbol(), pName.getSymbol(), ModuleKind.ENHANCEMENT_BODY);
    }

    // --ny
    public static ModuleID createProfileID(PosSymbol name, PosSymbol p2Name,
            PosSymbol p3Name, PosSymbol pcName, PosSymbol pcpName) {
        return new ModuleID(name.getSymbol(), p2Name.getSymbol(), p3Name
                .getSymbol(), pcName.getSymbol(), pcpName.getSymbol(),
                ModuleKind.PROFILE);
    }

    // -----------------------------------------------------------
    // Create ModuleID from ModuleDec
    // -----------------------------------------------------------

    public static ModuleID createID(ModuleDec dec) {
        ModuleID id = null;
        if (dec instanceof MathModuleDec) {
            PosSymbol name = dec.getName();
            id = createTheoryID(name);
        }
        else if (dec instanceof ConceptModuleDec) {
            PosSymbol name = dec.getName();
            id = createConceptID(name);
        }
        else if (dec instanceof FacilityModuleDec
                || dec instanceof ShortFacilityModuleDec) {
            PosSymbol name = dec.getName();
            id = createFacilityID(name);
        }
        else if (dec instanceof EnhancementModuleDec) {
            EnhancementModuleDec dec2 = (EnhancementModuleDec) dec;
            PosSymbol name = dec2.getName();
            PosSymbol cName = dec2.getConceptName();
            id = createEnhancementID(name, cName);
        }
        else if (dec instanceof ConceptBodyModuleDec) {
            ConceptBodyModuleDec dec2 = (ConceptBodyModuleDec) dec;
            PosSymbol name = dec2.getName();
            PosSymbol cName = dec2.getConceptName();
            id = createConceptBodyID(name, cName);
        }
        else if (dec instanceof EnhancementBodyModuleDec) {
            EnhancementBodyModuleDec dec2 = (EnhancementBodyModuleDec) dec;
            PosSymbol name = dec2.getName();
            PosSymbol eName = dec2.getEnhancementName();
            PosSymbol cName = dec2.getConceptName();
            id = createEnhancementBodyID(name, eName, cName);
        }
        else if (dec instanceof ProofModuleDec) {
            ProofModuleDec dec2 = (ProofModuleDec) dec;
            PosSymbol name = dec2.getName();
            id = createProofID(name);
        }
        else if (dec instanceof PerformanceEModuleDec) {
            PerformanceEModuleDec dec2 = (PerformanceEModuleDec) dec;
            PosSymbol Name = dec2.getName();
            PosSymbol p2Name = dec2.getProfileName2();
            PosSymbol p3Name = dec2.getProfileName3();
            PosSymbol pcName = dec2.getProfilecName();
            PosSymbol pcpName = dec2.getProfilecpName();
            //    id = createProfileID(Name, p2Name, p3Name, pcName, pcpName);
            id = createPerformanceID(Name);

        }
        else if (dec instanceof PerformanceCModuleDec) {
            PerformanceCModuleDec dec2 = (PerformanceCModuleDec) dec;
            PosSymbol Name = dec2.getName();
            PosSymbol p2Name = dec2.getProfileName1();
            PosSymbol p3Name = dec2.getProfilecName();
            id = createPerformanceID(Name);

        }
        return id;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // OK?
    public boolean equals(ModuleID mid) {
        if (cName != null && mid.getConceptName() != null) {
            if (!cName.equals(mid.getConceptName().getName()))
                return false;
        }
        if (eName != null && mid.getEnhancementName() != null) {
            if (!eName.equals(mid.getEnhancementName().getName()))
                return false;
        }
        return kind.toString().equals(mid.getModuleKind().toString())
                && name.equals(mid.getName().getName());
    }

    public boolean hasConcept() {
        return (cName != null);
    }

    public boolean hasEnhancement() {
        return (eName != null);
    }

    public boolean hasPerfProfile() {
        return (pName != null);
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

    public Symbol getPerfProfileName() {
        return pName;
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

    public ModuleID getPerfProfileID() {
        assert pName != null : "pName is null";
        assert eName != null : "eName is null";
        return new ModuleID(pName, ModuleKind.PROFILE);
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

    public String getPerfProfileFilename() {
        assert pName != null : "pName is null";
        return pName.toString() + ".pp";
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
        if (cName != null) {
            sb.append(cName.toString() + ".");
        }
        //if (eName != null) { sb.append(eName.toString() + ":"); }
        if (name != null) {
            sb.append(name.toString());
        }
        return sb.toString();
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof ModuleID) {
            ModuleID id = (ModuleID) obj;
            return (this.kind == id.kind && this.name == id.name
                    && this.eName == id.eName && this.cName == id.cName);
        }
        else {
            return false;
        }
    }

    //      public boolean equals(ModuleID id) {
    //          return (this.kind == id.kind && this.name == id.name &&
    //                  this.eName == id.eName && this.cName == id.cName);
    //      }

}
