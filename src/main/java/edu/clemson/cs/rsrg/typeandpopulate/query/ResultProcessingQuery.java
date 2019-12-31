/*
 * ResultProcessingQuery.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.query;

import edu.clemson.cs.rsrg.misc.Utilities.Mapping;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.TableSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searchpath.ScopeSearchPath;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ScopeRepository;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * An implementation of {@link SymbolQuery} that decorates an existing
 * <code>SymbolQuery</code>,
 * post processing its results and returning the processed set of results.
 * </p>
 *
 * @param <T> The return type of the base <code>SymbolQuery</code>.
 * @param <R> The return type of the resultant, processed entries.
 *
 * @version 2.0
 */
abstract class ResultProcessingQuery<T extends SymbolTableEntry, R extends SymbolTableEntry>
        implements
            SymbolQuery<R> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The actual query that is going to perform the searching.
     * </p>
     */
    private final SymbolQuery<T> myBaseQuery;

    /**
     * <p>
     * A mapping between two types of entries.
     * </p>
     */
    private final Mapping<T, R> myMapping;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This query uses the provided query to search for all entries of type
     * <code>T</code> and further
     * processes the entries with type <code>R</code>.
     * </p>
     *
     * @param baseQuery The query that is being decorated.
     * @param processing A mapping between two types of entries.
     */
    protected ResultProcessingQuery(SymbolQuery<T> baseQuery,
            Mapping<T, R> processing) {
        myBaseQuery = baseQuery;
        myMapping = processing;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Given a source {@link Scope} and a {@link ScopeRepository} containing any
     * imports, from which
     * <code>source</code> is drawn, searches them appropriately, returning a
     * list of matching
     * {@link SymbolTableEntry}s that are subtypes of <code>E</code>.
     * </p>
     *
     * <p>
     * If there are no matches, returns an empty list. If more than one match is
     * found where no more
     * than one was expected, throws a {@link DuplicateSymbolException}.
     * </p>
     *
     * @param source The source scope from which the search was spawned.
     * @param repo A repository of any referenced modules.
     *
     * @return A list of matches.
     */
    @Override
    public final List<R> searchFromContext(Scope source, ScopeRepository repo)
            throws DuplicateSymbolException {
        List<T> intermediateMatches =
                myBaseQuery.searchFromContext(source, repo);

        List<R> finalMatches = new LinkedList<>();
        for (T intermediateMatch : intermediateMatches) {
            finalMatches.add(myMapping.map(intermediateMatch));
        }

        return finalMatches;
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>
     * This is a simple implementation for a {@link BaseSymbolQuery}.
     * </p>
     */
    static class SimpleSymbolQuery extends BaseSymbolQuery<SymbolTableEntry> {

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * This query searches for all {@link SymbolTableEntry} that using a
         * {@link TableSearcher}.
         * </p>
         */
        SimpleSymbolQuery(ScopeSearchPath path,
                TableSearcher<SymbolTableEntry> searcher) {
            super(path, searcher);
        }

    }

}
