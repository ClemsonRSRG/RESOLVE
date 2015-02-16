/**
 * BaseMultimatchSymbolQuery.java
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
package edu.clemson.cs.r2jt.typeandpopulate2.query;

import edu.clemson.cs.r2jt.typeandpopulate2.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate2.Scope;
import edu.clemson.cs.r2jt.typeandpopulate2.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate2.ScopeSearchPath;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.searchers.MultimatchTableSearcher;

import java.util.List;

/**
 * <p>Refines {@link BaseSymbolQuery} by guaranteeing that the
 * associated searcher is a {@link MultimatchTableSearcher},
 * and thus the search methods of this class are guaranteed not to throw a
 * {@link DuplicateSymbolException}.</p>
 */
public class BaseMultimatchSymbolQuery<E extends SymbolTableEntry>
        extends
            BaseSymbolQuery<E> {

    public BaseMultimatchSymbolQuery(ScopeSearchPath path,
                                     MultimatchTableSearcher<E> searcher) {
        super(path, searcher);
    }

    /**
     * <p>Refines {@link BaseSymbolQuery#searchFromContext(Scope, ScopeRepository)
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
