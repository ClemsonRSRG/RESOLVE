/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.typeandpopulate.searchers.EntryTypeSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.MathSymbolTable.ImportStrategy;
import edu.clemson.cs.r2jt.typeandpopulate.UnqualifiedPath;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.query.BaseMultimatchSymbolQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.MultimatchSymbolQuery;

/**
 *
 * @author hamptos
 */
public class EntryTypeQuery<T extends SymbolTableEntry>
        extends
            BaseMultimatchSymbolQuery<T> implements MultimatchSymbolQuery<T> {

    public EntryTypeQuery(Class<? extends SymbolTableEntry> entryType,
            ImportStrategy importStrategy, FacilityStrategy facilityStrategy) {
        super(new UnqualifiedPath(importStrategy, facilityStrategy, false),
                new EntryTypeSearcher(entryType));
    }
}
