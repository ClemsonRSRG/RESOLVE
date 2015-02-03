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

import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * <p>A <code>ModuleScopeBuilder</code> refines 
 * {@link ScopeBuilder ScopeBuilder} with additional information specific to 
 * modules, such as a list of imports.</p>
 */
public class ModuleScopeBuilder extends ScopeBuilder implements ModuleScope {

    private final MathSymbolTableBuilder myWorkingSymbolTable;

    private final List<ModuleIdentifier> myImportedModules =
            new LinkedList<ModuleIdentifier>();

    ModuleScopeBuilder(TypeGraph g, ModuleDec definingElement, Scope parent,
            MathSymbolTableBuilder symbolTable) {
        super(symbolTable, g, definingElement, parent, new ModuleIdentifier(
                definingElement));

        myWorkingSymbolTable = symbolTable;
    }

    @Override
    public ModuleDec getDefiningElement() {
        return (ModuleDec) myDefiningElement;
    }

    @Override
    public ModuleIdentifier getModuleIdentifier() {
        return myRootModule;
    }

    /**
     * <p>Adds <code>i</code> to the list of modules imported by the module
     * who's scope this <code>ModuleScopeBuilder</code> represents.  Duplicate
     * imports or attempting to import the module represented itself will leave
     * the import list unmodified.</p>
     * 
     * @param i The module to import.
     */
    public void addImport(ModuleIdentifier i) {
        if (!myImportedModules.contains(i) && !myRootModule.equals(i)) {
            myImportedModules.add(i);
        }
    }

    @Override
    public boolean imports(ModuleIdentifier i) {
        return i.equals(myRootModule) || myImportedModules.contains(i);
    }

    @Override
    public List<ModuleIdentifier> getImports() {
        return new LinkedList<ModuleIdentifier>(myImportedModules);
    }

    @Override
    public FinalizedModuleScope seal(MathSymbolTable finalTable) {
        return new FinalizedModuleScope(myRootModule, myDefiningElement,
                myParent, myBindings, myImportedModules, finalTable);
    }
}
