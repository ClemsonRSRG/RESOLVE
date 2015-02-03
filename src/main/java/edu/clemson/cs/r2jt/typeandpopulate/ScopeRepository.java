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

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * <p>A <code>SymbolTable</code> maps
 * {@link edu.clemson.cs.r2jt.absyn.ResolveConceptualElement 
 *     ResolveConceptualElement}s and {@link ModuleIdentifier ModuleIdentifier}s
 * to the {@link Scope Scope}s they introduce.</p>
 * 
 * <p>Each <code>SymbolTable</code> has a 
 * {@link edu.clemson.cs.r2jt.typereasoning.TypeGraph TypeGraph} that relates
 * the types found in the symbol table.</p>
 * 
 * <p>While this base class defines no methods for mutating the symbol table,
 * concrete subclasses may provide mutation methods.  It is particularly 
 * important that clients be aware the symbol table may be "under construction"
 * even as they use it.  We therefore favor vocabulary such as "open" and 
 * "closed" for scopes rather than "exists", which might imply (erroneously) 
 * that scopes spring into existence atomically and fully formed.</p>
 */
public abstract class ScopeRepository {

    /**
     * <p>Returns the {@link ModuleScope ModuleScope} associated with the given
     * {@link ModuleIdentifier ModuleIdentifier}.</p>
     * 
     * @param module The module identifier.
     * 
     * @returns The associated module scope.
     * 
     * @throws NoSuchSymbolException If no scope has been opened for the named 
     *             module.
     */
    public abstract ModuleScope getModuleScope(ModuleIdentifier module)
            throws NoSuchSymbolException;

    /**
     * <p>Returns the {@link Scope Scope} introduced and bounded by the given
     * defining element.</p>
     * 
     * @param The defining element.
     * 
     * @returns The associated scope.
     * 
     * @throws NoSuchSymbolException If no scope has been opened for the given
     *             defining element.
     */
    public abstract Scope getScope(ResolveConceptualElement e);

    /**
     * <p>Returns the 
     * {@link edu.clemson.cs.r2jt.typereasoning.TypeGraph TypeGraph} that 
     * relates the types found in this <code>ImportRepository</code>.</p>
     * 
     * @return The <code>TypeGraph</code>.
     */
    public abstract TypeGraph getTypeGraph();
}
