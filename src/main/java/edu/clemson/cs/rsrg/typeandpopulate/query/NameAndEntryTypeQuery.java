/*
 * NameAndEntryTypeQuery.java
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

import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.NameAndEntryTypeSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searchpath.PossiblyQualifiedPath;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;

/**
 * <p>A <code>NameAndEntryQuery</code> takes a (possibly-null) qualifier, a
 * name, and an entry type descended from <code>SymbolTableEntry</code>, and
 * searched for entries that match, disregarding any entries with the correct
 * name but incorrect type. If the qualifier is non-null, the
 * appropriate facility or module is searched. If it <em>is</em> null, a
 * search is performed using the provided <code>ImportStrategy</code> and
 * <code>FacilityStrategy</code>.</p>
 *
 * @param <E> The return type of the base <code>MultimatchSymbolQuery</code>.
 *
 * @version 2.0
 */
public class NameAndEntryTypeQuery<E extends SymbolTableEntry>
        extends
            BaseMultimatchSymbolQuery<E> {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This query searches for all entries that match the given name and
     * has type <code>T</code>.</p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *                  facility or module.
     * @param name Name of the entry to be searched.
     * @param entryType The class type of the entry.
     * @param importStrategy The import strategy to use.
     * @param facilityStrategy The facility strategy to use.
     * @param localPriority Boolean flag that indicates whether or not
     *                      local items have priority.
     */
    public NameAndEntryTypeQuery(PosSymbol qualifier, String name,
            Class<E> entryType, ImportStrategy importStrategy,
            FacilityStrategy facilityStrategy, boolean localPriority) {
        super(new PossiblyQualifiedPath(qualifier, importStrategy,
                facilityStrategy, localPriority), new NameAndEntryTypeSearcher<>(name, entryType, false));
    }

    /**
     * <p>This query searches for all entries that match the given name
     * represented as a {@link PosSymbol} and has type <code>T</code>.</p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *                  facility or module.
     * @param name A name symbol of the entry to be searched.
     * @param entryType The class type of the entry.
     * @param importStrategy The import strategy to use.
     * @param facilityStrategy The facility strategy to use.
     * @param localPriority Boolean flag that indicates whether or not
     *                      local items have priority.
     */
    public NameAndEntryTypeQuery(PosSymbol qualifier, PosSymbol name,
            Class<E> entryType, ImportStrategy importStrategy,
            FacilityStrategy facilityStrategy, boolean localPriority) {
        this(qualifier, name.getName(), entryType, importStrategy,
                facilityStrategy, localPriority);
    }

    /**
     * <p>This query searches for all entries that match the given name and
     * has type <code>T</code>, using no import or facility strategies and
     * no local priority.</p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *                  facility or module.
     * @param name Name of the entry to be searched.
     * @param entryType The class type of the entry.
     */
    public NameAndEntryTypeQuery(PosSymbol qualifier, String name,
            Class<E> entryType) {
        this(qualifier, name, entryType, ImportStrategy.IMPORT_NONE,
                FacilityStrategy.FACILITY_IGNORE, false);
    }

    /**
     * <p>This query searches for all entries that match the given name
     * represented as a {@link PosSymbol} and has type <code>T</code>,
     * using no import or facility strategies and no local priority.</p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *                  facility or module.
     * @param name A name symbol of the entry to be searched.
     * @param entryType The class type of the entry.
     */
    public NameAndEntryTypeQuery(PosSymbol qualifier, PosSymbol name,
            Class<E> entryType) {
        this(qualifier, name, entryType, ImportStrategy.IMPORT_NONE,
                FacilityStrategy.FACILITY_IGNORE, false);
    }

}