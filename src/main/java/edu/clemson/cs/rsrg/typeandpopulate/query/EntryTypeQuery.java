/**
 * EntryTypeQuery.java
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
package edu.clemson.cs.rsrg.typeandpopulate.query;

import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.EntryTypeSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searchpath.UnqualifiedPath;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;

/**
 * <p>A <code>EntryTypeQuery</code> returns all type <code>T</code> symbol entries
 * using using the provided <code>ImportStrategy</code> and <code>FacilityStrategy</code>.</p>
 *
 * @version 2.0
 */
public class EntryTypeQuery<T extends SymbolTableEntry>
        extends
            BaseMultimatchSymbolQuery<T> {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This query searches for all entries of type <code>T</code>.</p>
     *
     * @param entryType The class type of the entry.
     * @param importStrategy The import strategy to use.
     * @param facilityStrategy The facility strategy to use.
     */
    public EntryTypeQuery(Class<T> entryType, ImportStrategy importStrategy, FacilityStrategy facilityStrategy) {
        super(new UnqualifiedPath(importStrategy, facilityStrategy, false), new EntryTypeSearcher<>(entryType));
    }
}