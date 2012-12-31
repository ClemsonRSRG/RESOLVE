/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.mathtype;

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.utilities.Mapping;

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
