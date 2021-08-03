/*
 * EntryTypeQuery.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate.query;

import edu.clemson.cs.r2jt.typeandpopulate.searchers.EntryTypeSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.UnqualifiedPath;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;

/**
 *
 * @author hamptos
 */
public class EntryTypeQuery<T extends SymbolTableEntry>
        extends
            BaseMultimatchSymbolQuery<T>
        implements
            MultimatchSymbolQuery<T> {

    public EntryTypeQuery(Class<? extends SymbolTableEntry> entryType,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy) {
        super(new UnqualifiedPath(importStrategy, facilityStrategy, false),
                new EntryTypeSearcher(entryType));
    }
}
