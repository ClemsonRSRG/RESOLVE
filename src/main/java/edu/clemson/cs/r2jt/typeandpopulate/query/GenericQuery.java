/*
 * GenericQuery.java
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

import edu.clemson.cs.r2jt.typeandpopulate.searchers.GenericSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.UnqualifiedPath;

/**
 *
 * @author hamptos
 */
public class GenericQuery extends BaseMultimatchSymbolQuery<ProgramTypeEntry>
        implements
            MultimatchSymbolQuery<ProgramTypeEntry> {

    public static final GenericQuery INSTANCE = new GenericQuery();

    private GenericQuery() {
        super(new UnqualifiedPath(ImportStrategy.IMPORT_NAMED,
                FacilityStrategy.FACILITY_IGNORE, true),
                GenericSearcher.INSTANCE);
    }
}
