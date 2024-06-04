/*
 * EntryTypeQuery.java
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
import edu.clemson.rsrg.typeandpopulate.query.searcher.EntryTypeSearcher;
import edu.clemson.rsrg.typeandpopulate.query.searchpath.UnqualifiedPath;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;

/**
 * <p>
 * A <code>EntryTypeQuery</code> returns all type <code>T</code> symbol entries using using the provided
 * <code>ImportStrategy</code> and <code>FacilityStrategy</code>.
 * </p>
 *
 * @param <E>
 *            The return type of the base <code>MultimatchSymbolQuery</code>.
 *
 * @version 2.0
 */
public class EntryTypeQuery<E extends SymbolTableEntry> extends BaseMultimatchSymbolQuery<E> {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This query searches for all entries of type <code>T</code>.
     * </p>
     *
     * @param entryType
     *            The class type of the entry.
     * @param importStrategy
     *            The import strategy to use.
     * @param facilityStrategy
     *            The facility strategy to use.
     */
    public EntryTypeQuery(Class<E> entryType, ImportStrategy importStrategy, FacilityStrategy facilityStrategy) {
        super(new UnqualifiedPath(importStrategy, facilityStrategy, false), new EntryTypeSearcher<>(entryType));
    }
}
