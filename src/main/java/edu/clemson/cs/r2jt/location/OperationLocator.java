/**
 * OperationLocator.java
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

public class OperationLocator {

    // ===========================================================
    // Variables
    // ===========================================================

    private ErrorHandler err;

    //private Environment env = Environment.getInstance();

    private OldSymbolTable table;

    private boolean showErrors;

    // ===========================================================
    // Constructors
    // ===========================================================

    public OperationLocator(OldSymbolTable table, ErrorHandler eh) {
        this.table = table;
        this.err = eh;
    }

    public OperationLocator(OldSymbolTable table, boolean err, ErrorHandler eh) {
        this.table = table;
        showErrors = err;
        this.err = eh;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public OperationEntry locateOperation(PosSymbol name, List<Type> argtypes)
            throws SymbolSearchException {
        List<OperationEntry> opers = locateOperationsInStack(name);
        if (opers.size() == 0) {
            opers = locateOperationsInImports(name);
        }
        return getUniqueOperation(name, argtypes, opers);
    }

    public OperationEntry locateOperation(PosSymbol qual, PosSymbol name,
            List<Type> argtypes) throws SymbolSearchException {
        if (qual == null) {
            return locateOperation(name, argtypes);
        }
        QualifierLocator qlocator = new QualifierLocator(table, err);
        ModuleScope scope = qlocator.locateProgramModule(qual);
        if (scope.containsOperation(name.getSymbol())) {
            OperationEntry oper = scope.getOperation(name.getSymbol());
            checkOperationArguments(name, argtypes, oper);
            return oper;
        }
        else {
            if (showErrors) {
                String msg =
                        cantFindOperInModMessage(name.toString(), qual
                                .toString());
                err.error(qual.getLocation(), msg);
            }
            throw new SymbolSearchException();
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private List<OperationEntry> locateOperationsInStack(PosSymbol name)
            throws SymbolSearchException {
        List<OperationEntry> opers = new List<OperationEntry>();
        Stack<Scope> stack = table.getStack();
        Stack<Scope> hold = new Stack<Scope>();
        try {
            while (!stack.isEmpty()) {
                Scope scope = stack.pop();
                hold.push(scope);
                if (scope instanceof ProcedureScope) {
                    opers.addAll(locateOperationsInProc(name,
                            (ProcedureScope) scope));
                    if (opers.size() > 0) {
                        break;
                    }
                }
                else if (scope instanceof ModuleScope) {
                    ModuleScope mscope = (ModuleScope) scope;
                    if (mscope.containsOperation(name.getSymbol())) {
                        opers.add(mscope.getOperation(name.getSymbol()));
                    }
                    // FIX: Check for recursive operation
                    // should be added here.
                }
                else {
                    // continue
                }
            }
            return opers;
        }
        finally {
            while (!hold.isEmpty()) {
                stack.push(hold.pop());
            }
        }
    }

    private List<OperationEntry> locateOperationsInProc(PosSymbol name,
            ProcedureScope scope) throws SymbolSearchException {
        List<OperationEntry> opers = new List<OperationEntry>();
        Iterator<ModuleScope> i = scope.getVisibleModules();
        while (i.hasNext()) {
            ModuleScope iscope = i.next();
            if (iscope.containsOperation(name.getSymbol())) {
                opers.add(iscope.getOperation(name.getSymbol()));
            }
        }
        return opers;
    }

    private List<OperationEntry> locateOperationsInImports(PosSymbol name)
            throws SymbolSearchException {
        List<OperationEntry> opers = new List<OperationEntry>();
        Iterator<ModuleScope> i =
                table.getModuleScope().getProgramVisibleModules();
        while (i.hasNext()) {
            ModuleScope iscope = i.next();
            if (iscope.containsOperation(name.getSymbol())) {
                opers.add(iscope.getOperation(name.getSymbol()));
            }
        }
        return opers;
    }

    private OperationEntry getUniqueOperation(PosSymbol name,
            List<Type> argtypes, List<OperationEntry> opers)
            throws SymbolSearchException {
        if (opers.size() == 0) {
            if (showErrors) {
                String msg = cantFindOperMessage(name.toString());
                err.error(name.getLocation(), msg);
            }
            throw new SymbolSearchException();
        }
        else if (opers.size() == 1) {
            checkOperationArguments(name, argtypes, opers.get(0));
            return opers.get(0);
        }
        else { // opers.size() > 1
            return disambiguateOperations(name, argtypes, opers);
        }
    }

    private OperationEntry disambiguateOperations(PosSymbol name,
            List<Type> argtypes, List<OperationEntry> opers)
            throws SymbolSearchException {
        List<OperationEntry> newopers = new List<OperationEntry>();
        Iterator<OperationEntry> i = opers.iterator();
        while (i.hasNext()) {
            OperationEntry oper = i.next();
            if (argumentTypesMatch(oper, argtypes)) {
                newopers.add(oper);
            }
        }
        if (newopers.size() == 0) {
            List<Location> locs = getLocationList(opers);
            if (showErrors) {
                String sig =
                        getSignatureString(opers.get(0).getName(), argtypes);
                String msg = cantFindOperMessage(sig, locs.toString());
                err.error(name.getLocation(), msg);
            }
            throw new SymbolSearchException();
        }
        else if (newopers.size() == 1) {
            return newopers.get(0);
        }
        else { // newopers.size() > 1
            List<Location> locs = getLocationList(opers);
            if (showErrors) {
                String msg =
                        ambigOperRefMessage(name.toString(), locs.toString());
                err.error(name.getLocation(), msg);
            }
            throw new SymbolSearchException();
        }
    }

    private void checkOperationArguments(PosSymbol name, List<Type> argtypes,
            OperationEntry oper) throws SymbolSearchException {
        if (!argumentTypesMatch(oper, argtypes)) {
            if (showErrors) {
                String opersig = getSignatureString(oper);
                String targsig = getSignatureString(oper.getName(), argtypes);
                String msg = argTypeMismatchMessage(opersig, targsig);
                err.error(name.getLocation(), msg);
            }
            throw new SymbolSearchException();
        }
    }

    private boolean argumentTypesMatch(OperationEntry oper, List<Type> argtypes) {
        TypeMatcher matcher = new TypeMatcher();
        List<Type> partypes = new List<Type>();
        Iterator<VarEntry> i = oper.getParameters();
        while (i.hasNext()) {
            VarEntry var = i.next();
            partypes.add(var.getType());
        }
        if (argtypes.size() != partypes.size()) {
            return false;
        }
        Iterator<Type> j = argtypes.iterator();
        Iterator<Type> k = partypes.iterator();
        while (j.hasNext()) {
            Type argtype = j.next();
            Type partype = k.next();
            if (!matcher.programMatches(argtype, partype)) {
                return false;
            }
        }
        return true;
    }

    private String getSignatureString(OperationEntry oper) {
        StringBuffer sb = new StringBuffer();
        sb.append(oper.getName().toString());
        sb.append("(");
        Iterator<VarEntry> i = oper.getParameters();
        while (i.hasNext()) {
            VarEntry entry = i.next();
            sb.append(entry.getType().getProgramName().toString());
            if (i.hasNext()) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    private String getSignatureString(PosSymbol name, List<Type> argtypes) {
        StringBuffer sb = new StringBuffer();
        sb.append(name.toString());
        sb.append("(");
        Iterator<Type> i = argtypes.iterator();
        while (i.hasNext()) {
            Type type = i.next();
            sb.append(type.getProgramName().toString());
            if (i.hasNext()) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    private List<Location> getLocationList(List<OperationEntry> entries) {
        List<Location> locs = new List<Location>();
        Iterator<OperationEntry> i = entries.iterator();
        while (i.hasNext()) {
            OperationEntry entry = i.next();
            locs.add(entry.getLocation());
        }
        return locs;
    }

    // -----------------------------------------------------------
    // Error Related Methods
    // -----------------------------------------------------------

    private String cantFindOperInModMessage(String name, String module) {
        return "Cannot find an operation named " + name + " in module "
                + module + ".";
    }

    private String ambigOperRefMessage(String name, String mods) {
        return "The operation named " + name + " is found in more than one "
                + "module visible from this scope: " + mods + ".";
    }

    private String cantFindOperMessage(String name) {
        return "Cannot find an operation named " + name + ".";
    }

    private String cantFindOperMessage(String sig, String mods) {
        return "Cannot find the operation with signature " + sig
                + ", but found operations: " + mods + ".";
    }

    private String argTypeMismatchMessage(String opersig, String targsig) {
        return "Expected an operation with the signature " + targsig
                + " but found one with the signature " + opersig + ".";
    }

}
