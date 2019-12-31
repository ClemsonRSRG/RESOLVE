/*
 * MathSymbolQuery.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.query;

import edu.clemson.cs.rsrg.misc.Utilities.Mapping;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.NameSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searchpath.PossiblyQualifiedPath;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;

/**
 * <p>
 * A <code>MathSymbolQuery</code> searches for a mathematical symbol.
 * </p>
 *
 * @version 2.0
 */
public class MathSymbolQuery
        extends
            ResultProcessingQuery<SymbolTableEntry, MathSymbolEntry> {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This query searches for a {@link MathSymbolEntry}.
     * </p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *        facility or module.
     * @param name A name symbol of the entry to be searched.
     */
    public MathSymbolQuery(PosSymbol qualifier, PosSymbol name) {
        this(qualifier, name.getName(), name.getLocation());
    }

    /**
     * <p>
     * This query searches for a {@link MathSymbolEntry}.
     * </p>
     *
     * @param qualifier A qualifier symbol that indicates the instantiating
     *        facility or module.
     * @param name Name of the entry to be searched.
     * @param nameLoc Location for the name.
     */
    public MathSymbolQuery(PosSymbol qualifier, String name, Location nameLoc) {
        super(new SimpleSymbolQuery(
                new PossiblyQualifiedPath(qualifier,
                        ImportStrategy.IMPORT_NAMED,
                        FacilityStrategy.FACILITY_INSTANTIATE, true),
                new NameSearcher(name, true)), new MapToMathSymbol(nameLoc));
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>
     * This is an implementation of a {@link Mapping} between
     * {@link SymbolTableEntry} and
     * {@link MathSymbolEntry}.
     * </p>
     */
    private static class MapToMathSymbol
            implements
                Mapping<SymbolTableEntry, MathSymbolEntry> {

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
         * This creates a conversion mapping between {@link SymbolTableEntry}
         * and
         * {@link MathSymbolEntry}.
         * </p>
         *
         * @param l Location for the name.
         */
        MapToMathSymbol(Location l) {
            myNameLocation = l;
        }

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>
         * This method converts a {@link SymbolTableEntry} into a
         * {@link MathSymbolEntry}.
         * </p>
         *
         * @param input A {@link SymbolTableEntry} object.
         *
         * @return A {@link MathSymbolEntry}.
         */
        @Override
        public final MathSymbolEntry map(SymbolTableEntry input) {
            return input.toMathSymbolEntry(myNameLocation);
        }

    }

}
