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
 * ScopeID.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.scope;

import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.init.Environment;

public class ScopeID {

    // ===========================================================
    // Variables
    // ===========================================================

    //private Environment env = Environment.getInstance();

    private static final int MODULE = 0;
    private static final int FACILITY = 1;
    private static final int OPERATION = 2;
    private static final int PROCEDURE = 3;
    private static final int DEFINITION = 4;
    private static final int STATEMENT = 5;
    private static final int EXPRESSION = 6;
    private static final int TYPE = 7;
    private static final int SHORT = 8;
    private static final int PROOF = 9;

    private ModuleID mid = null;

    private PosSymbol facility = null;

    private Symbol operation = null;

    private int kind = 0;

    private int index = 0;

    // ===========================================================
    // Constructors
    // ===========================================================

    private ScopeID(ModuleID mid, PosSymbol facility,
                    PosSymbol operation, int kind, int index) {
        this.mid = mid;
        this.facility = facility;
        if (facility != null) {
            //this.mid = env.getModuleID(facility.getFile());
        }
        if (operation != null) {
            this.operation = operation.getSymbol();
            //this.mid = env.getModuleID(operation.getFile());
        }
        this.kind = kind;
        this.index = index;
    }

    public static ScopeID createModuleScopeID(ModuleID id) {
        return new ScopeID(id, null, null, MODULE, 0);
    }

    public static ScopeID createFacilityScopeID(PosSymbol facility, ModuleID id) {
        return new ScopeID(id, facility, null, FACILITY, 0);
    }

    public static ScopeID createOperationScopeID(PosSymbol operation, ModuleID id) {
        return new ScopeID(id, null, operation, OPERATION, 0);
    }

    public static ScopeID createProcedureScopeID(PosSymbol operation, ModuleID id) {
        return new ScopeID(id, null, operation, PROCEDURE, 0);
    }

    public static ScopeID createProofScopeID(PosSymbol proof) {
    	return new ScopeID(null, null, proof, PROOF, 0);
    }
    
    public static ScopeID createDefinitionScopeID(PosSymbol def) {
        return new ScopeID(null, null, def, DEFINITION, 0);
    }

    public static ScopeID createExpressionScopeID(ModuleID id, int index) {
        return new ScopeID(id, null, null, EXPRESSION, index);
    }

    public static ScopeID createStatementScopeID(ModuleID id, int index) {
        return new ScopeID(id, null, null, STATEMENT, index);
    }

    public static ScopeID createTypeScopeID(ModuleID id, int index) {
        return new ScopeID(id, null, null, TYPE, index);
    }

    public static ScopeID createShortScopeID(ModuleID id) {
        return new ScopeID(id, null, null, SHORT, 0);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public ModuleID getModuleID() { return mid; }

    public boolean isModule() {
        return (kind == MODULE);
    }

    public boolean isLocalFacility() {
        return (kind == FACILITY);
    }

    public boolean isProcedure() {
        return (kind == PROCEDURE);
    }
    
    public boolean isOperation() {
    	return (kind == OPERATION);
    }
    
    public Symbol getOperation() {
    	return operation;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(mid.toString());
        switch (kind) {
        case MODULE: break;
        case FACILITY:
            sb.append("." + facility.toString());
            sb.append(facility.getPos().toString());
            break;
        case OPERATION:
            sb.append("." + operation.toString() + "(op)");
            break;
        case PROCEDURE:
            sb.append("." + operation.toString());
            break;
        case DEFINITION:
            sb.append(".DEF_" + index);
            break;
        case EXPRESSION:
            sb.append(".EXP_" + index);
            break;
        case STATEMENT:
            sb.append(".STMT_" + index);
            break;
        case TYPE:
            sb.append(".TYPE_" + index);
            break;
        case SHORT:
            sb.append("'");
            break;
        default:
            assert false : "kind is invalid: " + kind;
        }
        return sb.toString();
    }

    public int hashCode() {
        return toString().hashCode();
    } 

    public boolean equals(Object obj) {
        if (obj instanceof ScopeID) {
            ScopeID sid = (ScopeID)obj;
            if (!mid.equals(sid.mid)) { return false; }
            if (kind != sid.kind) { return false; }
            if (index != sid.index) { return false; }
            switch (kind) {
            case FACILITY:
                return (facility.getSymbol() == sid.facility.getSymbol() &&
                        facility.getPos().equals(sid.facility.getPos()));
            case OPERATION:
            case PROCEDURE:
                return (operation == sid.operation);
            default:
                return true;
            }
        } else {
            return false;
        }
    }
}
