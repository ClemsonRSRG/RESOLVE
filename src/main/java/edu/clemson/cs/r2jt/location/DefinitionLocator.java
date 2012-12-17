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
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the Clemson University nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
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
 * Clemson University. Contributors to the initial version are:
 * 
 * Steven Atkinson
 * Greg Kulczycki
 * Kunal Chopra
 * John Hunt
 * Heather Keown
 * Ben Markle
 * Kim Roche
 * Murali Sitaraman
 */
/*
 * DefinitionLocator.java
 * 
 * The Resolve Software Composition Workbench Project
 * 
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.location;

import edu.clemson.cs.r2jt.absyn.Exp;
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

public class DefinitionLocator {

    // ===========================================================
    // Variables
    // ===========================================================

    private ErrorHandler err;

    //private Environment env = Environment.getInstance();

    private OldSymbolTable table;

    private boolean showErrors = true;

    private boolean local = false;

    private TypeMatcher tm;

    // ===========================================================
    // Constructors
    // ===========================================================

    public DefinitionLocator(OldSymbolTable table, boolean err, TypeMatcher tm,
            ErrorHandler eh) {
        this.table = table;
        showErrors = err;
        this.tm = tm;
        this.err = eh;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public DefinitionEntry locateDefinition(PosSymbol name)
            throws SymbolSearchException {
        List<DefinitionEntry> opers = locateDefinitionsInStack(name);
        if (opers.size() == 0) {
            opers = locateDefinitionsInImports(name);
        }
        if (opers.size() > 1) {
            List<Location> locs = getLocationList(opers);
            if (showErrors) {
                String msg =
                        ambigDefRefMessage(name.toString(), locs.toString());
                err.error(name.getLocation(), msg);
            }
            throw new SymbolSearchException();
        }
        else if (opers.size() == 0) {
            if (showErrors) {
                String msg = cantFindDefMessage(name.toString());
                err.error(name.getLocation(), msg);
            }
            throw new SymbolSearchException();
        }
        else {
            return opers.get(0);
        }
    }

    public DefinitionEntry locateDefinition(PosSymbol name, List<Type> argtypes)
            throws SymbolSearchException {
        List<DefinitionEntry> defs = locateDefinitionsInStack(name);
        if (defs.size() == 0) {
            defs = locateDefinitionsInImports(name);
        }
        return getUniqueDefinition(name, argtypes, defs);
    }

    /*
     * Locates a definition with the given name in the given module irrespective
     * of its parameters and returns it.  Outputs errors if showErrors is on,
     * throwing SymbolSearchException regardless.
     * 
     * @param qual The module to search.  If qual is null, all modules will be
     *             searched.
     * @param name The definition to find.
     * 
     * @return The <code>DefinitionEntry</code> associated with the given
     *         definition.
     *         
     * @throw SymbolSearchException If such a definition cannot be found.
     */
    public DefinitionEntry locateDefinition(PosSymbol qual, PosSymbol name)
            throws SymbolSearchException {

        if (qual == null) {
            return locateDefinition(name);
        }

        QualifierLocator qualifierLocator = new QualifierLocator(table, err);
        ModuleScope scope;
        try {
            scope = qualifierLocator.locateMathModule(qual);
        }
        catch (SymbolSearchException sx1) {
            scope = qualifierLocator.locateProgramModule(qual);
        }
        if (scope.containsDefinition(name.getSymbol())) {
            DefinitionEntry def = scope.getDefinition(name.getSymbol());
            return def;
        }
        else {
            if (showErrors) {
                String msg =
                        cantFindDefInModMessage(name.toString(), qual
                                .toString());
                err.error(qual.getLocation(), msg);
            }
            throw new SymbolSearchException();
        }
    }

    public DefinitionEntry locateDefinition(PosSymbol qual, PosSymbol name,
            List<Type> argtypes) throws SymbolSearchException {
        if (qual == null) {
            return locateDefinition(name, argtypes);
        }
        QualifierLocator qlocator = new QualifierLocator(table, err);
        ModuleScope scope;
        try {
            scope = qlocator.locateMathModule(qual);
        }
        catch (SymbolSearchException sx1) {
            scope = qlocator.locateProgramModule(qual);
        }
        if (scope.containsDefinition(name.getSymbol())) {
            DefinitionEntry def = scope.getDefinition(name.getSymbol());
            checkDefinitionArguments(name, argtypes, def);
            return def;
        }
        else {
            if (showErrors) {
                String msg =
                        cantFindDefInModMessage(name.toString(), qual
                                .toString());
                err.error(qual.getLocation(), msg);
            }
            throw new SymbolSearchException();
        }
    }

    /*public DefinitionEntry locateDefinition(PosSymbol qual, PosSymbol name,
            PosSymbol index) throws SymbolSearchException {
    	if (qual == null) { return locateDefinition(name); }
    	QualifierLocator qlocator = new QualifierLocator(table);
    	ModuleScope scope;
    	try {
    		scope = qlocator.locateMathModule(qual);
    	}
    	catch(SymbolSearchException sx1) {
    		scope = qlocator.locateProgramModule(qual);
    	}
    	if (scope.containsDefinition(name.getSymbol())) {
    		DefinitionEntry def = scope.getDefinition(name.getSymbol());
    		def.
    		checkDefinitionArguments(name, argtypes, def);
    		return def;
    	} else {
    		if(showErrors) {
    			String msg = cantFindDefInModMessage(name.toString(),
                              	qual.toString());
    			err.error(qual.getLocation(), msg);
    		}
    		throw new SymbolSearchException();
    	}
    }*/

    // ===========================================================
    // Private Methods
    // ===========================================================

    private List<DefinitionEntry> locateDefinitionsInStack(PosSymbol name)
            throws SymbolSearchException {
        List<DefinitionEntry> defs = new List<DefinitionEntry>();
        Stack<Scope> stack = table.getStack();
        Stack<Scope> hold = new Stack<Scope>();
        try {
            while (!stack.isEmpty()) {
                Scope scope = stack.pop();
                hold.push(scope);
                if (scope instanceof ProcedureScope) {
                    defs.addAll(locateDefinitionsInProc(name,
                            (ProcedureScope) scope));
                    if (defs.size() > 0) {
                        break;
                    }
                }
                else if (scope instanceof ProofScope) {
                    defs.addAll(locateDefinitionsInProof(name,
                            (ProofScope) scope));
                    if (defs.size() > 0) {
                        break;
                    }
                }
                else if (scope instanceof ModuleScope) {
                    ModuleScope mscope = (ModuleScope) scope;
                    if (mscope.containsDefinition(name.getSymbol())) {
                        defs.add(mscope.getDefinition(name.getSymbol()));
                    }
                    // FIX: Check for recursive operation
                    // should be added here.
                }
                else {
                    // continue
                }
            }
            return defs;
        }
        finally {
            while (!hold.isEmpty()) {
                stack.push(hold.pop());
            }
        }
    }

    private List<DefinitionEntry> locateDefinitionsInProc(PosSymbol name,
            ProcedureScope scope) throws SymbolSearchException {
        List<DefinitionEntry> defs = new List<DefinitionEntry>();
        Iterator<ModuleScope> i = scope.getVisibleModules();
        while (i.hasNext()) {
            ModuleScope iscope = i.next();
            if (iscope.containsDefinition(name.getSymbol())) {
                defs.add(iscope.getDefinition(name.getSymbol()));
            }
        }
        return defs;
    }

    private List<DefinitionEntry> locateDefinitionsInProof(PosSymbol name,
            ProofScope scope) throws SymbolSearchException {
        List<DefinitionEntry> defs = new List<DefinitionEntry>();
        if (scope.containsDefinition(name.getSymbol())) {
            defs.add(scope.getDefinition(name.getSymbol()));
        }
        return defs;
    }

    private List<DefinitionEntry> locateDefinitionsInImports(PosSymbol name)
            throws SymbolSearchException {
        List<DefinitionEntry> defs = new List<DefinitionEntry>();
        Iterator<ModuleScope> i =
                table.getModuleScope().getMathVisibleModules();
        while (i.hasNext()) {
            ModuleScope iscope = i.next();
            if (iscope.containsDefinition(name.getSymbol())) {
                defs.add(iscope.getDefinition(name.getSymbol()));
            }
        }
        return defs;
    }

    private DefinitionEntry getUniqueDefinition(PosSymbol name,
            List<Type> argtypes, List<DefinitionEntry> defs)
            throws SymbolSearchException {
        if (defs.size() == 0) {
            if (showErrors) {
                String msg = cantFindDefMessage(name.toString());
                err.error(name.getLocation(), msg);
            }
            throw new SymbolSearchException();
        }
        else if (defs.size() == 1) {
            checkDefinitionArguments(name, argtypes, defs.get(0));
            return defs.get(0);
        }
        else { // defs.size() > 1
            return disambiguateDefinitions(name, argtypes, defs);
        }
    }

    private DefinitionEntry disambiguateDefinitions(PosSymbol name,
            List<Type> argtypes, List<DefinitionEntry> defs)
            throws SymbolSearchException {
        List<DefinitionEntry> newdefs = new List<DefinitionEntry>();
        Iterator<DefinitionEntry> i = defs.iterator();
        while (i.hasNext()) {
            DefinitionEntry def = i.next();
            if (argumentTypesMatch(def, argtypes)) {
                newdefs.add(def);
            }
        }
        if (newdefs.size() == 0) {
            List<Location> locs = getLocationList(defs);
            if (showErrors) {
                String sig =
                        getSignatureString(defs.get(0).getName(), argtypes);
                String msg = cantFindDefMessage(sig, locs.toString());
                err.error(name.getLocation(), msg);
            }
            throw new SymbolSearchException();
        }
        else if (newdefs.size() == 1) {
            return newdefs.get(0);
        }
        else { // newdefs.size() > 1
            List<Location> locs = getLocationList(defs);
            if (showErrors) {
                String msg =
                        ambigDefRefMessage(name.toString(), locs.toString());
                err.error(name.getLocation(), msg);
            }
            throw new SymbolSearchException();
        }
    }

    private void checkDefinitionArguments(PosSymbol name, List<Type> argtypes,
            DefinitionEntry def) throws SymbolSearchException {
        if (!argumentTypesMatch(def, argtypes)) {
            if (showErrors) {
                String defsig = getSignatureString(def);
                String targsig = getSignatureString(def.getName(), argtypes);
                String msg = argTypeMismatchMessage(defsig, targsig);
                err.error(name.getLocation(), msg);
            }
            throw new SymbolSearchException();
        }
    }

    private boolean argumentTypesMatch(DefinitionEntry def, List<Type> argtypes) {
        List<Type> partypes = new List<Type>();
        Iterator<VarEntry> i = def.getParameters();
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
            //            System.out.println("A1: " + argtype.toString());
            //            System.out.println("B1: " + partype.toString());
            if (!tm.mathMatches(argtype, partype)) {
                return false;
            }
        }
        return true;
    }

    private String getSignatureString(DefinitionEntry def) {
        StringBuffer sb = new StringBuffer();
        sb.append(def.getName().toString());
        sb.append("(");
        Iterator<VarEntry> i = def.getParameters();
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

    private List<Location> getLocationList(List<DefinitionEntry> entries) {
        List<Location> locs = new List<Location>();
        Iterator<DefinitionEntry> i = entries.iterator();
        while (i.hasNext()) {
            DefinitionEntry entry = i.next();
            locs.add(entry.getLocation());
        }
        return locs;
    }

    // -----------------------------------------------------------
    // Error Related Methods
    // -----------------------------------------------------------

    private String cantFindDefInModMessage(String name, String module) {
        return "Cannot find a definition named " + name + " in module "
                + module + ".";
    }

    private String ambigDefRefMessage(String name, String mods) {
        return "The definition named " + name + " is found in more than one "
                + "module visible from this scope: " + mods + ".";
    }

    private String cantFindDefMessage(String name) {
        return "Cannot find a definition named " + name + ".";
    }

    private String cantFindDefMessage(String sig, String mods) {
        return "Cannot find the definition with signature " + sig
                + ", but found definitions: " + mods + ".";
    }

    private String argTypeMismatchMessage(String opersig, String targsig) {
        return "Expected a definition with the signature " + targsig
                + " but found one with the signature " + opersig + ".";
    }

}
