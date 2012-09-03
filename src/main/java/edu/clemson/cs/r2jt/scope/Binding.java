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
 * Binding.java
 *
 * The Resolve Software Composition Workbench Project
 *
 * Copyright (c) 1999-2005
 * Reusable Software Research Group
 * Department of Computer Science
 * Clemson University
 */

package edu.clemson.cs.r2jt.scope;

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
import edu.clemson.cs.r2jt.location.SymbolSearchException;
import edu.clemson.cs.r2jt.location.TypeLocator;
import edu.clemson.cs.r2jt.type.*;

public class Binding {

    // ===========================================================
    // Variables
    // ===========================================================
	private CompileEnvironment myInstanceEnvironment;
    private ErrorHandler err;

    private Scope scope;

    private List<TypeID> programTypes = new List<TypeID>();

    private Map<TypeID, Type> mathTypes = new Map<TypeID, Type>();

    private Map<TypeID, Location> mathTypeDefinitionLocations = 
    	new Map<TypeID, Location>();

    private Map<TypeID, Location> programTypeDefinitionLocations = 
    	new Map<TypeID, Location>();

    // ===========================================================
    // Constructors
    // ===========================================================

    public Binding(Scope scope, CompileEnvironment instanceEnvironment) {
    	myInstanceEnvironment = instanceEnvironment;
        this.scope = scope;
        this.err = myInstanceEnvironment.getErrorHandler();
    }

    // ===========================================================
    // Accessors
    // ===========================================================

    public Scope getScope() {
        return scope;
    }

    public ScopeID getScopeID() {
        return scope.getScopeID();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Query Methods
    // -----------------------------------------------------------

    /* Uses for debugging. */
    public boolean isEmpty() {
        return mathTypes.isEmpty();
    }

    public boolean contains(PosSymbol name) {
        TypeID tid = new TypeID(null, name, 0);
        return (mathTypes.containsKey(tid));
    }

    public boolean contains(PosSymbol qual, PosSymbol name) {
        TypeID tid = new TypeID(qual, name, 0);
        return (mathTypes.containsKey(tid));
    }

    public boolean contains(PosSymbol name, int params) {
        TypeID tid = new TypeID(null, name, params);
        return (mathTypes.containsKey(tid));
    }

    public boolean contains(PosSymbol qual, PosSymbol name, int params) {
        TypeID tid = new TypeID(qual, name, params);
        return (mathTypes.containsKey(tid));
    }

    // -----------------------------------------------------------
    // Update Methods
    // -----------------------------------------------------------

    public void addProgramIndirectName(PosSymbol qual, PosSymbol name) {
        TypeID tid = new TypeID(qual, name, 0);
        Location loc = (qual != null) ?
            qual.getLocation() : name.getLocation();
        if (!mathTypes.containsKey(tid)) {
            mathTypes.put(tid, new VoidType());
            mathTypeDefinitionLocations.put(tid, loc);
        }
        if (!programTypes.contains(tid)) {
            programTypes.add(tid);
            programTypeDefinitionLocations.put(tid, loc);
        }
    }

    public void addMathIndirectName(PosSymbol qual, PosSymbol name) {
    	TypeID tid = new TypeID(qual, name, 0);
        Location loc = (qual != null) ?
            qual.getLocation() : name.getLocation();
        if (!mathTypes.containsKey(tid)) {
            mathTypes.put(tid, new VoidType());
            mathTypeDefinitionLocations.put(tid, loc);
        }
    }

    public void addConstructedName(PosSymbol qual, PosSymbol name,
                                   int params) {
    	TypeID tid = new TypeID(qual, name, params);
        if (!mathTypes.containsKey(tid)) { mathTypes.put(tid, new VoidType()); }
        Location loc = (qual != null) ?
            qual.getLocation() : name.getLocation();
        mathTypeDefinitionLocations.put(tid, loc);
    }

    public void addRenaming(PosSymbol newname, PosSymbol qual,
                            PosSymbol oldname) {

        TypeID tid = new TypeID(null, newname, 0);
        Type type = new IndirectType(qual, oldname, this);
        if (!mathTypes.containsKey(tid)) { mathTypes.put(tid, type); }
        addMathIndirectName(qual, oldname);
    }

    public void addTypeMapping(TypeEntry entry) {
    	int params = 0;
        if (entry.getType() instanceof PrimitiveType) {
            params = ((PrimitiveType)entry.getType()).paramCount();
        }
        TypeID tid = new TypeID(null, entry.getName(), params);
        if (mathTypes.containsKey(tid)) {
            assert mathTypes.get(tid) instanceof VoidType;
        }
        mathTypes.put(tid, entry.getType());
    }

    // -----------------------------------------------------------
    // Access Methods
    // -----------------------------------------------------------

    public TypeName getProgramName(PosSymbol qual, PosSymbol name) {
        return mathTypes.get(new TypeID(qual, name, 0)).getProgramName();
    }

    /* This method is here for checking postfix expressions. */
    public Type getType(PosSymbol qual, PosSymbol name) {
        return mathTypes.get(new TypeID(qual, name, 0));
    }

    public Type toMath(PosSymbol qual, PosSymbol name) {
        return mathTypes.get(new TypeID(qual, name, 0)).toMath();
    }

    public Symbol getQualifier(PosSymbol name, int params) {
        TypeID tid = new TypeID(null, name, params);
        PrimitiveType type = castToPrimitiveType(mathTypes.get(tid));
        //if(type == null) look in the imports & add to the current binding?
        if(type == null) {
        	TypeLocator tloc = new TypeLocator(scope, myInstanceEnvironment);
        	try {
        		TypeEntry te = tloc.locateMathType(tid);
        		return te.getScope().getScopeID().getModuleID().getName();
         	}
        	catch(SymbolSearchException ex1) {
        		try {
            		TypeEntry te = tloc.locateProgramType(tid);
            		return te.getScope().getScopeID().getModuleID().getName();
        		}
        	    catch(SymbolSearchException ex2) {
        	        if(type == null) return null;
        	        return type.getQualifier();
        	    }
        	}
        }
        if(type == null) return null;
        return type.getQualifier();
    }

    // -----------------------------------------------------------
    // Instantiation Methods
    // -----------------------------------------------------------

    public void addBindingsFromScope(ModuleScope scope) {
        Binding bind = scope.getBinding();
        Iterator<TypeID> j = bind.mathTypes.keyIterator();
        while (j.hasNext()) {
            TypeID tid = j.next();
            Type type = bind.mathTypes.get(tid);
            /*
             * NOTE: No need to worry about renamings since this
             * method will only be used on concept and enhancement
             * bindings.
             */
            mathTypes.put(tid, type);
        }
    }

    public Binding instantiate(PosSymbol facility, ModuleScope newscope,
                               Binding replBind, Map<Symbol, Type> typeMap) {
    	
        Binding newbind = new Binding(newscope, myInstanceEnvironment);
        Iterator<TypeID> i = mathTypes.keyIterator();
        while (i.hasNext()) {
            TypeID tid = i.next();
            Type type = mathTypes.get(tid);
            Type newtype = type.instantiate(scope.getScopeID(), replBind);
            
            if (type instanceof FormalType) {
                newtype = getReplacementType(tid, typeMap);
            }
            if (type instanceof ConcType) {
                newtype = getNameType(newtype, facility);
            }
            newbind.mathTypes.put(tid, newtype);
        }
        
        return newbind;
    }

    public void simplifyNames(PosSymbol name) {
        
    	Iterator<TypeID> i = mathTypes.keyIterator();
        while (i.hasNext()) {
            TypeID tid = i.next();
            if (mathTypes.get(tid) instanceof NameType) {
                NameType nametype = (NameType)mathTypes.get(tid);
                NameType newtype = new NameType(nametype.getModuleID(),
                                   nametype.getName(), nametype.getType());
                mathTypes.put(tid, newtype);
            }
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("==================================================\n");
        sb.append("Binding for scope " + scope.getScopeID().toString()
                  + "\n");
        sb.append("==================================================\n");
        sb.append("Program Types: " + programTypes.toString() + "\n");
        sb.append("Mapping: " + mathTypes.toString() + "\n");
        sb.append("----- end binding --------------------------------\n");
        return sb.toString();
    }

    // -----------------------------------------------------------
    // Binding Methods
    // -----------------------------------------------------------

    public void bindTypeNames() {
    	
        TypeLocator locator = new TypeLocator(scope, myInstanceEnvironment);
        Iterator<TypeID> i = mathTypes.keyIterator();
        while (i.hasNext()) {
            TypeID curTypeID = i.next();
            if (!(mathTypes.get(curTypeID) instanceof VoidType)) {
                continue; // type is already bound
            }
            if (programTypes.contains(curTypeID)) {
                try {
                    TypeEntry typeEntry = locator.locateProgramType(curTypeID);
                    Type type;
                    if(typeEntry == null)
                        type = new VoidType();
                    else
                        type = typeEntry.getType();
                    mathTypes.put(curTypeID, type);
                } catch (SymbolSearchException ex) {
                    err.error(programTypeDefinitionLocations.get(curTypeID), ex.getMessage());
                }
            } else { // type is math type
                try {
                    TypeEntry typeEntry = locator.locateMathType(curTypeID);
                    Type type = typeEntry.getType();
                    mathTypes.put(curTypeID, type);
                } catch (SymbolSearchException ex) {
//                   	System.out.println(tid.toString() + " (MATH)");
//                   	System.out.println(scope.getClass());
//                   	System.out.println(scope);
                    err.error(mathTypeDefinitionLocations.get(curTypeID), ex.getMessage());
                }
            }
        } // continue loop
    }

    // ===========================================================
    // Private Methods
    // ===========================================================
    
    // -----------------------------------------------------------
    // Cast Methods
    // -----------------------------------------------------------

    private PrimitiveType castToPrimitiveType(Type type) {
        assert type instanceof PrimitiveType :
                "type is not a PrimitiveType";
        return (PrimitiveType) type;
    }


    private ConcType castToConcType(Type type) {
        assert type instanceof ConcType :
                "typs is not a ConcType";
        return (ConcType) type;
    }

    private ModuleScope castToModuleScope(Scope scope) {
        assert scope instanceof ModuleScope :
                "scope is not a ModuleScope";
        return (ModuleScope) scope;
    }

    private ProcedureScope castToProcedureScope(Scope scope) {
        assert scope instanceof ProcedureScope :
                "scope is not a ProcedureScope";
        return (ProcedureScope) scope;
    }

    private Type getReplacementType(TypeID tid, Map<Symbol, Type> typeMap) {
        Iterator<Symbol> i = typeMap.keyIterator();
        while (i.hasNext()) {
            Symbol sym = i.next();
            TypeID itid = new TypeID(sym);
            if (tid.equals(itid)) {
                return typeMap.get(sym);
            }
        }
        return new VoidType();
    }

    private Type getNameType(Type type, PosSymbol facility) {
        //Environment env = Environment.getInstance();
        ModuleID id = myInstanceEnvironment.getModuleID(facility.getLocation().getFile());
        ConcType conctype = castToConcType(type);
        return new NameType(id, facility, conctype.getName(),
                            conctype.getType());
    }
}
