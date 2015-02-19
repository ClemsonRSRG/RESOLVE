/**
 * NameAndEntryTypeQuery.java
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

import edu.clemson.cs.r2jt.typeandpopulate2.PossiblyQualifiedPath;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate2.searchers.NameAndEntryTypeSearcher;
import org.antlr.v4.runtime.Token;

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

    public NameAndEntryTypeQuery(Token qualifier, String name,
            Class<? extends SymbolTableEntry> entryType,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean localPriority) {
        super(new PossiblyQualifiedPath(qualifier, importStrategy,
                facilityStrategy, localPriority), new NameAndEntryTypeSearcher(
                name, entryType, false));
    }

    public NameAndEntryTypeQuery(Token qualifier, Token name,
            Class<? extends SymbolTableEntry> entryType,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean localPriority) {
        this(qualifier, name.getText(), entryType, importStrategy,
                facilityStrategy, localPriority);
    }

    public NameAndEntryTypeQuery(Token qualifier, String name,
            Class<? extends SymbolTableEntry> entryType) {
        this(qualifier, name, entryType, ImportStrategy.IMPORT_NONE,
                FacilityStrategy.FACILITY_IGNORE, false);
    }

    public NameAndEntryTypeQuery(Token qualifier, Token name,
            Class<? extends SymbolTableEntry> entryType) {
        this(qualifier, name, entryType, ImportStrategy.IMPORT_NONE,
                FacilityStrategy.FACILITY_IGNORE, false);
    }
}