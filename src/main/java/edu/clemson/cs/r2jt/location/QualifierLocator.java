/**
 * QualifierLocator.java
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
package edu.clemson.cs.r2jt.location;

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Stack;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.entry.*;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.Environment;
import edu.clemson.cs.r2jt.scope.*;
import edu.clemson.cs.r2jt.type.*;

public class QualifierLocator {

    // ===========================================================
    // Variables
    // ===========================================================

    private ErrorHandler err;

    //private Environment env = Environment.getInstance();

    private OldSymbolTable table;

    // ===========================================================
    // Constructors
    // ===========================================================

    public QualifierLocator(OldSymbolTable table, ErrorHandler err) {
        this.table = table;
        this.err = err;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public boolean isProgramQualifier(PosSymbol qual) {
        ModuleScope mainscope = table.getModuleScope();
        ModuleScope module = locateModuleInStack(qual);
        if (module == null) {
            if (mainscope.isProgramVisible(qual.getSymbol())) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return true;
        }
    }

    public boolean isMathQualifier(PosSymbol qual) {
        ModuleScope mainscope = table.getModuleScope();
        ModuleScope module = locateModuleInStack(qual);
        if (module == null) {
            if (mainscope.isMathVisible(qual.getSymbol())) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public ModuleScope locateMathModule(PosSymbol qual)
            throws SymbolSearchException {
        ModuleScope mainscope = table.getModuleScope();
        ModuleScope module = locateModuleInStack(qual);
        if (module == null) {
            if (mainscope.isMathVisible(qual.getSymbol())) {
                module = mainscope.getMathVisibleModule(qual.getSymbol());
            }
            else {
                String msg = cantFindMathModMessage(qual.toString());
                err.error(qual.getLocation(), msg);
                throw new SymbolSearchException();
            }
        }
        return module;
    }

    public ModuleScope locateProgramModule(PosSymbol qual)
            throws SymbolSearchException {
        assert qual != null : "qual is null";
        ModuleScope mainscope = table.getModuleScope();
        ModuleScope module = locateModuleInStack(qual);
        if (module == null) {
            if (mainscope.isProgramVisible(qual.getSymbol())) {
                module = mainscope.getProgramVisibleModule(qual.getSymbol());
            }
            else {
                String msg = cantFindProgModMessage(qual.toString());
                err.error(qual.getLocation(), msg);
                throw new SymbolSearchException();
            }
        }
        return module;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private ModuleScope locateModuleInStack(PosSymbol qual) {
        ModuleScope module = null;
        Stack<Scope> stack = table.getStack();
        Stack<Scope> hold = new Stack<Scope>();
        while (!stack.isEmpty()) {
            Scope scope = stack.pop();
            hold.push(scope);
            if (scope instanceof ProcedureScope) {
                module = locateModule(qual, (ProcedureScope) scope);
            }
            if (module != null) {
                break;
            }
            if (scope instanceof ProofScope) {
                module = locateModule(qual, (ProofScope) scope);
            }
            if (module != null) {
                break;
            }
        }
        while (!hold.isEmpty()) {
            stack.push(hold.pop());
        }
        return module;
    }

    private ModuleScope locateModule(PosSymbol name, ProcedureScope scope) {
        if (scope.containsVisibleModule(name.getSymbol())) {
            return scope.getVisibleModule(name.getSymbol());
        }
        else {
            return null;
        }
    }

    private ModuleScope locateModule(PosSymbol name, ProofScope scope) {
        if (scope.containsVisibleModule(name.getSymbol())) {
            return scope.getVisibleModule(name.getSymbol());
        }
        else {
            return null;
        }
    }

    // -----------------------------------------------------------
    // Error Related Methods
    // -----------------------------------------------------------

    private String cantFindProgModMessage(String qual) {
        return "The qualifier " + qual + " does not correspond to any "
                + "program modules visible from this scope.";
    }

    private String cantFindMathModMessage(String qual) {
        return "The qualifier " + qual + " does not correspond to any "
                + "modules visible from this scope.";
    }
}
