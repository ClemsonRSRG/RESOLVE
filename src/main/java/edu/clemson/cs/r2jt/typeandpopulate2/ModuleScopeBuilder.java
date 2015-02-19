/**
 * ModuleScopeBuilder.java
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
package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.absynnew.ModuleAST;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>A <code>ModuleScopeBuilder</code> refines {@link ScopeBuilder} with
 * additional information specific to modules, such as a list of imports.</p>
 */
public class ModuleScopeBuilder extends ScopeBuilder implements ModuleScope {

    private final MathSymbolTableBuilder myWorkingSymbolTable;

    private final List<ModuleIdentifier> myImportedModules =
            new LinkedList<ModuleIdentifier>();

    ModuleScopeBuilder(TypeGraph g, ModuleAST definingElement, Scope parent,
            MathSymbolTableBuilder symbolTable) {
        super(symbolTable, g, definingElement, parent, new ModuleIdentifier(
                definingElement));

        myWorkingSymbolTable = symbolTable;
    }

    @Override
    public ModuleAST getDefiningElement() {
        return (ModuleAST) myDefiningElement;
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
