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

import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.NameSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.PossiblyQualifiedPath;

/**
 * <p>A <code>NameQuery</code> takes a (possibly-null) qualifier and a name
 * and searches for entries that match.  If the qualifier is non-null, the
 * appropriate facility or module is searched.  If it <em>is</em> null, a
 * search is performed using the provided <code>ImportStrategy</code> and
 * <code>FacilityStrategy</code>.</p>
 */
public class NameQuery extends BaseMultimatchSymbolQuery<SymbolTableEntry>
        implements
            MultimatchSymbolQuery<SymbolTableEntry> {

    public NameQuery(PosSymbol qualifier, String name,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean localPriority) {
        super(new PossiblyQualifiedPath(qualifier, importStrategy,
                facilityStrategy, localPriority), new NameSearcher(name, false));
    }

    public NameQuery(PosSymbol qualifier, PosSymbol name,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean localPriority) {
        this(qualifier, name.getName(), importStrategy, facilityStrategy,
                localPriority);
    }

    public NameQuery(PosSymbol qualifier, String name) {
        this(qualifier, name, ImportStrategy.IMPORT_NONE,
                FacilityStrategy.FACILITY_IGNORE, false);
    }

    public NameQuery(PosSymbol qualifier, PosSymbol name) {
        this(qualifier, name, ImportStrategy.IMPORT_NONE,
                FacilityStrategy.FACILITY_IGNORE, false);
    }
}
