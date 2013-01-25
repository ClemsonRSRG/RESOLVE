package edu.clemson.cs.r2jt.typeandpopulate.query;

import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.NameSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.UnqualifiedPath;

public class UnqualifiedNameQuery
        extends
            BaseMultimatchSymbolQuery<SymbolTableEntry> {

    public UnqualifiedNameQuery(String searchString,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy,
            boolean stopAfterFirst, boolean localPriority) {

        super(new UnqualifiedPath(importStrategy, facilityStrategy,
                localPriority), new NameSearcher(searchString, stopAfterFirst));
    }

    public UnqualifiedNameQuery(String searchString) {
        this(searchString, ImportStrategy.IMPORT_NONE,
                FacilityStrategy.FACILITY_INSTANTIATE, true, true);
    }
}
