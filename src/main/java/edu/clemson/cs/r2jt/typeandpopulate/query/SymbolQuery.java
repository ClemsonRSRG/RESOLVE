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
package edu.clemson.cs.r2jt.typeandpopulate.query;

import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.Scope;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.List;

/**
 * <p>A <code>SymbolQuery</code> defines a strategy for returning a list of
 * {@link SymbolTableEntry SymbolTableEntry}s that meet a certain set of 
 * criteria starting from some <em>source scope</em>.
 */
public interface SymbolQuery<E extends SymbolTableEntry> {

    /**
     * <p>Given a source {@link Scope Scope} and a 
     * {@link ScopeRepository ScopeRepository} containing any imports, from
     * which <code>source</code> is drawn, searches them appropriately, 
     * returning a list of matching {@link SymbolTableEntry SymbolTableEntry}s 
     * that are subtypes of <code>E</code>.</p>
     * 
     * <p>If there are no matches, returns an empty list.  If more than one 
     * match is found where no more than one was expected, throws a
     * {@link DuplicateSymbolException DuplicateSymbolException}.</p>
     * 
     * @param source The source scope from which the search was spawned.
     * @param repo A repository of any referenced modules.
     * 
     * @return A list of matches.
     */
    public List<E> searchFromContext(Scope source, ScopeRepository repo)
            throws DuplicateSymbolException;
}
