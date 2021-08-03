/*
 * NameAndEntryTypeSearcher.java
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
package edu.clemson.cs.rsrg.typeandpopulate.query.searcher;

import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.SymbolTable;
import java.util.List;

/**
 * <p>
 * A <code>NameAndEntryTypeSearcher</code> returns entries in a
 * {@link SymbolTable} that have the
 * specified name and entry type.
 * </p>
 *
 * @param <E> The return type of the base <code>MultimatchTableSearcher</code>.
 *
 * @version 2.0
 */
public class NameAndEntryTypeSearcher<E extends SymbolTableEntry>
        implements
            MultimatchTableSearcher<E> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A class that inherits from {@link SymbolTableEntry}
     * </p>
     */
    private final Class<E> myTargetClass;

    /**
     * <p>
     * Name of the entry to be searched
     * </p>
     */
    private final String myTargetName;

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
     * This constructs a searcher that specifies a search string, an entry type
     * and a boolean flag
     * that indicates whether or not we stop after the first match.
     * </p>
     *
     * @param name Name of the entry to be searched.
     * @param targetClass A class that inherits from {@link SymbolTableEntry}.
     * @param stopAfterFirst Boolean flag that indicates if we stop after we
     *        find the first or not.
     */
    public NameAndEntryTypeSearcher(String name, Class<E> targetClass,
            boolean stopAfterFirst) {
        myTargetClass = targetClass;
        myTargetName = name;
        myStopAfterFirstFlag = stopAfterFirst;
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
    public final boolean addMatches(SymbolTable entries, List<E> matches,
            SearchContext l) {
        SymbolTableEntry match = entries.get(myTargetName);

        boolean foundOne = (match != null)
                && myTargetClass.isAssignableFrom(match.getClass());

        if (foundOne) {
            matches.add((E) match);
        }

        return myStopAfterFirstFlag && foundOne;
    }

}
