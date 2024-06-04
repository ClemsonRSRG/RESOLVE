/*
 * BaseSymbolQuery.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.query;

import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.rsrg.typeandpopulate.query.searcher.TableSearcher;
import edu.clemson.rsrg.typeandpopulate.query.searchpath.ScopeSearchPath;
import edu.clemson.rsrg.typeandpopulate.symboltables.Scope;
import edu.clemson.rsrg.typeandpopulate.symboltables.ScopeRepository;
import java.util.List;

/**
 * <p>
 * The most basic implementation of {@link SymbolQuery}, which pairs a {@link ScopeSearchPath} with a
 * {@link TableSearcher} to define a fully parameterized strategy for searching a set of scopes.
 * </p>
 *
 * @param <E>
 *            The return type of the base <code>SymbolQuery</code>.
 *
 * @version 2.0
 */
abstract class BaseSymbolQuery<E extends SymbolTableEntry> implements SymbolQuery<E> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Search path.
     * </p>
     */
    private final ScopeSearchPath mySearchPath;

    /**
     * <p>
     * Symbol table searcher.
     * </p>
     */
    private final TableSearcher<E> mySearcher;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This is an helper constructor for storing the search path and symbol table search strategy.
     * </p>
     *
     * @param path
     *            Search path.
     * @param searcher
     *            Symbol table searcher.
     */
    protected BaseSymbolQuery(ScopeSearchPath path, TableSearcher<E> searcher) {
        mySearchPath = path;
        mySearcher = searcher;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Given a source {@link Scope} and a {@link ScopeRepository} containing any imports, from which <code>source</code>
     * is drawn, searches them appropriately, returning a list of matching {@link SymbolTableEntry}s that are subtypes
     * of <code>E</code>.
     * </p>
     *
     * <p>
     * If there are no matches, returns an empty list. If more than one match is found where no more than one was
     * expected, throws a {@link DuplicateSymbolException}.
     * </p>
     *
     * @param source
     *            The source scope from which the search was spawned.
     * @param repo
     *            A repository of any referenced modules.
     *
     * @return A list of matches.
     */
    @Override
    public final List<E> searchFromContext(Scope source, ScopeRepository repo) throws DuplicateSymbolException {
        return mySearchPath.searchFromContext(mySearcher, source, repo);
    }

}
