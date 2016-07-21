/**
 * EntryTypeSearcher.java
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
package edu.clemson.cs.rsrg.typeandpopulate.query.searcher;

import edu.clemson.cs.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.SymbolTable;
import java.util.Iterator;
import java.util.List;

/**
 * <p>An <code>EntryTypeSearcher</code> returns entries in a {@link SymbolTable}
 * that have the specified entry type.</p>
 *
 * @param <E> The return type of the base <code>MultimatchTableSearcher</code>.
 *
 * @version 2.0
 */
public class EntryTypeSearcher<E extends SymbolTableEntry> implements MultimatchTableSearcher<E> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>An {@code EntryTypeSearcher} for {@link FacilityEntry}s.</p> */
    public static final EntryTypeSearcher<FacilityEntry> FACILITY_SEARCHER =
            new EntryTypeSearcher<>(FacilityEntry.class);

    /** <p>A class that inherits from {@link SymbolTableEntry}</p> */
    private final Class<E> myTargetClass;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a searcher that specifies an symbol table
     * entry type.</p>
     *
     * @param targetClass A class that inherits from {@link SymbolTableEntry}.
     */
    public EntryTypeSearcher(Class<E> targetClass) {
        myTargetClass = targetClass;
    }

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
    public final boolean addMatches(SymbolTable entries, List<E> matches, SearchContext l) {
        Iterator<E> matchesIter = entries.iterateByType(myTargetClass);

        while (matchesIter.hasNext()) {
            matches.add(matchesIter.next());
        }

        return false;
    }

}