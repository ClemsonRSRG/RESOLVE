/**
 * FinalizedModuleScope.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.absynnew.ModuleAST;
import edu.clemson.cs.r2jt.absynnew.ResolveAST;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>A <code>FinalizedModuleScope</code> is an immutable realization of 
 * {@link edu.clemson.cs.r2jt.typeandpopulate.ModuleScope ModuleScope}.</p>
 *
 * <p>Note that <code>FinalizedModuleScope</code> has no public constructor.
 * <code>FinalizedModuleScope</code>s are acquired through calls to some of the
 * methods of {@link edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable MathSymbolTable}.</p>
 */
public class FinalizedModuleScope extends FinalizedScope implements ModuleScope {

    private final List<ModuleIdentifier> myImportedModules;
    private final MathSymbolTable mySymbolTable;

    FinalizedModuleScope(ModuleIdentifier module, ResolveAST definingElement,
            Scope parent, BaseSymbolTable bindings,
            List<ModuleIdentifier> importedModules, MathSymbolTable symbolTable) {

        super(symbolTable, definingElement, module, parent, bindings);

        myImportedModules = new LinkedList<ModuleIdentifier>(importedModules);
        mySymbolTable = symbolTable;
    }

    @Override
    public ModuleAST getDefiningElement() {
        return (ModuleAST) myDefiningElement;
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
