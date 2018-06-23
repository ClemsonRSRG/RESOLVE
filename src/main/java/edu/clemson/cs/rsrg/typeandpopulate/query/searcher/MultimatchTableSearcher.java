/*
 * MultimatchTableSearcher.java
 * ---------------------------------
 * Copyright (c) 2018
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
 * <p>A simple refinement on {@link TableSearcher} that guarantees
 * its method will not throw a {@link DuplicateSymbolException}.</p>
 *
 * @param <E> Permits concrete implementations of this interface to refine the
 *            type of <code>SymbolTableEntry</code> they will match. This
 *            searcher guarantees that any entry it matches will descend from
 *            <code>E</code>. Put another way: no matched entry will not be
 *            a subtype of <code>E</code>.
 *
 * @version 2.0
 */
public interface MultimatchTableSearcher<E extends SymbolTableEntry>
        extends
            TableSearcher<E> {

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
    boolean addMatches(SymbolTable entries, List<E> matches, SearchContext l);

}