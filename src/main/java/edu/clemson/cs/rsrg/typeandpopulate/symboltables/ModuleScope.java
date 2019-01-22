/*
 * ModuleScope.java
 * ---------------------------------
 * Copyright (c) 2019
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
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.List;

/**
 * <p>A <code>ModuleScope</code> refines {@link Scope} to provide methods
 * specific to those scopes introduced by a RESOLVE module.</p>
 *
 * <p>As with <code>Scope</code>, <code>ModuleScope</code> defines no mutator
 * methods, but specific concrete subclasses may be mutable.</p>
 *
 * @version 2.0
 */
public interface ModuleScope extends Scope {

    /**
     * <p>Returns a <code>ModuleDec</code> who's scope is represented by
     * this <code>ModuleScope</code>.</p>
     *
     * @return The <code>ModuleDec</code>.
     */
    ModuleDec getDefiningElement();

    /**
     * <p>Returns a <code>List</code> of modules that the module who's scope
     * is represented by this <code>ModuleScope</code> imports, not including
     * itself (which all modules are defined to import).  This <code>List</code>
     * is a copy and modifying it will not impact the behavior of this
     * <code>ModuleScope</code>.</p>
     *
     * @return A <code>List</code> of imported modules.
     */
    List<ModuleIdentifier> getImports();

    /**
     * <p>Returns a <code>ModuleIdentifier</code> that can be used to refer
     * to the module who's scope is represented by this
     * <code>ModuleScope</code>.</p>
     *
     * @return The <code>ModuleIdentifier</code>.
     */
    ModuleIdentifier getModuleIdentifier();

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> the module who's scope
     * is represented by this <code>ModuleScope</code> imports the given
     * module. Note that, by definition, all modules import themselves.</p>
     *
     * @param i A {@link ModuleIdentifier}.
     *
     * @return The boolean result.
     */
    boolean imports(ModuleIdentifier i);

}