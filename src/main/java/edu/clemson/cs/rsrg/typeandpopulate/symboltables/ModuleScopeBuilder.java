/*
 * ModuleScopeBuilder.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.symboltables;

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * A <code>ModuleScopeBuilder</code> refines {@link ScopeBuilder} with
 * additional information
 * specific to modules, such as a list of imports.
 * </p>
 *
 * @version 2.0
 */
public class ModuleScopeBuilder extends ScopeBuilder implements ModuleScope {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * This contains all the imported modules.
     * </p>
     */
    private final List<ModuleIdentifier> myImportedModules = new LinkedList<>();

    // ==========================================================
    // Constructors
    // ==========================================================

    /**
     * <p>
     * This constructs a module scope where new entries can be added.
     * </p>
     *
     * @param g The current type graph.
     * @param definingElement The element that created this scope.
     * @param parent The parent scope.
     * @param symbolTable The current scope repository builder.
     */
    ModuleScopeBuilder(TypeGraph g, ModuleDec definingElement, Scope parent,
            MathSymbolTableBuilder symbolTable) {
        super(symbolTable, g, definingElement, parent,
                new ModuleIdentifier(definingElement));
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Adds <code>i</code> to the list of modules imported by the module who's
     * scope this
     * <code>ModuleScopeBuilder</code> represents. Duplicate imports or
     * attempting to import the
     * module represented itself will leave the import list unmodified.
     * </p>
     *
     * @param i The module to import.
     */
    public final void addImport(ModuleIdentifier i) {
        if (!myImportedModules.contains(i) && !myRootModule.equals(i)) {
            myImportedModules.add(i);
        }
    }

    /**
     * <p>
     * Returns a <code>ModuleDec</code> who's scope is represented by this
     * <code>ModuleScope</code>.
     * </p>
     *
     * @return The <code>ModuleDec</code>.
     */
    @Override
    public final ModuleDec getDefiningElement() {
        return (ModuleDec) myDefiningElement;
    }

    /**
     * <p>
     * Returns a <code>List</code> of modules that the module who's scope is
     * represented by this
     * <code>ModuleScope</code> imports, not including itself (which all modules
     * are defined to
     * import). This <code>List</code> is a copy and modifying it will not
     * impact the behavior of this
     * <code>ModuleScope</code>.
     * </p>
     *
     * @return A <code>List</code> of imported modules.
     */
    @Override
    public final List<ModuleIdentifier> getImports() {
        return new LinkedList<>(myImportedModules);
    }

    /**
     * <p>
     * Returns a <code>ModuleIdentifier</code> that can be used to refer to the
     * module who's scope is
     * represented by this <code>ModuleScope</code>.
     * </p>
     *
     * @return The <code>ModuleIdentifier</code>.
     */
    @Override
    public final ModuleIdentifier getModuleIdentifier() {
        return myRootModule;
    }

    /**
     * <p>
     * Returns <code>true</code> <strong>iff</strong> the module who's scope is
     * represented by this
     * <code>ModuleScope</code> imports the given module. Note that, by
     * definition, all modules import
     * themselves.
     * </p>
     *
     * @return The boolean result.
     */
    @Override
    public final boolean imports(ModuleIdentifier i) {
        return i.equals(myRootModule) || myImportedModules.contains(i);
    }

    // ===========================================================
    // Package Private Methods
    // ===========================================================

    /**
     * <p>
     * This method seals this scope from further modifications.
     * </p>
     *
     * @param finalTable The finalized symbol table.
     *
     * @return A {@link FinalizedModuleScope} object.
     */
    @Override
    final FinalizedModuleScope seal(MathSymbolTable finalTable) {
        return new FinalizedModuleScope(finalTable, myDefiningElement, myParent,
                myRootModule, myBindings, myImportedModules);
    }

}
