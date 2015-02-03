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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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

import java.util.List;

import edu.clemson.cs.r2jt.absyn.ModuleDec;

/**
 * <p>A <code>ModuleScope</code> refines {@link Scope Scope} to provide methods
 * specific to those scopes introduced by a RESOLVE module.</p>
 * 
 * <p>As with <code>Scope</code>, <code>ModuleScope</code> defines no mutator
 * methods, but specific concrete subclasses may be mutable.</p>
 */
public interface ModuleScope extends Scope {

    public ModuleDec getDefiningElement();

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
