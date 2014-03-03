/**
 * ScopeID.java
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

    private ScopeID(ModuleID mid, PosSymbol facility, PosSymbol operation,
            int kind, int index) {
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

    public static ScopeID createOperationScopeID(PosSymbol operation,
            ModuleID id) {
        return new ScopeID(id, null, operation, OPERATION, 0);
    }

    public static ScopeID createProcedureScopeID(PosSymbol operation,
            ModuleID id) {
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

    public ModuleID getModuleID() {
        return mid;
    }

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
        case MODULE:
            break;
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
            ScopeID sid = (ScopeID) obj;
            if (!mid.equals(sid.mid)) {
                return false;
            }
            if (kind != sid.kind) {
                return false;
            }
            if (index != sid.index) {
                return false;
            }
            switch (kind) {
            case FACILITY:
                return (facility.getSymbol() == sid.facility.getSymbol() && facility
                        .getPos().equals(sid.facility.getPos()));
            case OPERATION:
            case PROCEDURE:
                return (operation == sid.operation);
            default:
                return true;
            }
        }
        else {
            return false;
        }
    }
}
