/*
 * ProgramVariableQuery.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.query;

import edu.clemson.rsrg.misc.Utilities.Mapping;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.typeandpopulate.entry.ProgramVariableEntry;
import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.rsrg.typeandpopulate.query.searcher.NameSearcher;
import edu.clemson.rsrg.typeandpopulate.query.searchpath.PossiblyQualifiedPath;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;

/**
 * <p>
 * A <code>ProgramVariableQuery</code> searches for a variable name as found in executable code.
 * </p>
 *
 * @version 2.0
 */
public class ProgramVariableQuery extends ResultProcessingQuery<SymbolTableEntry, ProgramVariableEntry> {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This query searches for a {@link ProgramVariableEntry}.
     * </p>
     *
     * @param qualifier
     *            A qualifier symbol that indicates the instantiating facility or module.
     * @param name
     *            A name symbol of the entry to be searched.
     */
    public ProgramVariableQuery(PosSymbol qualifier, PosSymbol name) {
        this(qualifier, name.getName(), name.getLocation());
    }

    /**
     * <p>
     * This query searches for a {@link ProgramVariableEntry}.
     * </p>
     *
     * @param qualifier
     *            A qualifier symbol that indicates the instantiating facility or module.
     * @param name
     *            Name of the entry to be searched.
     * @param nameLoc
     *            Location for the name.
     */
    public ProgramVariableQuery(PosSymbol qualifier, String name, Location nameLoc) {
        super(new SimpleSymbolQuery(new PossiblyQualifiedPath(qualifier, ImportStrategy.IMPORT_NAMED,
                FacilityStrategy.FACILITY_IGNORE, true), new NameSearcher(name, true)),
                new MapToProgramVariable(nameLoc));
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>
     * This is an implementation of a {@link Mapping} between {@link SymbolTableEntry} and {@link ProgramVariableEntry}.
     * </p>
     */
    private static class MapToProgramVariable implements Mapping<SymbolTableEntry, ProgramVariableEntry> {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * Location for the name.
         * </p>
         */
        private final Location myNameLocation;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * This creates a conversion mapping between {@link SymbolTableEntry} and {@link ProgramVariableEntry}.
         * </p>
         *
         * @param l
         *            Location for the name.
         */
        MapToProgramVariable(Location l) {
            myNameLocation = l;
        }

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>
         * This method converts a {@link SymbolTableEntry} into a {@link ProgramVariableEntry}.
         * </p>
         *
         * @param input
         *            A {@link SymbolTableEntry} object.
         *
         * @return A {@link ProgramVariableEntry}.
         */
        @Override
        public final ProgramVariableEntry map(SymbolTableEntry input) {
            return input.toProgramVariableEntry(myNameLocation);
        }
    }

}
