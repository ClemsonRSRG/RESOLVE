/**
 * ModuleScope.java
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
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;

import java.util.List;

/**
 * <p>A <code>ModuleScope</code> refines {@link Scope} to provide methods
 * specific to those scopes introduced by a RESOLVE module.</p>
 *
 * <p>As with <code>Scope</code>, <code>ModuleScope</code> defines no mutator
 * methods, but specific concrete subclasses may be mutable.</p>
 */
public interface ModuleScope extends Scope {

    public ModuleAST getDefiningElement();

    /**
     * <p>Returns a <code>ModuleIdentifier</code> that can be used to refer
     * to the module who's scope is represented by this 
     * <code>ModuleScope</code>.</p>
     * 
     * @return The <code>ModuleIdentifier</code>.
     */
    public ModuleIdentifier getModuleIdentifier();

    /**
     * <p>Returns <code>true</code> <strong>iff</code> the module who's scope
     * is represented by this <code>ModuleScope</code> imports the given
     * module.  Note that, by definition, all modules import themselves.</p>
     */
    public boolean imports(ModuleIdentifier i);

    /**
     * <p>Returns a <code>List</code> of modules that the module who's scope
     * is represented by this <code>ModuleScope</code> imports, not including
     * itself (which all modules are defined to import).  This <code>List</code>
     * is a copy and modifying it will not impact the behavior of this
     * <code>ModuleScope</code>.</p>
     * 
     * @returns A <code>List</code> of imported modules.
     */
    public List<ModuleIdentifier> getImports();
}
