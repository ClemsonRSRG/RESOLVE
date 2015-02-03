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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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

import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.NameAndEntryTypeSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.PossiblyQualifiedPath;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;

/**
 * <p>A <code>NameAndEntryQuery</code> takes a (possibly-null) qualifier, a 
 * name, and an entry type descended from <code>SymbolTableEntry</code>, and
 * searched for entries that match, disregarding any entries with the correct
 * name but incorrect type.  If the qualifier is non-null, the
 * appropriate facility or module is searched.  If it <em>is</em> null, a
 * search is performed using the provided <code>ImportStrategy</code> and
 * <code>FacilityStrategy</code>.</p>
 */
public class NameAndEntryTypeQuery
        extends
            BaseMultimatchSymbolQuery<SymbolTableEntry> {

    public NameAndEntryTypeQuery(PosSymbol qualifier, String name,
            Class<? extends SymbolTableEntry> entryType,
            MathSymbolTable.ImportStrategy importStrategy,
            MathSymbolTable.FacilityStrategy facilityStrategy,
            boolean localPriority) {
        super(new PossiblyQualifiedPath(qualifier, importStrategy,
                facilityStrategy, localPriority), new NameAndEntryTypeSearcher(
                name, entryType, false));
    }

    public NameAndEntryTypeQuery(PosSymbol qualifier, PosSymbol name,
            Class<? extends SymbolTableEntry> entryType,
            MathSymbolTable.ImportStrategy importStrategy,
            MathSymbolTable.FacilityStrategy facilityStrategy,
            boolean localPriority) {
        this(qualifier, name.getName(), entryType, importStrategy,
                facilityStrategy, localPriority);
    }

    public NameAndEntryTypeQuery(PosSymbol qualifier, String name,
            Class<? extends SymbolTableEntry> entryType) {
        this(qualifier, name, entryType,
                MathSymbolTable.ImportStrategy.IMPORT_NONE,
                MathSymbolTable.FacilityStrategy.FACILITY_IGNORE, false);
    }

    public NameAndEntryTypeQuery(PosSymbol qualifier, PosSymbol name,
            Class<? extends SymbolTableEntry> entryType) {
        this(qualifier, name, entryType,
                MathSymbolTable.ImportStrategy.IMPORT_NONE,
                MathSymbolTable.FacilityStrategy.FACILITY_IGNORE, false);
    }
}