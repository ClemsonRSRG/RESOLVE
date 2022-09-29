/*
 * RelevantTheoremExtractor.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.nProver.utilities.theorems;

import edu.clemson.rsrg.typeandpopulate.entry.TheoremEntry;
import edu.clemson.rsrg.typeandpopulate.query.EntryTypeQuery;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScope;

import java.util.List;

public class RelevantTheoremExtractor {

    private final ModuleScope myCurrentModuleScope;

    public RelevantTheoremExtractor(ModuleScope scope) {
        myCurrentModuleScope = scope;
    }

    public List<TheoremEntry> theoremEntryQuery() {
        List<TheoremEntry> te = null;
        te = myCurrentModuleScope.query(new EntryTypeQuery<TheoremEntry>(TheoremEntry.class,
                MathSymbolTable.ImportStrategy.IMPORT_NAMED, MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));
        System.err.println(te.size());
        return te;
    }
}
