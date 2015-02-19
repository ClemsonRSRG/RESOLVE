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
package edu.clemson.cs.r2jt.typeandpopulate2.query;

import edu.clemson.cs.r2jt.misc.Utils;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate2.PossiblyQualifiedPath;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.ProgramVariableEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.searchers.NameSearcher;
import org.antlr.v4.runtime.Token;

/**
 * <p>The type of query used when looking for a variable name as found in
 * executable code.</p>
 */
public class ProgramVariableQuery
        extends
            ResultProcessingQuery<SymbolTableEntry, ProgramVariableEntry> {

    public ProgramVariableQuery(Token qualifier, Token name) {
        this(qualifier, name.getText(), name);
    }

    public ProgramVariableQuery(Token qualifier, String name, Token nameLoc) {
        super(new BaseSymbolQuery<SymbolTableEntry>(new PossiblyQualifiedPath(
                qualifier, MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                MathSymbolTable.FacilityStrategy.FACILITY_IGNORE, true),
                new NameSearcher(name, true)),
                new MapToProgramVariable(nameLoc));
    }

    private static class MapToProgramVariable
            implements
                Utils.Mapping<SymbolTableEntry, ProgramVariableEntry> {

        private final Token myNameLocation;

        public MapToProgramVariable(Token t) {
            myNameLocation = t;
        }

        @Override
        public ProgramVariableEntry map(SymbolTableEntry input) {
            return input.toProgramVariableEntry(myNameLocation);
        }
    }
}
