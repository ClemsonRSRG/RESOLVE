/**
 * ProgramVariableQuery.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate.query;

import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramVariableEntry;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.NameSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.PossiblyQualifiedPath;
import edu.clemson.cs.r2jt.misc.Utils.Mapping;

/**
 * <p>The type of query used when looking for a variable name as found in
 * executable code.</p>
 */
public class ProgramVariableQuery
        extends
            ResultProcessingQuery<SymbolTableEntry, ProgramVariableEntry> {

    public ProgramVariableQuery(PosSymbol qualifier, PosSymbol name) {
        this(qualifier, name.getName(), name.getLocation());
    }

    public ProgramVariableQuery(PosSymbol qualifier, String name,
            Location nameLoc) {
        super(new BaseSymbolQuery<SymbolTableEntry>(new PossiblyQualifiedPath(
                qualifier, MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                MathSymbolTable.FacilityStrategy.FACILITY_IGNORE, true),
                new NameSearcher(name, true)),
                new MapToProgramVariable(nameLoc));
    }

    private static class MapToProgramVariable
            implements
                Mapping<SymbolTableEntry, ProgramVariableEntry> {

        private final Location myNameLocation;

        public MapToProgramVariable(Location l) {
            myNameLocation = l;
        }

        @Override
        public ProgramVariableEntry map(SymbolTableEntry input) {
            return input.toProgramVariableEntry(myNameLocation);
        }
    }
}
