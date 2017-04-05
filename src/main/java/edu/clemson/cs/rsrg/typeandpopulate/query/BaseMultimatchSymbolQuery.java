/*
 * BaseMultimatchSymbolQuery.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.query;

import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.MultimatchTableSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searchpath.ScopeSearchPath;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ScopeRepository;
import java.util.List;

/**
 * <p>The most basic implementation of {@link MultimatchSymbolQuery}, which
 * pairs a {@link ScopeSearchPath} with a {@link MultimatchTableSearcher} to
 * define a fully parameterized strategy for searching a set of scopes.</p>
 *
 * @param <E> The return type of the base <code>MultimatchSymbolQuery</code>.
 *
 * @version 2.0
 */
abstract class BaseMultimatchSymbolQuery<E extends SymbolTableEntry>
        implements
            MultimatchSymbolQuery<E> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Search path.</p> */
    private final ScopeSearchPath mySearchPath;

    /** <p>Symbol table searcher.</p> */
    private final MultimatchTableSearcher<E> mySearcher;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This is an helper constructor for storing the search path
     * and a multimatch symbol table search strategy.</p>
     *
     * @param path Search path.
     * @param searcher Multimatch symbol table searcher.
     */
    protected BaseMultimatchSymbolQuery(ScopeSearchPath path,
            MultimatchTableSearcher<E> searcher) {
        mySearchPath = path;
        mySearcher = searcher;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Behaves just as {@link SymbolQuery#searchFromContext(Scope, ScopeRepository)},
     * except that it cannot throw a {@link DuplicateSymbolException}.</p>
     *
     * @param source The source scope from which the search was spawned.
     * @param repo A repository of any referenced modules.
     *
     * @return A list of matches.
     */
    @Override
    public final List<E> searchFromContext(Scope source, ScopeRepository repo) {
        return mySearchPath.searchFromContext(mySearcher, source, repo);
    }

}