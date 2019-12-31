/*
 * NameQuery.java
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
package edu.clemson.cs.r2jt.typeandpopulate.query;

import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.NameSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.PossiblyQualifiedPath;

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
 */
public class NameQuery extends BaseMultimatchSymbolQuery<SymbolTableEntry>
        implements
            MultimatchSymbolQuery<SymbolTableEntry> {

    public NameQuery(PosSymbol qualifier, String name,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean localPriority) {
        super(new PossiblyQualifiedPath(qualifier, importStrategy,
                facilityStrategy, localPriority),
                new NameSearcher(name, false));
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
