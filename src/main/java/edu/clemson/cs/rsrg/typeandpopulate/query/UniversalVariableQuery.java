/*
 * UniversalVariableQuery.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.query;

import edu.clemson.cs.rsrg.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.MultimatchTableSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.TableSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searchpath.ScopeSearchPath;
import edu.clemson.cs.rsrg.typeandpopulate.query.searchpath.UnqualifiedPath;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ScopeRepository;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.SymbolTable;
import java.util.Iterator;
import java.util.List;

/**
 * <p>A <code>UniversalVariableQuery</code> searches for entries that
 * are universally quantified.</p>
 *
 * @version 2.0
 */
public class UniversalVariableQuery
        implements
            MultimatchSymbolQuery<MathSymbolEntry> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>A singleton instance for this query.</p> */
    public static final MultimatchSymbolQuery<MathSymbolEntry> INSTANCE =
            new UniversalVariableQuery();

    /** <p>The actual query that is going to perform the searching.</p> */
    private final BaseMultimatchSymbolQuery<MathSymbolEntry> myBaseQuery;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This query searches for all {@link MathSymbolEntry} that are
     * universally quantified.</p>
     */
    private UniversalVariableQuery() {
        myBaseQuery =
                new SimpleMultimatchSymbolQuery(new UnqualifiedPath(
                        ImportStrategy.IMPORT_NONE,
                        FacilityStrategy.FACILITY_IGNORE, false),
                        new UniversalVariableSearcher());
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
    public final List<MathSymbolEntry> searchFromContext(Scope source,
            ScopeRepository repo) {
        return myBaseQuery.searchFromContext(source, repo);
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>This is a simple implementation for a {@link BaseMultimatchSymbolQuery}.</p>
     */
    private static class SimpleMultimatchSymbolQuery
            extends
                BaseMultimatchSymbolQuery<MathSymbolEntry> {

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This query searches for all {@link MathSymbolEntry} that using a
         * {@link MultimatchTableSearcher}.</p>
         */
        SimpleMultimatchSymbolQuery(ScopeSearchPath path,
                MultimatchTableSearcher<MathSymbolEntry> searcher) {
            super(path, searcher);
        }

    }

    /**
     * <p>This is an implementation of {@link MultimatchTableSearcher} for
     * searching universally quantified symbol table entries.</p>
     */
    private static class UniversalVariableSearcher
            implements
                MultimatchTableSearcher<MathSymbolEntry> {

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>Refines {@link TableSearcher#addMatches(SymbolTable, List, SearchContext)}}
         * to guarantee that it will not throw a {@link DuplicateSymbolException}.
         * Otherwise, behaves identically.</p>
         *
         * @param entries The set of symbol table entries to consider.
         * @param matches A non-<code>null</code> accumulator of matches.
         * @param l The context from which <code>entries</code> was drawn.
         *
         * @return <code>true</code> if <code>matches</code> now represents a
         *         final list of search results; i.e., no further symbol table
         *         entries should be considered. <code>false</code> indicates that
         *         the search should continue, provided there are additional
         *         un-searched scopes.
         */
        @Override
        public final boolean addMatches(SymbolTable entries,
                List<MathSymbolEntry> matches, SearchContext l) {
            Iterator<MathSymbolEntry> mathSymbols =
                    entries.iterateByType(MathSymbolEntry.class);

            MathSymbolEntry curSymbol;
            while (mathSymbols.hasNext()) {
                curSymbol = mathSymbols.next();

                if (curSymbol.getQuantification() == SymbolTableEntry.Quantification.UNIVERSAL) {
                    matches.add(curSymbol);
                }
            }

            return false;
        }

    }

}