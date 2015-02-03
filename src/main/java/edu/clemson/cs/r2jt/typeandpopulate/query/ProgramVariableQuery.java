/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.typeandpopulate.query;

import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramVariableEntry;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.NameSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.PossiblyQualifiedPath;
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
