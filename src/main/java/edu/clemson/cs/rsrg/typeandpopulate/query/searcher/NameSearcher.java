/*
 * NameSearcher.java
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
package edu.clemson.cs.rsrg.typeandpopulate.query.searcher;

import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.SymbolTable;
import java.util.List;

/**
 * <p>
 * A <code>NameSearcher</code> returns entries in a {@link SymbolTable} that
 * have the specified
 * name.
 * </p>
 *
 * @version 2.0
 */
public class NameSearcher implements MultimatchTableSearcher<SymbolTableEntry> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Name of the entry to be searched
     * </p>
     */
    private final String mySearchString;

    /**
     * <p>
     * Boolean flag that indicates if we stop after we find the first or not.
     * </p>
     */
    private final boolean myStopAfterFirstFlag;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a searcher that specifies a search string and a boolean
     * flag that indicates
     * whether or not we stop after the first match.
     * </p>
     *
     * @param searchString Name of the entry to be searched.
     * @param stopAfterFirst Boolean flag that indicates if we stop after we
     *        find the first or not.
     */
    public NameSearcher(String searchString, boolean stopAfterFirst) {
        mySearchString = searchString;
        myStopAfterFirstFlag = stopAfterFirst;
    }

    /**
     * <p>
     * This constructs a searcher that specifies a search string and stops after
     * we locate the first
     * match.
     * </p>
     *
     * @param searchString Name of the entry to be searched.
     */
    public NameSearcher(String searchString) {
        this(searchString, true);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Refines
     * {@link TableSearcher#addMatches(SymbolTable, List, SearchContext)}} to
     * guarantee that
     * it will not throw a {@link DuplicateSymbolException}. Otherwise, behaves
     * identically.
     * </p>
     *
     * @param entries The set of symbol table entries to consider.
     * @param matches A non-<code>null</code> accumulator of matches.
     * @param l The context from which <code>entries</code> was drawn.
     *
     * @return <code>true</code> if <code>matches</code> now represents a final
     *         list of search
     *         results; i.e., no further symbol table entries should be
     *         considered. <code>false</code>
     *         indicates that the search should continue, provided there are
     *         additional un-searched
     *         scopes.
     */
    @Override
    public final boolean addMatches(SymbolTable entries,
            List<SymbolTableEntry> matches, SearchContext l) {
        boolean result = entries.containsKey(mySearchString);

        if (result) {
            SymbolTableEntry e = entries.get(mySearchString);

            // Parameters of imported modules or facility instantiations are not
            // exported and therefore should not be considered for results
            if (l.equals(SearchContext.SOURCE_MODULE)
                    || !(e instanceof ProgramParameterEntry)) {
                matches.add(entries.get(mySearchString));
            }
        }

        return myStopAfterFirstFlag && result;
    }

}
