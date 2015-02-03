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
package edu.clemson.cs.r2jt.typeandpopulate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;

/**
 * <p>A <code>FinalizedModuleScope</code> is an immutable realization of 
 * {@link ModuleScope ModuleScope}.</p>
 * 
 * <p>Note that <code>FinalizedModuleScope</code> has no public constructor.  
 * <code>FinalizedModuleScope</code>s are acquired through calls to some of the 
 * methods of {@link MathSymbolTable MathSymbolTable}.</p>
 */
public class FinalizedModuleScope extends FinalizedScope implements ModuleScope {

    private final List<ModuleIdentifier> myImportedModules;
    private final MathSymbolTable mySymbolTable;

    FinalizedModuleScope(ModuleIdentifier module,
            ResolveConceptualElement definingElement, Scope parent,
            BaseSymbolTable bindings, List<ModuleIdentifier> importedModules,
            MathSymbolTable symbolTable) {

        super(symbolTable, definingElement, module, parent, bindings);

        myImportedModules = new LinkedList<ModuleIdentifier>(importedModules);
        mySymbolTable = symbolTable;
    }

    @Override
    public ModuleDec getDefiningElement() {
        return (ModuleDec) myDefiningElement;
    }

    @Override
    public ModuleIdentifier getModuleIdentifier() {
        return myRootModule;
    }

    @Override
    public boolean imports(ModuleIdentifier i) {
        return i.equals(myRootModule) || myImportedModules.contains(i);
    }

    @Override
    public List<ModuleIdentifier> getImports() {
        return new LinkedList<ModuleIdentifier>(myImportedModules);
    }
}
