/**
 * TheoremLocator.java
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

public class TheoremLocator {

    // ===========================================================
    // Variables
    // ===========================================================

    private ErrorHandler err;

    //private Environment env = Environment.getInstance();

    private OldSymbolTable table;

    private TypeMatcher tm;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TheoremLocator(OldSymbolTable table, TypeMatcher tm, ErrorHandler err) {
        this.table = table;
        this.tm = tm;
        this.err = err;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public TheoremEntry locateTheorem(PosSymbol name)
            throws SymbolSearchException {
        List<TheoremEntry> theorems = locateTheoremsInStack(name);
        if (theorems.size() == 0) {
            theorems = locateTheoremsInImports(name);
        }
        if (theorems.size() > 1) {
            List<Location> locs = getLocationList(theorems);
            String msg =
                    ambigTheoremRefMessage(name.toString(), locs.toString());
            err.error(name.getLocation(), msg);
            throw new SymbolSearchException();
        }
        else if (theorems.size() == 0) {
            String msg = cantFindTheoremMessage(name.toString());
            err.error(name.getLocation(), msg);
            throw new SymbolSearchException();
        }
        else {
            return theorems.get(0);
        }
    }

    public TheoremEntry locateTheorem(PosSymbol qual, PosSymbol name)
            throws SymbolSearchException {
        if (qual == null) {
            return locateTheorem(name);
        }
        QualifierLocator qlocator = new QualifierLocator(table, err);
        ModuleScope scope;
        scope = qlocator.locateMathModule(qual);
        if (scope.containsTheorem(name.getSymbol())) {
            TheoremEntry p = scope.getTheorem(name.getSymbol());
            return p;
        }
        else {
            String msg =
                    cantFindTheoremInModMessage(name.toString(), qual
                            .toString());
            err.error(qual.getLocation(), msg);
            throw new SymbolSearchException();
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private List<TheoremEntry> locateTheoremsInStack(PosSymbol name)
            throws SymbolSearchException {
        List<TheoremEntry> theorems = new List<TheoremEntry>();
        Stack<Scope> stack = table.getStack();
        Stack<Scope> hold = new Stack<Scope>();
        try {
            while (!stack.isEmpty()) {
                Scope scope = stack.pop();
                hold.push(scope);
                if (scope instanceof ModuleScope) {
                    ModuleScope mscope = (ModuleScope) scope;
                    if (mscope.containsTheorem(name.getSymbol())) {
                        theorems.add(mscope.getTheorem(name.getSymbol()));
                    }
                    // FIX: Check for recursive operation
                    // should be added here.
                }
            }
            return theorems;
        }
        finally {
            while (!hold.isEmpty()) {
                stack.push(hold.pop());
            }
        }
    }

    private List<TheoremEntry> locateTheoremsInImports(PosSymbol name)
            throws SymbolSearchException {
        List<TheoremEntry> theorems = new List<TheoremEntry>();
        Iterator<ModuleScope> i =
                table.getModuleScope().getMathVisibleModules();
        while (i.hasNext()) {
            ModuleScope iscope = i.next();
            if (iscope.containsTheorem(name.getSymbol())) {
                theorems.add(iscope.getTheorem(name.getSymbol()));
            }
        }
        return theorems;
    }

    private List<Location> getLocationList(List<TheoremEntry> entries) {
        List<Location> locs = new List<Location>();
        Iterator<TheoremEntry> i = entries.iterator();
        while (i.hasNext()) {
            TheoremEntry entry = i.next();
            locs.add(entry.getLocation());
        }
        return locs;
    }

    // -----------------------------------------------------------
    // Error Related Methods
    // -----------------------------------------------------------

    private String cantFindTheoremInModMessage(String name, String module) {
        return "Cannot find a theorem named " + name + " in module " + module
                + ".";
    }

    public String cantFindTheoremMessage(String name) {
        return "Cannot find a theorem named " + name + ".";
    }

    private String ambigTheoremRefMessage(String name, String mods) {
        return "The theorem named " + name + " is found in more than one "
                + "module visible from this scope: " + mods + ".";
    }

}
