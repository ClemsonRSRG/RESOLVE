/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
