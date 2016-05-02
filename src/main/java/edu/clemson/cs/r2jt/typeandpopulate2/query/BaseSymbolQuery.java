/**
 * BaseSymbolQuery.java
 * ---------------------------------
 * Copyright (c) 2016
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
import edu.clemson.cs.r2jt.typeandpopulate2.searchers.TableSearcher;

import java.util.List;

/**
 * <p>The most basic implementation of {@link SymbolQuery}, which
 * pairs a {@link ScopeSearchPath} with a {@link TableSearcher} to define a f
 * ully parameterized strategy for searching a set of scopes.</p>
 */
public class BaseSymbolQuery<E extends SymbolTableEntry>
        implements
            SymbolQuery<E> {

    private final ScopeSearchPath mySearchPath;
    private final TableSearcher<E> mySearcher;

    public BaseSymbolQuery(ScopeSearchPath path, TableSearcher<E> searcher) {
        mySearchPath = path;
        mySearcher = searcher;
    }

    @Override
    public List<E> searchFromContext(Scope source, ScopeRepository repo)
            throws DuplicateSymbolException {
        return mySearchPath.searchFromContext(mySearcher, source, repo);
    }
}
