/*
 * NameQuery.java
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
package edu.clemson.cs.rsrg.typeandpopulate.query;

import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.NameSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searchpath.PossiblyQualifiedPath;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;

/**
 * <p>
 * A <code>NameQuery</code> takes a (possibly-null) qualifier and a name and
 * searches for entries
 * that match. If the qualifier is non-null, the appropriate facility or module
 * is searched. If it
 * <em>is</em> null, a search is performed using the provided
 * <code>ImportStrategy</code> and
 * <code>FacilityStrategy</code>.
 * </p>
 *
 * @version 2.0
 */
public class NameQuery extends BaseMultimatchSymbolQuery<SymbolTableEntry> {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This query searches for all entries that match the given name.
     * </p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *        facility or module.
     * @param name Name of the entry to be searched.
     * @param importStrategy The import strategy to use.
     * @param facilityStrategy The facility strategy to use.
     * @param localPriority Boolean flag that indicates whether or not local
     *        items have priority.
     */
    public NameQuery(PosSymbol qualifier, String name,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean localPriority) {
        super(new PossiblyQualifiedPath(qualifier, importStrategy,
                facilityStrategy, localPriority),
                new NameSearcher(name, false));
    }

    /**
     * <p>
     * This query searches for all entries that match the given name represented
     * as a
     * {@link PosSymbol}.
     * </p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *        facility or module.
     * @param name A name symbol of the entry to be searched.
     * @param importStrategy The import strategy to use.
     * @param facilityStrategy The facility strategy to use.
     * @param localPriority Boolean flag that indicates whether or not local
     *        items have priority.
     */
    public NameQuery(PosSymbol qualifier, PosSymbol name,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean localPriority) {
        this(qualifier, name.getName(), importStrategy, facilityStrategy,
                localPriority);
    }

    /**
     * <p>
     * This query searches for all entries that match the given name using no
     * import or facility
     * strategies and no local priority.
     * </p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *        facility or module.
     * @param name Name of the entry to be searched.
     */
    public NameQuery(PosSymbol qualifier, String name) {
        this(qualifier, name, ImportStrategy.IMPORT_NONE,
                FacilityStrategy.FACILITY_IGNORE, false);
    }

    /**
     * <p>
     * This query searches for all entries that match the given name represented
     * as a
     * {@link PosSymbol} using no import or facility strategies and no local
     * priority.
     * </p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *        facility or module.
     * @param name A name symbol of the entry to be searched.
     */
    public NameQuery(PosSymbol qualifier, PosSymbol name) {
        this(qualifier, name, ImportStrategy.IMPORT_NONE,
                FacilityStrategy.FACILITY_IGNORE, false);
    }

}
