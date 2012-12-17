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
 * ModuleScope.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.scope;

import java.util.Arrays;
import java.util.Set;

import edu.clemson.cs.r2jt.absyn.FacilityDec;
import edu.clemson.cs.r2jt.absyn.MathVarDec;
import edu.clemson.cs.r2jt.analysis.TypeCorrespondence;
import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.collections.Map;
import edu.clemson.cs.r2jt.data.ModuleID;
import edu.clemson.cs.r2jt.data.ModuleKind;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.entry.*;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.init.Environment;
import edu.clemson.cs.r2jt.population.InstantiationException;
import edu.clemson.cs.r2jt.type.*;

public class ModuleScope extends Scope {

    // ===========================================================
    // Variables
    // ===========================================================

    //private Environment env = Environment.getInstance();
	private CompileEnvironment myInstanceEnvironment;

    private ErrorHandler err;

    private ScopeID sid = null;

    /* remains null for everything but short facilities and local short
     * facilities.
     */
    private FacilityDec fdec = null;

    private List<Entry> params = new List<Entry>();

    private List<ModuleID> specs = new List<ModuleID>();
    private List<ModuleID> associates = new List<ModuleID>();

    private Map<Symbol, ModuleEntry> progModules
        = new Map<Symbol, ModuleEntry>();
    private Map<Symbol, ModuleEntry> mathModules
        = new Map<Symbol, ModuleEntry>();

    private Map<Symbol, TheoremEntry> theorems
        = new Map<Symbol, TheoremEntry>();
    private Map<Symbol, ProofEntry> proofs
        = new Map<Symbol, ProofEntry>();
    private Map<Symbol, DefinitionEntry> definitions
        = new Map<Symbol, DefinitionEntry>();
    private Map<Symbol, VarEntry> variables
        = new Map<Symbol, VarEntry>();
    private Map<Symbol, OperationEntry> operations
        = new Map<Symbol, OperationEntry>();
    private Map<Symbol, TypeEntry> types
        = new Map<Symbol, TypeEntry>();
    
    private List<TypeCorrespondence> typeCorrespondences
        = new List<TypeCorrespondence>();
    
    private List<MathVarDec> alternateVarTypes
        = new List<MathVarDec>();

    private Binding binding = null;

    private Binding facbind = null; // only used in short facilities

    private TypeHolder holder = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ModuleScope(ModuleID id, CompileEnvironment instanceEnvironment) {
    	myInstanceEnvironment = instanceEnvironment;
        sid = ScopeID.createModuleScopeID(id);
        List<ModuleID> theories = myInstanceEnvironment.getTheories(id);
        Iterator<ModuleID> i = theories.iterator();
        while (i.hasNext()) {
            ModuleID id2 = i.next();
            assert !mathModules.containsKey(id.getName());
            addMathVisible(id2);
        }
        this.err = myInstanceEnvironment.getErrorHandler();
        binding = new Binding(this, myInstanceEnvironment);
        holder = new TypeHolder(this, myInstanceEnvironment);
    }

    public ModuleScope(FacilityDec dec, CompileEnvironment instanceEnvironment) {
    	myInstanceEnvironment = instanceEnvironment;
    	ModuleID mid = myInstanceEnvironment.getModuleID(dec.getName().getFile());
        sid = ScopeID.createFacilityScopeID(dec.getName(), mid);
        fdec = dec;
        this.err = myInstanceEnvironment.getErrorHandler();
        binding = new Binding(this, myInstanceEnvironment);
        holder = new TypeHolder(this, myInstanceEnvironment);
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    public ScopeID getScopeID() { return sid; }

    public FacilityDec getFacilityDec() { return fdec; }

    public ModuleID getModuleID() { return sid.getModuleID(); }
    
    public Map<Symbol, TypeEntry> getTypes() { return types; }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Module Parameter Methods
    // -----------------------------------------------------------

    /** Adds the specified module parameter to the module scope. */
    public void addModuleParameter(Entry entry) {
        params.add(entry);
    }

    /** Returns an iterator of module parameters. */
    public List<Entry> getModuleParameters() {
        return params;
    }
    
    public List<MathVarDec> getAlternateVarTypes() {
    	 return alternateVarTypes;
    }

    // -----------------------------------------------------------
    // Spec and Associate Methods
    // -----------------------------------------------------------

    /*
     * Specs are the modules that a body directly implements.
     * For an unbundled concept implementation this would be
     * the concept. For an enhancement implementation this would
     * be the enhancement. For a bundled implementation this would
     * be the concept and any additional enhancements.
     *
     * Associates are modules whose scopes are conceptually integrated
     * into the current module scope. For a body this includes its
     * specs and all associated concept and enhancement modules. For
     * an enhancement this includes its associated concept.
     */

    /** Adds the specified spec id to the module scope. */
    public void addSpec(ModuleID id) {
        specs.add(id);
    }

    /** Adds the specified associate id to the module scope. */
    public void addAssociate(ModuleID id) {
        associates.add(id);
    }

    public Iterator<ModuleID> getSpecIterator() {
        return specs.iterator();
    }

    public Iterator<ModuleID> getAssociateIterator() {
        return associates.iterator();
    }

    // -----------------------------------------------------------
    // Methods Related to Imported Modules
    // -----------------------------------------------------------

    /** Adds the specified uses item to the module scope. */ 
    public void addUsesItem(ModuleID id) {
        assert (id.getModuleKind() == ModuleKind.THEORY ||
                id.getModuleKind() == ModuleKind.FACILITY);
        addMathVisible(id);
        if (id.getModuleKind() == ModuleKind.FACILITY) {
            addProgramVisible(id);
        }
    }

    /** Adds the specified facility declaration to the module scope. */
    public void addFacility(ModuleEntry entry) {
        mathModules.put(entry.getSymbol(), entry);
        progModules.put(entry.getSymbol(), entry);
    }

    /** Returns an iterator of all import modules visible in a math
     *  context.
     */
    public Iterator<ModuleScope> getMathVisibleModules() {
        List<ModuleScope> modules = new List<ModuleScope>();
        Iterator<Symbol> i = mathModules.keyIterator();
        while (i.hasNext()) {
            Symbol sym = i.next();
            modules.add(mathModules.get(sym).getModuleScope());
        }
        return modules.iterator();
    }

    /** Returns an iterator of all import modules visible in a
     *  programming context.
     */
    public Iterator<ModuleScope> getProgramVisibleModules() {
        List<ModuleScope> modules = new List<ModuleScope>();
        Iterator<Symbol> i = progModules.keyIterator();
        while (i.hasNext()) {
            Symbol sym = i.next();
            modules.add(mathModules.get(sym).getModuleScope());
        }
        return modules.iterator();
    }

    public boolean isMathVisible(Symbol qualifier) {
        return mathModules.containsKey(qualifier);
    }

    public boolean isProgramVisible(Symbol qualifier) {
        return progModules.containsKey(qualifier);
    }

    public ModuleScope getMathVisibleModule(Symbol qualifier) {
        return mathModules.get(qualifier).getModuleScope();
    }

    public ModuleScope getProgramVisibleModule(Symbol qualifier) {
        return progModules.get(qualifier).getModuleScope();
    }

    public void addAssocVisibleModules() {
        addVisibleModulesFrom(specs);
        addVisibleModulesFrom(associates);
    }

    private void addVisibleModulesFrom(List<ModuleID> mods) {
        Iterator<ModuleID> i = mods.iterator();
        while (i.hasNext()) {
            ModuleScope scope = myInstanceEnvironment.getModuleScope(i.next());
            Iterator<ModuleScope> j = scope.getProgramVisibleModules();
            while (j.hasNext()) {
                ModuleID id = j.next().getModuleID();
                addProgramVisible(id);
                addMathVisible(id);
            }
        }
    }

    public void addParameter(VarEntry entry) {
        params.add(entry);
    }
    
    // -----------------------------------------------------------
    // Population Methods
    // -----------------------------------------------------------

    public boolean addPermitted(Symbol sym) {
        if (mathModules.containsKey(sym)) {
            return false;
        }
        if (containsLocal(sym)) {
            return false;
        }
        Iterator<ModuleID> i = specs.iterator();
        while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (  scope.containsLocalVariable(sym) ||
                  scope.containsLocalNonConcType(sym)) {
                return false;
            }
        }
        Iterator<ModuleID> j = associates.iterator();
        while (j.hasNext()) {
            ModuleID id = j.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocal(sym)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean addDefinitionTypePermitted(Symbol sym) {
    	if (mathModules.containsKey(sym)) {
            return false;
        }
        if (containsLocalType(sym)) {
            return false;
        }
        Iterator<ModuleID> i = specs.iterator();
        while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalNonConcType(sym)) {
                return false;
            }
        }
        Iterator<ModuleID> j = associates.iterator();
        while (j.hasNext()) {
            ModuleID id = j.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalType(sym)) {
                return false;
            }
        }
        return true;
    }

    public Entry getAddObstructor(Symbol sym) {
        if (mathModules.containsKey(sym)) {
            return mathModules.get(sym);
        }
        if (containsLocal(sym)) {
            return getLocal(sym);
        }
        Iterator<ModuleID> i = specs.iterator();
        while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (  scope.containsLocalVariable(sym) ||
                  scope.containsLocalNonConcType(sym)) {
                return scope.getLocal(sym);
            }
        }
        Iterator<ModuleID> j = associates.iterator();
        while (j.hasNext()) {
            ModuleID id = j.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocal(sym)) {
                return scope.getLocal(sym);
            }
        }
        assert false : "getAddObstructor failed";
        return null;
    }

    /** Adds a math assertion to the module scope. */
    public void addTheorem(TheoremEntry entry) {
        theorems.put(entry.getSymbol(), entry);
    }
    
    public void addProof(ProofEntry entry) {
    	proofs.put(entry.getSymbol(), entry);
    }
    
    /** Adds a math definition to the module scope. */
    public void addDefinition(DefinitionEntry entry) {
    	definitions.put(entry.getSymbol(), entry);
    }

    /** Adds a variable to the module scope. */
    public void addVariable(VarEntry entry) {
        variables.put(entry.getSymbol(), entry);
    }

    /** Adds an operation to the module scope. */
    public void addOperation(OperationEntry entry) {
        operations.put(entry.getSymbol(), entry);
    }

    /** Adds a type to the module scope. */
    public void addType(TypeEntry entry) {
        types.put(entry.getName().getSymbol(), entry);
    }
    
    public void addTypeCorrespondence(Type t1, Type t2) {
    	TypeCorrespondence newTc = new TypeCorrespondence(t1, t2);
    	typeCorrespondences.add(newTc);
    }
    
    public List<TypeCorrespondence> getTypeCorrespondences() {
    	return typeCorrespondences;
    }
    
    public void addAlternateVarType(MathVarDec dec) {
    	alternateVarTypes.add(dec);
    }

    // -----------------------------------------------------------
    // Location Methods
    // -----------------------------------------------------------

    public boolean containsVariable(Symbol sym) {
        if (containsLocalVariable(sym)) {
            return true;
        }
        Iterator<ModuleID> i = specs.iterator();
        while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalVariable(sym)) {
                return true;
            }
        }
        Iterator<ModuleID> j = associates.iterator();
        while (j.hasNext()) {
            ModuleID id = j.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalVariable(sym)) {
                return true;
            }
        }
        return false;
    }

    public VarEntry getVariable(Symbol sym) {
        if (containsLocalVariable(sym)) {
            return getLocalVariable(sym);
        }
        Iterator<ModuleID> i = specs.iterator();
        while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalVariable(sym)) {
                return scope.getLocalVariable(sym);
            }
        }
        Iterator<ModuleID> j = associates.iterator();
        while (j.hasNext()) {
            ModuleID id = j.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalVariable(sym)) {
                return scope.getLocalVariable(sym);
            }
        }
        assert false : "getVariable failed";
        return null;
    }
   
    public boolean containsTheorem(Symbol sym) {
    	if(containsLocalTheorem(sym)) {
    		return true;
    	}
    	Iterator<ModuleID> i = associates.iterator();
    	while(i.hasNext()) {
    		ModuleID id = i.next();
    		ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
    		if(scope.containsLocalTheorem(sym)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public TheoremEntry getTheorem(Symbol sym) {
    	if(containsLocalTheorem(sym)) {
    		return getLocalTheorem(sym);
    	}
    	Iterator<ModuleID> i = associates.iterator();
    	while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalTheorem(sym)) {
                return scope.getLocalTheorem(sym);
            }
        }
        assert false : "getDefinition failed";
        return null;
    }
    
    public boolean containsProof(Symbol sym) {
    	if(containsLocalProof(sym)) {
    		return true;
    	}
    	Iterator<ModuleID> i = associates.iterator();
    	while(i.hasNext()) {
    		ModuleID id = i.next();
    		ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
    		if(scope.containsLocalProof(sym)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public ProofEntry getProof(Symbol sym) {
    	if(containsLocalProof(sym)) {
    		return getLocalProof(sym);
    	}
    	Iterator<ModuleID> i = associates.iterator();
    	while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalProof(sym)) {
                return scope.getLocalProof(sym);
            }
        }
        assert false : "getDefinition failed";
        return null;
    }
    
    public boolean containsDefinition(Symbol sym) {
    	if(containsLocalDefinition(sym)) {
    		return true;
    	}
    	Iterator<ModuleID> i = associates.iterator();
    	while(i.hasNext()) {
    		ModuleID id = i.next();
    		ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
    		if(scope.containsLocalDefinition(sym)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public DefinitionEntry getDefinition(Symbol sym) {
    	if(containsLocalDefinition(sym)) {
    		return getLocalDefinition(sym);
    	}
    	Iterator<ModuleID> i = associates.iterator();
        while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalDefinition(sym)) {
                return scope.getLocalDefinition(sym);
            }
        }
        assert false : "getDefinition failed";
        return null;
    }

    public boolean containsOperation(Symbol sym) {
        if (containsLocalOperation(sym)) {
            return true;
        }
        Iterator<ModuleID> i = associates.iterator();
        while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalOperation(sym)) {
                return true;
            }
        }
        return false;
    }

    public OperationEntry getOperation(Symbol sym) {
        if (containsLocalOperation(sym)) {
            return getLocalOperation(sym);
        }
        Iterator<ModuleID> i = associates.iterator();
        while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalOperation(sym)) {
                return scope.getLocalOperation(sym);
            }
        }
        assert false : "getOperation failed";
        return null;
    }

    public boolean containsType(Symbol sym) {
        if (containsLocalType(sym)) {
            return true;
        }
        Iterator<ModuleID> i = specs.iterator();
        while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalNonConcType(sym)) {
                return true;
            }
        }
        Iterator<ModuleID> j = associates.iterator();
        while (j.hasNext()) {
            ModuleID id = j.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalType(sym)) {
                return true;
            }
        }
        return false;
    }

    public TypeEntry getType(Symbol sym) {
        if (containsLocalType(sym)) {
            return getLocalType(sym);
        }
        Iterator<ModuleID> i = associates.iterator();
        while (i.hasNext()) {
            ModuleID id = i.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalNonConcType(sym)) {
                return scope.getLocalType(sym);
            }
        }
        Iterator<ModuleID> j = associates.iterator();
        while (j.hasNext()) {
            ModuleID id = j.next();
            ModuleScope scope = myInstanceEnvironment.getSymbolTable(id).getModuleScope();
            if (scope.containsLocalType(sym)) {
                return scope.getLocalType(sym);
            }
        }
        assert false : "getType failed";
        return null;
    }

    // -----------------------------------------------------------
    // Local contains and get methods
    // -----------------------------------------------------------

    public boolean containsLocal(Symbol sym) {
        return (  theorems.containsKey(sym) ||
        		  definitions.containsKey(sym) ||
        		  proofs.containsKey(sym) ||
                  variables.containsKey(sym) ||
                  operations.containsKey(sym) ||
                  types.containsKey(sym));
    }

    public Entry getLocal(Symbol sym) {
        if (theorems.containsKey(sym)) {
            return theorems.get(sym);
        } else if (definitions.containsKey(sym)) {
        	return definitions.get(sym);
        } else if (proofs.containsKey(sym)) {
        	return proofs.get(sym);
        } else if (variables.containsKey(sym)) {
            return variables.get(sym);
        } else if (operations.containsKey(sym)) {
            return operations.get(sym);
        } else if (types.containsKey(sym)) {
            return types.get(sym);
        } else {
            assert false : "getLocal failed";
            return null;
        }
    }

    public boolean containsLocalProof(Symbol sym) {
    	return (proofs.containsKey(sym));
    }
    
    public ProofEntry getLocalProof(Symbol sym) {
    	return proofs.get(sym);
    }
    
    public boolean containsLocalTheorem(Symbol sym) {
    	return (theorems.containsKey(sym));
    }
    
    public TheoremEntry getLocalTheorem(Symbol sym) {
    	return theorems.get(sym);
    }
    
    /**
     * <p>Returns a <code>List</code> of the names of all the theorems defined 
     * locally in this module, in lexical order.</p>
     * 
     * @return The names, as <code>Symbol</code>s, of each theorem defined
     *         locally in this module in lexical order.
     */
    public List<Symbol> getLocalTheoremNames() {
    	List<Symbol> retval = new List<Symbol>();
    	
    	Set<Symbol> keys = theorems.keySet();
    	Symbol[] alphabeticalKeys = new Symbol[keys.size()];
    	keys.toArray(alphabeticalKeys);
    	Arrays.sort(alphabeticalKeys);
    	
    	for (Symbol s : alphabeticalKeys) {
    		retval.add(s);
    	}
    	
    	return retval;
    }
    
    public boolean containsLocalVariable(Symbol sym) {
        return (variables.containsKey(sym));
    }

    public VarEntry getLocalVariable(Symbol sym) {
        return variables.get(sym);
    }
    
    public boolean containsLocalDefinition(Symbol sym) {
    	return (definitions.containsKey(sym));
    }
    
    public DefinitionEntry getLocalDefinition(Symbol sym) {
    	return definitions.get(sym);
    }

    public boolean containsLocalOperation(Symbol sym) {
        return (operations.containsKey(sym));
    }

    public List<Symbol> getLocalOperationNames() {
    	List<Symbol> retval = new List<Symbol>();
    	
    	Set<Symbol> keys = operations.keySet();
    	Symbol[] alphabeticalKeys = new Symbol[keys.size()];
    	keys.toArray(alphabeticalKeys);
    	Arrays.sort(alphabeticalKeys);
    	
    	for (Symbol s : alphabeticalKeys) {
    		retval.add(s);
    	}
    	
    	return retval;
    }
    
    public OperationEntry getLocalOperation(Symbol sym) {
        return operations.get(sym);
    }

    public boolean containsLocalType(Symbol sym) {
        return (types.containsKey(sym));
    }

    public boolean containsLocalConcType(Symbol sym) {
        return (types.containsKey(sym) &&
                types.get(sym).getType() instanceof ConcType);
    }

    public boolean containsLocalNonConcType(Symbol sym) {
        return (types.containsKey(sym) &&
                !(types.get(sym).getType() instanceof ConcType));
    }

    public TypeEntry getLocalType(Symbol sym) {
        return types.get(sym);
    }

    // -----------------------------------------------------------
    // Binding Accessors
    // -----------------------------------------------------------

    /** Returns a handle to this scope's binding. */
    public Binding getBinding() { return binding; }

    public TypeHolder getTypeHolder() { return holder; }

    public void setFacbind(Binding facbind) {
        assert this.facbind == null :
                "this.facbid != null: " + this.facbind;
        this.facbind = facbind;
    }

    // -----------------------------------------------------------
    // Instantiation Methods
    // -----------------------------------------------------------

    public void merge(List<ModuleScope> scopes)
        throws InstantiationException
    {
        Iterator<ModuleScope> i = scopes.iterator();
        while (i.hasNext()) {
            ModuleScope scope = i.next();
            addVariablesFromScope(scope);
            addDefinitionsFromScope(scope);
            addOperationsFromScope(scope);
            addTypesFromScope(scope);
            binding.addBindingsFromScope(scope);
        }
    }

    /**
     * Merges the facility dec created by a short facility
     * into the short facility. It does not merge the binding.
     */
    public void mergeFacility(ModuleScope scope) {
        try {
            addVariablesFromScope(scope);
            addDefinitionsFromScope(scope);
            addOperationsFromScope(scope);
            addTypesFromScope(scope);
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    public void removeFacilityFromVisible(Symbol facility) {
        if (progModules.containsKey(facility)) {
            progModules.remove(facility);
        }
        if (mathModules.containsKey(facility)) {
            mathModules.remove(facility);
        }
    }

    public ModuleScope instantiate(FacilityDec fdec,
                           Map<Symbol, Type> typeMap, Binding replBind) {
        PosSymbol facility = fdec.getName();
        ModuleScope newscope = new ModuleScope(fdec, myInstanceEnvironment);
        Iterator<Symbol> i = variables.keyIterator();
        while (i.hasNext()) {
            Symbol sym = i.next();
            VarEntry newentry
                = variables.get(sym).instantiate(sid, replBind);
            newscope.variables.put(sym, newentry);
        }
        Iterator<Symbol> j = operations.keyIterator();
        while (j.hasNext()) {
            Symbol sym = j.next();
            OperationEntry newentry
                = operations.get(sym).instantiate(sid, replBind);
            newscope.operations.put(sym, newentry);
        }
        Iterator<Symbol> m = definitions.keyIterator();
        while(m.hasNext()) {
        	Symbol sym = m.next();
        	DefinitionEntry newentry
        	    = definitions.get(sym).instantiate(sid, replBind);
        	newscope.definitions.put(sym, newentry);
        }
        Iterator<Symbol> k = types.keyIterator();
        while (k.hasNext()) {
            Symbol sym = k.next();
            Type type = types.get(sym).getType();
            if (type instanceof FormalType) { continue; }
            Type newtype
                = type.instantiate(sid, replBind);
            if (type instanceof ConcType) {
                newtype = getNameType(newtype, facility);
            }
            TypeEntry newentry = new TypeEntry(replBind.getScope(),
                                               types.get(sym).getName(),
                                               newtype);
            newscope.types.put(sym, newentry);
        }
        newscope.binding = binding.instantiate(facility, newscope, replBind,
                                               typeMap);
        return newscope;
    }

    public void simplifyNames(PosSymbol name) {
        sid = ScopeID.createShortScopeID(sid.getModuleID());
        Iterator<Symbol> i = types.keyIterator();
        while (i.hasNext()) {
            Symbol sym = i.next();
            if (types.get(sym).getType() instanceof NameType) {
                NameType nametype = (NameType)types.get(sym).getType();
                NameType newtype = new NameType(nametype.getModuleID(),
                                   nametype.getName(), nametype.getType());

                TypeEntry newentry = new TypeEntry(binding.getScope(),
                                                   types.get(sym).getName(),
                                                   newtype);
                types.put(sym, newentry);
            }
        }
        binding.simplifyNames(name);
    }

    // -----------------------------------------------------------
    // To String Method
    // -----------------------------------------------------------

    /** Returns a string representation of the module scope. */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("==================================================\n");
        sb.append("Module Scope for " + sid.toString());
        sb.append("\n");
        sb.append("==================================================\n\n");
        sb.append("Module Parameters: " + params.toString() + "\n");
        sb.append("Specification Modules: " + specs.toString() + "\n");
        sb.append("Associate Modules: " + associates.toString() + "\n\n");
        sb.append("---------- Program Visible Modules ---------------\n");
        sb.append(progModules.toString() + "\n");
        sb.append("---------- Math Visible Modules ------------------\n");
        sb.append(mathModules.toString() + "\n");
        sb.append("---------- Math Assertions -----------------------\n");
        sb.append(theorems.toString() + "\n");
        sb.append("---------- Math Definitions ----------------------\n");
        sb.append(definitions.toString() + "\n");
        sb.append("---------- Variables -----------------------------\n");
        sb.append(variables.toString() + "\n");
        sb.append("---------- Operations ----------------------------\n");
        sb.append(operations.toString() + "\n");
        sb.append("---------- Types ---------------------------------\n");
        sb.append(types.toString() + "\n");
        sb.append(binding.toString());
        if (facbind != null) { sb.append(facbind.toString()); }
        sb.append(holder.toString() + "\n");
        sb.append("----- end module scope ---------------------------\n");
        return sb.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private void addVariablesFromScope(ModuleScope scope)
        throws InstantiationException
    {
        Iterator<Symbol> i = scope.variables.keyIterator();
        while (i.hasNext()) {
            Symbol sym = i.next();
            VarEntry var = scope.getLocalVariable(sym);
            if (this.containsLocal(sym)) {
                String loc1 = this.getLocal(sym).getLocation().toString();
                String loc2 = var.getLocation().toString();
                String msg = cantInstantiateMessage(sym.toString(),
                                                    loc1, loc2);
                throw new InstantiationException(msg);
            } else {
                this.addVariable(var);
            }
        }
    }
    
    private void addDefinitionsFromScope(ModuleScope scope)
        throws InstantiationException
    {
    	Iterator<Symbol> i = scope.definitions.keyIterator();
    	while(i.hasNext()) {
    		Symbol sym = i.next();
    		DefinitionEntry def = scope.getLocalDefinition(sym);
    		if(this.containsLocal(sym)) {
    			String loc1 = this.getLocal(sym).getLocation().toString();
                String loc2 = def.getLocation().toString();
                String msg = cantInstantiateMessage(sym.toString(),
                                                    loc1, loc2);
                throw new InstantiationException(msg);
    		}
    		else {
    			this.addDefinition(def);
    		}
    	}
    }
            
    private void addOperationsFromScope(ModuleScope scope)
        throws InstantiationException
    {
        Iterator<Symbol> i = scope.operations.keyIterator();
        while (i.hasNext()) {
            Symbol sym = i.next();
            OperationEntry oper = scope.getLocalOperation(sym);
            if (this.containsLocal(sym)) {
                String loc1 = this.getLocal(sym).getLocation().toString();
                String loc2 = oper.getLocation().toString();
                String msg = cantInstantiateMessage(sym.toString(),
                                                    loc1, loc2);
                throw new InstantiationException(msg);
            } else {
                this.addOperation(oper);
            }
        }
    }
            
    private void addTypesFromScope(ModuleScope scope)
        throws InstantiationException
    {
        Iterator<Symbol> i = scope.types.keyIterator();
        while (i.hasNext()) {
            Symbol sym = i.next();
            TypeEntry type = scope.getLocalType(sym);
            if (this.containsLocal(sym)) {
                String loc1 = this.getLocal(sym).getLocation().toString();
                String loc2 = type.getLocation().toString();
                String msg = cantInstantiateMessage(sym.toString(),
                                                    loc1, loc2);
                throw new InstantiationException(msg);
            } else {
                this.addType(type);
            }
        }
    }
            
    private void addMathVisible(ModuleID id) {
        Symbol sym = id.getName();
        ModuleEntry entry = new ModuleEntry(id, myInstanceEnvironment);
        if (!mathModules.containsKey(sym)) {
            mathModules.put(sym, entry);
        }
    }

    private void addProgramVisible(ModuleID id) {
        Symbol sym = id.getName();
        ModuleEntry entry = new ModuleEntry(id, myInstanceEnvironment);
        if (!progModules.containsKey(sym)) {
            progModules.put(sym, entry);
        }
    }

    private Type getNameType(Type type, PosSymbol facility) {
        //Environment env = Environment.getInstance();
        ModuleID id = myInstanceEnvironment.getModuleID(facility.getLocation().getFile());
        ConcType conctype = castToConcType(type);
        return new NameType(id, facility, conctype.getName(),
                            conctype.getType());
    }        

    private ConcType castToConcType(Type type) {
        assert type instanceof ConcType;
        return (ConcType) type;
    }

    private static String cantInstantiateMessage(String sym, String loc1,
                                                 String loc2) {
        return "Can't instantiate generic - Symbol " + sym + " has "
            + "duplicate definition at " + loc1 + " and " + loc2;
    }
}
