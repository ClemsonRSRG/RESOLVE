/*
 * GenericProgramTypeSearcher.java
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
package edu.clemson.rsrg.typeandpopulate.query.searcher;

import edu.clemson.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.rsrg.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.rsrg.typeandpopulate.symboltables.SymbolTable;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * An <code>GenericProgramTypeSearcher</code> returns entries in a {@link SymbolTable} that are generic type parameters.
 * </p>
 *
 * @version 2.0
 */
public class GenericProgramTypeSearcher implements MultimatchTableSearcher<ProgramTypeEntry> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A singleton instance for this searcher.
     * </p>
     */
    public static final GenericProgramTypeSearcher INSTANCE = new GenericProgramTypeSearcher();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a searcher that searches for a generic program type.
     * </p>
     */
    private GenericProgramTypeSearcher() {
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Refines {@link TableSearcher#addMatches(SymbolTable, List, SearchContext)}} to guarantee that it will not throw a
     * {@link DuplicateSymbolException}. Otherwise, behaves identically.
     * </p>
     *
     * @param entries
     *            The set of symbol table entries to consider.
     * @param matches
     *            A non-<code>null</code> accumulator of matches.
     * @param l
     *            The context from which <code>entries</code> was drawn.
     *
     * @return <code>true</code> if <code>matches</code> now represents a final list of search results; i.e., no further
     *         symbol table entries should be considered. <code>false</code> indicates that the search should continue,
     *         provided there are additional un-searched scopes.
     */
    @Override
    public final boolean addMatches(SymbolTable entries, List<ProgramTypeEntry> matches, SearchContext l) {
        Iterator<ProgramParameterEntry> parameters = entries.iterateByType(ProgramParameterEntry.class);
        ProgramParameterEntry parameter;
        while (parameters.hasNext()) {
            parameter = parameters.next();

            if (parameter.getParameterMode().equals(ProgramParameterEntry.ParameterMode.TYPE)) {
                matches.add(parameter.toProgramTypeEntry(parameter.getDefiningElement().getLocation()));
            }
        }

        return false;
    }

}
