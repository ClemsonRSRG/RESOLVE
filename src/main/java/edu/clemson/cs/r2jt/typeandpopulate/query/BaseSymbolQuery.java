/*
 * BaseSymbolQuery.java
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
package edu.clemson.cs.r2jt.typeandpopulate.query;

import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.Scope;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeSearchPath;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.TableSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.List;

/**
 * <p>
 * The most basic implementation of {@link SymbolQuery SymbolQuery}, which pairs
 * a
 * {@link ScopeSearchPath ScopeSearchPath} with a {@link TableSearcher
 * TableSearcher} to define a
 * fully parameterized strategy for searching a set of scopes.
 * </p>
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
