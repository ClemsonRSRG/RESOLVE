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
 * TypeLocator.java
 * 
 * The Resolve Software Composition Workbench Project
 * 
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.location;

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.entry.*;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.init.Environment;
import edu.clemson.cs.r2jt.scope.*;
import edu.clemson.cs.r2jt.type.*;

public class TypeLocator {

    // ===========================================================
    // Variables
    // ===========================================================

    private ErrorHandler err;

    //private Environment env = Environment.getInstance();
    CompileEnvironment myInstanceEnvironment;

    private ModuleScope modscope = null;

    private ProcedureScope procscope = null;

    private ProofScope proofscope = null;

    private boolean showErrors = true;

    /**
     * Search only for math types */
    private boolean mathTypesOnly = false;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TypeLocator(OldSymbolTable table,
            CompileEnvironment instanceEnvironment) {
        myInstanceEnvironment = instanceEnvironment;
        Scope curScope = table.getCurrentScope();
        if (curScope instanceof ProcedureScope) {
            procscope = (ProcedureScope) curScope;
            modscope = procscope.getModuleScope();
        }
        else if (curScope instanceof ProofScope) {
            proofscope = (ProofScope) curScope;
            modscope = proofscope.getModuleScope();
        }
        else {
            modscope = table.getModuleScope();
        }
        this.err = instanceEnvironment.getErrorHandler();
    }

    public TypeLocator(Scope scope, CompileEnvironment instanceEnvironment) {
        myInstanceEnvironment = instanceEnvironment;
        if (scope instanceof ProcedureScope) {
            procscope = (ProcedureScope) scope;
            modscope = procscope.getModuleScope();
        }
        else if (scope instanceof ProofScope) {
            proofscope = (ProofScope) scope;
            modscope = proofscope.getModuleScope();
        }
        else {
            modscope = (ModuleScope) scope;
        }
        this.err = instanceEnvironment.getErrorHandler();
    }

    public TypeLocator(OldSymbolTable table, boolean err,
            CompileEnvironment instanceEnvironment) {
        myInstanceEnvironment = instanceEnvironment;
        Scope curScope = table.getCurrentScope();
        if (curScope instanceof ProcedureScope) {
            procscope = (ProcedureScope) curScope;
            modscope = procscope.getModuleScope();
        }
        else if (curScope instanceof ProofScope) {
            proofscope = (ProofScope) curScope;
            modscope = proofscope.getModuleScope();
        }
        else {
            modscope = table.getModuleScope();
        }
        showErrors = err;
        this.err = instanceEnvironment.getErrorHandler();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public TypeEntry locateMathType(TypeID tid) throws SymbolSearchException {
        TypeEntry retval;

        Symbol tidName = tid.getName();
        if (tidName.getName().equals("SSet")) {
            retval =
                    new TypeEntry(new PosSymbol(null, tidName), new VoidType());
        }
        else {
            mathTypesOnly = false;
            if (procscope != null) {
                retval = locatePSMathType(tid);
            }
            else if (proofscope != null) {
                retval = locatePrSMathType(tid);
            }
            else {
                retval = locateMSMathType(tid);
            }
        }

        return retval;
    }

    public TypeEntry strictLocateMathType(TypeID tid)
            throws SymbolSearchException {
        mathTypesOnly = true;
        if (procscope != null) {
            return locatePSMathType(tid);
        }
        else if (proofscope != null) {
            return locatePrSMathType(tid);
        }
        return locateMSMathType(tid);
    }

    public TypeEntry locateProgramType(TypeID tid) throws SymbolSearchException {
        mathTypesOnly = false;
        if (procscope != null) {
            return locatePSProgramType(tid);
        }
        return locateMSProgramType(tid);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private TypeEntry locatePSMathType(TypeID tid) throws SymbolSearchException {
        if (tid.getQualifier() != null) {
            Symbol qsym = tid.getQualifier();
            Symbol nsym = tid.getName();
            if (procscope.containsVisibleModule(qsym)) {
                ModuleScope mod = procscope.getVisibleModule(qsym);
                if (mod.containsType(nsym)) {
                    TypeEntry entry = mod.getType(nsym);
                    if (mathTypesOnly) {
                        if (isMathType(entry)) {
                            return entry;
                        }
                    }
                    else {
                        return entry;
                    }
                }
            }
            return locateMSMathType(tid);
        }
        else {
            List<TypeEntry> entries =
                    getTypeEntries(tid.getName(), procscope.getVisibleModules());
            if (entries.size() == 0) {
                return locateMSMathType(tid);
            }
            else if (entries.size() > 1) {
                List<Location> locs = getLocationList(entries);
                String msg =
                        ambigRefMessage(tid.getName().toString(), locs
                                .toString());
                throw new SymbolSearchException(msg);
            }
            else { // exactly one entry!
                TypeEntry entry = entries.get(0);
                checkParamSizeEqual(tid, entry.getType(), entry.getLocation());
                if (mathTypesOnly) {
                    if (isMathType(entry)) {
                        return entry;
                    }
                    else {
                        String msg =
                                cantFindMathTypeMessage(tid.getName().getName());
                        throw new SymbolSearchException(msg);
                    }
                }
                else {
                    return entry;
                }
            }
        }
        //        assert false : "could not locateMSMathType";
        //        return null;
    }

    private TypeEntry locatePrSMathType(TypeID tid)
            throws SymbolSearchException {
        if (tid.getQualifier() != null) {
            Symbol qsym = tid.getQualifier();
            Symbol nsym = tid.getName();
            if (proofscope.containsVisibleModule(qsym)) {
                ModuleScope mod = proofscope.getVisibleModule(qsym);
                if (mod.containsType(nsym)) {
                    TypeEntry entry = mod.getType(nsym);
                    if (mathTypesOnly) {
                        if (isMathType(entry)) {
                            return entry;
                        }
                    }
                    else {
                        return entry;
                    }
                }
            }
            return locateMSMathType(tid);
        }
        else {
            List<TypeEntry> entries = new List<TypeEntry>();
            if (proofscope.containsType(tid.getName())) {
                entries.add(proofscope.getType(tid.getName()));
            }
            if (entries.size() == 0) {
                entries.addAll(getTypeEntries(tid.getName(), proofscope
                        .getVisibleModules()));
            }
            if (entries.size() == 0) {
                return locateMSMathType(tid);
            }
            else if (entries.size() > 1) {
                List<Location> locs = getLocationList(entries);
                String msg =
                        ambigRefMessage(tid.getName().toString(), locs
                                .toString());
                throw new SymbolSearchException(msg);
            }
            else { // exactly one entry!
                TypeEntry entry = entries.get(0);
                checkParamSizeEqual(tid, entry.getType(), entry.getLocation());
                if (mathTypesOnly) {
                    if (isMathType(entry)) {
                        return entry;
                    }
                    else {
                        String msg =
                                cantFindMathTypeMessage(tid.getName().getName());
                        throw new SymbolSearchException(msg);
                    }
                }
                else {
                    return entry;
                }
            }
        }
    }

    private TypeEntry locatePSProgramType(TypeID tid)
            throws SymbolSearchException {
        if (tid.getQualifier() != null) {
            Symbol qsym = tid.getQualifier();
            Symbol nsym = tid.getName();
            if (procscope.containsVisibleModule(qsym)) {
                ModuleScope mod = procscope.getVisibleModule(qsym);
                if (mod.containsType(nsym)) {
                    TypeEntry entry = mod.getType(nsym);
                    checkProgramType(tid, entry.getType());
                    return entry;
                }
            }
            else {
                return locateMSProgramType(tid);
            }
        }
        else {
            List<TypeEntry> entries =
                    getTypeEntries(tid.getName(), procscope.getVisibleModules());
            if (entries.size() == 0) {
                if (modscope.containsLocalType(tid.getName())) {
                    TypeEntry entry = modscope.getLocalType(tid.getName());
                    checkProgramType(tid, entry.getType());
                    return entry;
                }
                else {
                    return locateMSProgramType(tid);
                }
            }
            else if (entries.size() > 1) {
                List<Location> locs = getLocationList(entries);
                String msg =
                        ambigRefMessage(tid.getName().toString(), locs
                                .toString());
                throw new SymbolSearchException(msg);
            }
            else { // exactly one entry!
                TypeEntry entry = entries.get(0);
                checkProgramType(tid, entry.getType());
                return entry;
            }
        }
        assert false : "Could not locatePSProgramType";
        return null;
    }

    private TypeEntry locateMSMathType(TypeID tid) throws SymbolSearchException {
        if (tid.getQualifier() != null) {
            return locateMSQualMathType(tid);
        }
        else {
            return locateMSUnqualMathType(tid);
        }
    }

    public TypeEntry locateMSProgramType(TypeID tid)
            throws SymbolSearchException {
        if (tid.getParamCount() > 0) {
            String msg = cantUsePrimInProgMessage(tid.getName().toString());
            throw new SymbolSearchException(msg);
        }
        TypeEntry pentry = null;
        if (tid.getQualifier() != null) {
            pentry = locateMSQualProgramType(tid);
        }
        else {
            pentry = locateMSUnqualProgramType(tid);
        }
        if (pentry != null)
            checkProgramType(tid, pentry.getType());
        return pentry;
    }

    private TypeEntry locateMSQualProgramType(TypeID tid)
            throws SymbolSearchException {
        Symbol qual = tid.getQualifier();
        Symbol name = tid.getName();
        if (modscope.isProgramVisible(qual)) {
            ModuleScope mod = modscope.getProgramVisibleModule(qual);
            if (mod.containsLocalType(name)) {
                return mod.getLocalType(name);
            }
            else {
                String msg =
                        cantFindProgTypeInModMessage(name.toString(), mod
                                .toString());
                throw new SymbolSearchException(msg);
            }
        }
        else {
            String msg = modNotProgVisibleMessage(qual.toString());
            throw new SymbolSearchException(msg);
        }
    }

    private TypeEntry locateMSQualMathType(TypeID tid)
            throws SymbolSearchException {
        Symbol qual = tid.getQualifier();
        Symbol name = tid.getName();
        if (modscope.isMathVisible(qual)) {
            ModuleScope mod = modscope.getMathVisibleModule(qual);
            if (mod.containsLocalType(name)) {
                TypeEntry entry = mod.getLocalType(name);
                checkParamSizeEqual(tid, entry.getType(), entry.getLocation());
                if (mathTypesOnly) {
                    if (isMathType(entry)) {
                        return entry;
                    }
                    else {
                        String msg = cantFindMathTypeMessage(name.toString());
                        throw new SymbolSearchException(msg);
                    }
                }
                else {
                    return entry;
                }
            }
            else {
                String msg =
                        cantFindTypeInModMessage(name.toString(), mod
                                .toString());
                throw new SymbolSearchException(msg);
            }
        }
        else {
            String msg = modNotVisibleMessage(qual.toString());
            throw new SymbolSearchException(msg);
        }
    }

    private void checkParamSizeEqual(TypeID tid, Type type, Location eloc)
            throws SymbolSearchException {
        int paramCount = 0;
        if (type instanceof PrimitiveType) {
            paramCount = ((PrimitiveType) type).paramCount();
        }
        if (tid.getParamCount() != paramCount) {
            String msg =
                    paramMismatchMessage(tid.getName().toString(), tid
                            .getParamCount(), eloc.toString(), paramCount);
            throw new SymbolSearchException(msg);
        }
    }

    private TypeEntry locateMSUnqualProgramType(TypeID tid)
            throws SymbolSearchException {
        TypeEntry typeEntry = locateTypeEntryInAssociates(modscope, tid);
        if (typeEntry != null) {
            return typeEntry;
        }
        if (modscope.containsType(tid.getName())) {
            TypeEntry entry = modscope.getType(tid.getName());
            checkParamSizeEqual(tid, entry.getType(), entry.getLocation());
            return entry;
        }
        List<TypeEntry> entries =
                getTypeEntries(tid.getName(), modscope
                        .getProgramVisibleModules());

        if (entries.size() == 0) {
            String msg = cantFindProgTypeMessage(tid.getName().toString());
            throw new SymbolSearchException(msg);
        }
        else if (entries.size() > 1) {
            List<Location> locs = getLocationList(entries);
            String msg =
                    ambigRefMessage(tid.getName().toString(), locs.toString());
            throw new SymbolSearchException(msg);
        }
        else { // exactly one entry!
            typeEntry = entries.get(0);
            return typeEntry;
        }
    }

    private List<TypeEntry> getTypeEntries(Symbol name, Iterator<ModuleScope> i) {
        List<TypeEntry> entries = new List<TypeEntry>();
        while (i.hasNext()) {
            ModuleScope mod = i.next();
            if (mod.containsLocalType(name)) {
                entries.add(mod.getLocalType(name));
            }
        }
        return entries;
    }

    private List<Location> getLocationList(List<TypeEntry> entries) {
        List<Location> locs = new List<Location>();
        Iterator<TypeEntry> i = entries.iterator();
        while (i.hasNext()) {
            TypeEntry entry = i.next();
            locs.add(entry.getLocation());
        }
        return locs;
    }

    private TypeEntry locateTypeEntryInAssociates(ModuleScope mscope, TypeID tid) {
        Iterator<ModuleID> i = mscope.getSpecIterator();
        while (i.hasNext()) {
            ModuleID next = i.next();
            ModuleScope mod = myInstanceEnvironment.getModuleScope(next);
            if (mod.containsLocalNonConcType(tid.getName())) {
                return mod.getLocalType(tid.getName());
            }
        }
        Iterator<ModuleID> j = mscope.getAssociateIterator();
        while (j.hasNext()) {
            ModuleScope mod = myInstanceEnvironment.getModuleScope(j.next());
            if (mod.containsLocalType(tid.getName())) {
                return mod.getLocalType(tid.getName());
            }
        }
        return null;
    }

    private TypeEntry locateMSUnqualMathType(TypeID tid)
            throws SymbolSearchException {
        if (tid.getName().getName().equals("D")) {
            System.out.println("HELLO!");
        }

        TypeEntry typeEntry;
        if (mathTypesOnly) {
            typeEntry = locateConcTypeInAssociates(modscope, tid);
        }
        else {
            typeEntry = locateTypeInAssociates(modscope, tid);
        }

        if (typeEntry != null) {
            if (mathTypesOnly) {
                if (isMathType(typeEntry)) {
                    return typeEntry;
                }
            }
            else {
                return typeEntry;
            }
        }
        if (modscope.containsType(tid.getName())) {
            TypeEntry entry = modscope.getType(tid.getName());
            checkParamSizeEqual(tid, entry.getType(), entry.getLocation());
            if (mathTypesOnly) {
                if (isMathType(entry)) {
                    return entry;
                }
            }
            else {
                return entry;
            }
        }
        List<TypeEntry> entries =
                getTypeEntries(tid.getName(), modscope.getMathVisibleModules());
        if (entries.size() == 0) {
            String msg = cantFindTypeMessage(tid.getName().toString());
            throw new SymbolSearchException(msg);
        }
        else if (entries.size() > 1) {
            List<Location> locs = getLocationList(entries);
            String msg =
                    ambigRefMessage(tid.getName().toString(), locs.toString());
            throw new SymbolSearchException(msg);
        }
        else { // exactly one entry!
            TypeEntry entry = entries.get(0);
            checkParamSizeEqual(tid, entry.getType(), entry.getLocation());
            if (mathTypesOnly) {
                if (isMathType(entry)) {
                    return entry;
                }
                else {
                    String msg =
                            cantFindMathTypeMessage(tid.getName().getName());
                    throw new SymbolSearchException(msg);
                }
            }
            else {
                return entry;
            }
        }
    }

    private TypeEntry locateConcTypeInAssociates(ModuleScope mscope, TypeID tid) {
        Iterator<ModuleID> i = mscope.getSpecIterator();
        while (i.hasNext()) {
            ModuleScope mod = myInstanceEnvironment.getModuleScope(i.next());
            if (mod.containsLocalConcType(tid.getName())) {
                return (mod.getLocalType(tid.getName()));
            }
        }
        Iterator<ModuleID> j = mscope.getAssociateIterator();
        while (j.hasNext()) {
            ModuleScope mod = myInstanceEnvironment.getModuleScope(j.next());
            if (mod.containsLocalConcType(tid.getName())) {
                return (mod.getLocalType(tid.getName()));
            }
        }
        return null;
    }

    private TypeEntry locateTypeInAssociates(ModuleScope mscope, TypeID tid) {
        Iterator<ModuleID> i = mscope.getSpecIterator();
        while (i.hasNext()) {
            ModuleScope mod = myInstanceEnvironment.getModuleScope(i.next());
            if (mod.containsLocalNonConcType(tid.getName())) {
                return mod.getLocalType(tid.getName());
            }
        }
        Iterator<ModuleID> j = mscope.getAssociateIterator();
        while (j.hasNext()) {
            ModuleScope mod = myInstanceEnvironment.getModuleScope(j.next());
            if (mod.containsLocalType(tid.getName())) {
                return mod.getLocalType(tid.getName());
            }
        }
        return null;
    }

    private void checkProgramType(TypeID tid, Type type)
            throws SymbolSearchException {
        Type tmptype = type;
        if (type instanceof IndirectType) {
            tmptype = ((IndirectType) type).getType();
        }
        if (!(tmptype instanceof ConcType || tmptype instanceof NameType
                || tmptype instanceof FormalType
                || tmptype instanceof ArrayType || tmptype instanceof RecordType)) {
            String msg = cantFindProgTypeMessage(tid.getName().toString());
            throw new SymbolSearchException(msg);
        }
    }

    private boolean isMathType(TypeEntry te) {
        if (te.getExemplar() != null) {
            return true;
        }
        return false;
    }

    // -----------------------------------------------------------
    // Error Related Methods
    // -----------------------------------------------------------

    private String cantUsePrimInProgMessage(String name) {
        return "Cannot use parameterized type " + name + " in program context.";
    }

    private String paramMismatchMessage(String name, int n, String loc, int m) {
        return "Type " + name + " with " + n
                + " arguments has the same name as a type declared at " + loc
                + " that takes " + m + " parameters.";
    }

    private String cantFindProgTypeInModMessage(String name, String module) {
        return "Cannot find a program type named " + name + " in module "
                + module + ".";
    }

    private String cantFindTypeInModMessage(String name, String module) {
        return "Cannot find a type named " + name + " in module " + module
                + ".";
    }

    private String modNotProgVisibleMessage(String qual) {
        return "The qualifier " + qual + " does not correspond to any "
                + "program modules visible from this scope.";
    }

    private String modNotVisibleMessage(String qual) {
        return "The qualifier " + qual + " does not correspond to any "
                + "modules visible from this scope.";
    }

    private String ambigRefMessage(String name, String mods) {
        return "The type named " + name + " is found in more than one "
                + "module visible from this scope: " + mods + ".";
    }

    private String cantFindProgTypeMessage(String name) {
        return "Cannot find a program type named " + name + ".";
    }

    private String cantFindMathTypeMessage(String name) {
        return "Cannot find a math type named " + name + ".";
    }

    private String cantFindTypeMessage(String name) {
        return "Cannot find a type named " + name + ".";
    }
}
