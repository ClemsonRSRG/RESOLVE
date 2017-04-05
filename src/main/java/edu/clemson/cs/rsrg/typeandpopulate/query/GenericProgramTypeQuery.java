/*
 * GenericProgramTypeQuery.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.query;

import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.GenericProgramTypeSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searchpath.UnqualifiedPath;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTable.ImportStrategy;

/**
 * <p>A <code>GenericProgramTypeQuery</code> returns all {@link ProgramTypeEntry}
 * in our current scope. The search is performed using {@link ImportStrategy#IMPORT_NAMED}
 * and {@link FacilityStrategy#FACILITY_IGNORE} using the {@link GenericProgramTypeSearcher#INSTANCE}.</p>
 *
 * @version 2.0
 */
public class GenericProgramTypeQuery
        extends
            BaseMultimatchSymbolQuery<ProgramTypeEntry> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>A singleton instance for this query.</p> */
    public static final GenericProgramTypeQuery INSTANCE =
            new GenericProgramTypeQuery();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This query searches for all the generic program types in scope.</p>
     */
    private GenericProgramTypeQuery() {
        super(new UnqualifiedPath(ImportStrategy.IMPORT_NAMED,
                FacilityStrategy.FACILITY_IGNORE, true),
                GenericProgramTypeSearcher.INSTANCE);
    }

}