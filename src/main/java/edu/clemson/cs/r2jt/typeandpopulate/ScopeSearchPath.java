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

import edu.clemson.cs.r2jt.typeandpopulate.searchers.TableSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.List;

/**
 * <p>A <code>ScopeSearchPath</code> defines which {@link Scope Scope}s
 * should be searched for symbol table matches and in what order.</p>
 * 
 * <p>All symbol table searches take place in the context of a 
 * <em>source scope</em>, which is the scope from which the request is made.  
 * I.e., if a procedure called <code>Foo</code> references a symbol called 
 * <code>X</code>, triggering a look-up for what <code>X</code> could be, then 
 * the scope for <code>Foo</code> is the source scope.</p>
 * 
 * <p>Given a {@link TableSearcher TableSearcher}, a source scope, and a 
 * {@link ScopeRepository ScopeRepository} containing any imports, a 
 * <code>ScopeSearchPath</code> will apply the <code>TableSearcher</code> 
 * appropriately to any <code>Scope</code>s that should be searched.</p>
 */
public interface ScopeSearchPath {

    /**
     * <p>Applies the given {@link TableSearcher TableSearcher} to the 
     * appropriate {@link Scope Scope}s, given a source scope and a
     * {@link ScopeRepository ScopeRepository} containing any imports, returning
     * a list of matching {@link SymbolTableEntry SymbolTableEntry}s.</p>
     * 
     * <p>If there are no matches, returns an empty list.  If more than one 
     * match is found and <code>searcher</code> expects no more than one match,
     * throws a {@link DuplicateSymbolException DuplicateSymbolException}.</p>
     * 
     * @param searcher A <code>TableSearcher</code> to apply to each scope along
     *            the search path.
     * @param table A symbol table containing any referenced modules.
     * @param context The current scope from which the search was spawned.
     * 
     * @return A list of matches.
     */
    public <E extends SymbolTableEntry> List<E> searchFromContext(
            TableSearcher<E> searcher, Scope source, ScopeRepository repo)
            throws DuplicateSymbolException;
}
