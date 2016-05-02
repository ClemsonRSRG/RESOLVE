/**
 * GenericQuery.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2.query;

import edu.clemson.cs.r2jt.typeandpopulate2.MathSymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate2.UnqualifiedPath;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.ProgramTypeEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.searchers.GenericSearcher;

/**
 *
 * @author hamptos
 */
public class GenericQuery extends BaseMultimatchSymbolQuery<ProgramTypeEntry>
        implements
            MultimatchSymbolQuery<ProgramTypeEntry> {

    public static final GenericQuery INSTANCE = new GenericQuery();

    private GenericQuery() {
        super(new UnqualifiedPath(MathSymbolTable.ImportStrategy.IMPORT_NAMED,
                MathSymbolTable.FacilityStrategy.FACILITY_IGNORE, true),
                GenericSearcher.INSTANCE);
    }
}
