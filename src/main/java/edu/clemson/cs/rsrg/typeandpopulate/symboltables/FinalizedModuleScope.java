/*
 * FinalizedModuleScope.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.symboltables;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>A <code>FinalizedModuleScope</code> is an immutable realization of
 * {@link ModuleScope}.</p>
 *
 * <p>Note that <code>FinalizedModuleScope</code> has no public constructor.
 * <code>FinalizedModuleScope</code>s are acquired through calls to some of the
 * methods of {@link MathSymbolTable}.</p>
 *
 * @version 2.0
 */
class FinalizedModuleScope extends FinalizedScope implements ModuleScope {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The list of modules imported by this {@link ModuleDec}.</p> */
    private final List<ModuleIdentifier> myImportedModules;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an immutable scope for a {@link ModuleDec}.</p>
     *
     * @param source The source scope repository.
     * @param definingElement The element that created this scope.
     * @param parent The parent scope.
     * @param enclosingModule The module identifier for the module
     *                        that this scope belongs to.
     * @param bindings The symbol table bindings.
     * @param importedModules The list of modules imported by this
     *                        {@link ModuleDec}.
     */
    FinalizedModuleScope(MathSymbolTable source, ResolveConceptualElement definingElement,
                         Scope parent, ModuleIdentifier enclosingModule, BaseSymbolTable bindings,
                         List<ModuleIdentifier> importedModules) {
        super(source, definingElement, parent, enclosingModule, bindings);
        myImportedModules = new LinkedList<>(importedModules);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Returns a <code>ModuleDec</code> who's scope is represented by
     * this <code>ModuleScope</code>.</p>
     *
     * @return The <code>ModuleDec</code>.
     */
    @Override
    public final ModuleDec getDefiningElement() {
        return (ModuleDec) myDefiningElement;
    }

    /**
     * <p>Returns a <code>List</code> of modules that the module who's scope
     * is represented by this <code>ModuleScope</code> imports, not including
     * itself (which all modules are defined to import).  This <code>List</code>
     * is a copy and modifying it will not impact the behavior of this
     * <code>ModuleScope</code>.</p>
     *
     * @returns A <code>List</code> of imported modules.
     */
    @Override
    public List<ModuleIdentifier> getImports() {
        return new LinkedList<>(myImportedModules);
    }

    /**
     * <p>Returns a <code>ModuleIdentifier</code> that can be used to refer
     * to the module who's scope is represented by this
     * <code>ModuleScope</code>.</p>
     *
     * @return The <code>ModuleIdentifier</code>.
     */
    @Override
    public final ModuleIdentifier getModuleIdentifier() {
        return getRootModule();
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</code> the module who's scope
     * is represented by this <code>ModuleScope</code> imports the given
     * module. Note that, by definition, all modules import themselves.</p>
     *
     * @return The boolean result.
     */
    @Override
    public boolean imports(ModuleIdentifier i) {
        return i.equals(getRootModule()) || myImportedModules.contains(i);
    }

}