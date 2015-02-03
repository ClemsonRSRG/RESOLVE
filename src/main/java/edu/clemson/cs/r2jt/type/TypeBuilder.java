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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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
package edu.clemson.cs.r2jt.type;

import edu.clemson.cs.r2jt.absyn.*;
import edu.clemson.cs.r2jt.collections.*;
import edu.clemson.cs.r2jt.data.*;
import edu.clemson.cs.r2jt.entry.*;
import edu.clemson.cs.r2jt.errors.ErrorHandler;
import edu.clemson.cs.r2jt.init.CompileEnvironment;
import edu.clemson.cs.r2jt.init.Environment;
import edu.clemson.cs.r2jt.location.OperationLocator;
import edu.clemson.cs.r2jt.location.DefinitionLocator;
import edu.clemson.cs.r2jt.location.ProofLocator;
import edu.clemson.cs.r2jt.location.QualifierLocator;
import edu.clemson.cs.r2jt.location.TheoremLocator;
import edu.clemson.cs.r2jt.location.VariableLocator;
import edu.clemson.cs.r2jt.location.SymbolSearchException;
import edu.clemson.cs.r2jt.location.TypeLocator;
import edu.clemson.cs.r2jt.proofchecking.*;
import edu.clemson.cs.r2jt.scope.Binding;
import edu.clemson.cs.r2jt.scope.ModuleScope;
import edu.clemson.cs.r2jt.scope.OperationScope;
import edu.clemson.cs.r2jt.scope.Scope;
import edu.clemson.cs.r2jt.scope.ScopeID;
import edu.clemson.cs.r2jt.scope.OldSymbolTable;
import edu.clemson.cs.r2jt.scope.TypeHolder;
import edu.clemson.cs.r2jt.scope.TypeID;

public class TypeBuilder {

    // ===========================================================
    // Variables 
    // ===========================================================

    //private Environment env = Environment.getInstance();
    private CompileEnvironment myInstanceEnvironment;

    private ErrorHandler err;

    private OldSymbolTable table = null;

    private Type B = null;

    private Type Char = null;

    private Type N = null;

    private Type Z = null;

    private Type R = null;

    private Type Str = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TypeBuilder(OldSymbolTable table,
            CompileEnvironment instanceEnvironment) {
        myInstanceEnvironment = instanceEnvironment;
        this.table = table;
        this.err = instanceEnvironment.getErrorHandler();
    }

    public void setInstanceVar(String var, Type t) {
        if (var.equals("B"))
            B = t;
        else if (var.equals("N"))
            N = t;
        else if (var.equals("Z"))
            Z = t;
        else if (var.equals("Char"))
            Char = t;
        else if (var.equals("Str"))
            Str = t;
    }

    // If XXX_Theory.X is visible in the symbol table,
    //     it retrieves a copy of it - only have to check
    //     for X's visibility once per module
    // NOTE: Unfortunately, can't just use a VariableLocator
    //     to find XXX_Theory.X -- same problem if
    //     XXX_Theory is not already compiled
    public Type getType(String theory, String var, Location loc, boolean quiet) {
        // If we have already concluded that the type is visible, return
        //     that type's value (held in the instance variable of the
        //     same name)
        if (var.equals("B") && B != null)
            return B;
        if (var.equals("N") && N != null)
            return N;
        if (var.equals("Z") && Z != null)
            return Z;
        if (var.equals("Char") && Char != null)
            return Char;
        if (var.equals("Str") && Str != null)
            return Str;
        ModuleID tid =
                ModuleID.createTheoryID(new PosSymbol(null, Symbol
                        .symbol(theory)));
        if (myInstanceEnvironment.contains(tid)) {
            // Make sure XXX_Theory has math type "X"
            PosSymbol ps = new PosSymbol(null, Symbol.symbol(var));
            OldSymbolTable st = myInstanceEnvironment.getSymbolTable(tid);
            ModuleScope scope = null;
            if (st != null) {
                // XXX_Theory has already been successfully compiled
                scope = myInstanceEnvironment.getModuleScope(tid);
            }
            else if (table.getModuleScope().getModuleID().equals(tid)) {
                // XXX_Theory has not fully been compiled yet,
                //     check to see if *this* is XXX_Theory, in
                //     which case we can get X from our own ST
                scope = table.getModuleScope();
                if (scope.containsType(ps.getSymbol())) {
                    // If X exists in the current scope
                    Type t = scope.getType(ps.getSymbol()).getType();
                    setInstanceVar(var, t);
                    return t;
                }
                else {
                    if (!quiet) {
                        err.error(loc, "Module " + theory
                                + " does not contain math type " + var + ".");
                    }
                    return null;
                }
            }
            else {
                if (!quiet) {
                    err.error(loc, "Module " + theory + " is not visible from "
                            + table.getModuleID().getFilename() + ".");
                }
                return null;
            }
            if (scope.containsType(ps.getSymbol())) {
                // XXX_Theory has already been successfully compiled
                Type t = scope.getType(ps.getSymbol()).getType();
                setInstanceVar(var, t);
                return t;
            }
            else {
                if (!quiet) {
                    err.error(loc, "Module " + theory
                            + " does not contain math type " + var + ".");
                }
                return null;
            }
        }
        else {
            if (!quiet) {
                err.error(loc, "Module " + theory + " is not visible from "
                        + table.getModuleID().getFilename() + ".");
            }
            return null;
        }
    }
}
