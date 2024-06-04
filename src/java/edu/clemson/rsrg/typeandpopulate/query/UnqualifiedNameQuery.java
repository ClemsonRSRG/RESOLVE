/*
 * UnqualifiedNameQuery.java
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
import edu.clemson.rsrg.typeandpopulate.query.searcher.NameSearcher;
import edu.clemson.rsrg.typeandpopulate.query.searchpath.UnqualifiedPath;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;

/**
 * <p>
 * An <code>UnqualifiedNameQuery</code> takes a name and searches for entries that match the given name. This query does
 * not take in a qualifier, therefore a search is performed using the provided <code>ImportStrategy</code> and
 * <code>FacilityStrategy</code>.
 * </p>
 *
 * @version 2.0
 */
public class UnqualifiedNameQuery extends BaseMultimatchSymbolQuery<SymbolTableEntry> {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This query searches for a name that is not qualified.
     * </p>
     *
     * @param searchString
     *            Name of the entry to be searched.
     * @param importStrategy
     *            The import strategy to use.
     * @param facilityStrategy
     *            The facility strategy to use.
     * @param stopAfterFirst
     *            Boolean flag that indicates if we stop after we find the first or not.
     * @param localPriority
     *            Boolean flag that indicates whether or not local items have priority.
     */
    public UnqualifiedNameQuery(String searchString, ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean stopAfterFirst, boolean localPriority) {
        super(new UnqualifiedPath(importStrategy, facilityStrategy, localPriority),
                new NameSearcher(searchString, stopAfterFirst));
    }

    /**
     * <p>
     * This query searches for a name that is not qualified.
     * </p>
     *
     * @param searchString
     *            Name of the entry to be searched.
     */
    public UnqualifiedNameQuery(String searchString) {
        this(searchString, ImportStrategy.IMPORT_NAMED, FacilityStrategy.FACILITY_INSTANTIATE, true, true);
    }

}
