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
package edu.clemson.cs.r2jt.typeandpopulate.query;

import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.MultimatchTableSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.Scope;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeSearchPath;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.List;

/**
 * <p>Refines {@link BaseSymbolQuery BaseSymbolQuery} by guaranteeing that the 
 * associated searcher is a {@link MultimatchTableSearch MultimatchTableSearch},
 * and thus the search methods of this class are guaranteed not to throw a
 * {@link DuplicateSymbolException DuplicateSymbolException}.</p>
 */
public class BaseMultimatchSymbolQuery<E extends SymbolTableEntry>
        extends
            BaseSymbolQuery<E> {

    public BaseMultimatchSymbolQuery(ScopeSearchPath path,
            MultimatchTableSearcher<E> searcher) {
        super(path, searcher);
    }

    /**
     * <p>Refines {@link 
     * BaseSymbolQuery#searchFromContext(Scope, ScopeRepository)
     * BaseSymbolQuery.searchFromContext()} to guarantee that it will not
     * throw a {@link DuplicateSymbolException DuplicateSymbolException}.
     * Otherwise, behaves identically.</p>
     */
    @Override
    public List<E> searchFromContext(Scope source, ScopeRepository repo) {

        List<E> result;

        try {
            result = super.searchFromContext(source, repo);
        }
        catch (DuplicateSymbolException dse) {
            //Not possible.  We know our searcher is, in fact, a 
            //MultimatchTableSearch
            throw new RuntimeException(dse);
        }

        return result;
    }
}
